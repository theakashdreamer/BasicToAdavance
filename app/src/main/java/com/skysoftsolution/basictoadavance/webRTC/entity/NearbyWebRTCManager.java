package com.skysoftsolution.basictoadavance.webRTC.entity;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.*;

import org.webrtc.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NearbyWebRTCManager {
    private ConnectionsClient connectionsClient;
    private String endpointId;
    private DataChannel dataChannel;
    private PeerConnection peerConnection;
    private PeerConnectionFactory factory;
    private NearbyListener listener;
    private Map<String, String> discoveredDevices = new HashMap<>(); // Store (endpointId, deviceName)
    private DeviceDiscoveryListener discoveryListener;

    public interface NearbyListener {
        void onMessageReceived(String message);
    }

    public NearbyWebRTCManager(Context context, NearbyListener listener) {
        this.listener = listener;
        connectionsClient = Nearby.getConnectionsClient(context);

        PeerConnectionFactory.InitializationOptions options =
                PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions();
        PeerConnectionFactory.initialize(options);

        factory = PeerConnectionFactory.builder().createPeerConnectionFactory();

        PeerConnection.RTCConfiguration config = new PeerConnection.RTCConfiguration(new ArrayList<>());
        peerConnection = factory.createPeerConnection(config, peerConnectionObserver);
    }

//    public void startDiscovery() {
//        connectionsClient.startDiscovery(
//                "com.skysoftsolution.basictoadavance",
//                new EndpointDiscoveryCallback() {
//                    @Override
//                    public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
//                        connectionsClient.requestConnection("Device", endpointId, connectionLifecycleCallback);
//                    }
//
//                    @Override
//                    public void onEndpointLost(String endpointId) {
//                    }
//                },
//                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
//        );
//    }

    public void startAdvertising() {
        Log.d("NearbyWebRTCManager", "📢 Advertising started...");

        connectionsClient.startAdvertising(
                "DeviceName",
                "com.skysoftsolution.basictoadavance", // MUST match with startDiscovery()
                new ConnectionLifecycleCallback() {
                    @Override
                    public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                        Log.d("NearbyWebRTCManager", "🔄 Connection initiated with: " + endpointId);
                        connectionsClient.acceptConnection(endpointId, payloadCallback);
                    }

                    @Override
                    public void onConnectionResult(String endpointId, ConnectionResolution result) {
                        if (result.getStatus().isSuccess()) {
                            Log.d("NearbyWebRTCManager", "✅ Connected to: " + endpointId);
                        } else {
                            Log.e("NearbyWebRTCManager", "❌ Connection failed: " + result.getStatus().getStatusMessage());
                        }
                    }

                    @Override
                    public void onDisconnected(String endpointId) {
                        Log.d("NearbyWebRTCManager", "🔌 Disconnected from: " + endpointId);
                    }
                },
                new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        );
    }



    public interface DeviceDiscoveryListener {
        void onDeviceDiscovered(String endpointId, String deviceName);
    }

    public NearbyWebRTCManager(Context context, DeviceDiscoveryListener listener) {
        this.discoveryListener = listener;
        connectionsClient = Nearby.getConnectionsClient(context);
    }
    public void startDiscovery() {
        Log.d("NearbyWebRTCManager", "🚀 Discovery started...");

        connectionsClient.startDiscovery(
                "com.skysoftsolution.basictoadavance",
                new EndpointDiscoveryCallback() {
                    @Override
                    public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                        Log.d("NearbyWebRTCManager", "✅ Device found: " + info.getEndpointName());
                        discoveredDevices.put(endpointId, info.getEndpointName());

                        if (discoveryListener != null) {
                            discoveryListener.onDeviceDiscovered(endpointId, info.getEndpointName());
                        }
                    }

                    @Override
                    public void onEndpointLost(String endpointId) {
                        Log.d("NearbyWebRTCManager", "⚠️ Device lost: " + endpointId);
                        discoveredDevices.remove(endpointId);
                    }
                },
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        );
    }



    public void requestConnection(String endpointId) {
        Log.d("NearbyWebRTCManager", "📡 Requesting connection to: " + endpointId);

        connectionsClient.requestConnection("Device", endpointId, new ConnectionLifecycleCallback() {
            @Override
            public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                Log.d("NearbyWebRTCManager", "🔄 Connection initiated with: " + endpointId);
                connectionsClient.acceptConnection(endpointId, payloadCallback);
            }

            @Override
            public void onConnectionResult(String endpointId, ConnectionResolution result) {
                if (result.getStatus().isSuccess()) {
                    Log.d("NearbyWebRTCManager", "✅ Connected to: " + endpointId);
                } else {
                    Log.e("NearbyWebRTCManager", "❌ Connection failed: " + result.getStatus().getStatusMessage());
                }
            }

            @Override
            public void onDisconnected(String endpointId) {
                Log.d("NearbyWebRTCManager", "🔌 Disconnected from: " + endpointId);
            }
        });
    }



    public Map<String, String> getDiscoveredDevices() {
        return discoveredDevices;
    }
    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
            connectionsClient.acceptConnection(endpointId, payloadCallback);
        }

        @Override
        public void onConnectionResult(String endpointId, ConnectionResolution result) {
            if (result.getStatus().isSuccess()) {
                NearbyWebRTCManager.this.endpointId = endpointId;
            }
        }

        @Override
        public void onDisconnected(String endpointId) {
        }
    };

    private final PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(String endpointId, Payload payload) {
            String receivedMessage = new String(payload.asBytes(), StandardCharsets.UTF_8);
            listener.onMessageReceived(receivedMessage);
        }

        @Override
        public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
        }
    };

    public void sendMessage(String message) {
        if (endpointId != null) {
            Payload payload = Payload.fromBytes(message.getBytes(StandardCharsets.UTF_8));
            connectionsClient.sendPayload(endpointId, payload);
        }
    }

    private final PeerConnection.Observer peerConnectionObserver = new PeerConnection.Observer() {
        @Override
        public void onDataChannel(DataChannel channel) {
            dataChannel = channel;
        }

        @Override
        public void onIceCandidate(IceCandidate candidate) {
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

        }

        @Override
        public void onAddStream(MediaStream mediaStream) {

        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {

        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState state) {
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState state) {
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState state) {
        }

        @Override
        public void onRenegotiationNeeded() {
        }

        @Override
        public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {

        }
    };
}