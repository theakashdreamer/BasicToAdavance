package com.skysoftsolution.basictoadavance.webRTC.utils;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

public class NearbyShareHelper {
    private final ConnectionsClient connectionsClient;
    private static final String SERVICE_ID = "com.skysoftsolution.basictoadavance";
    private final String deviceName = "WebRTC_Device";

    public NearbyShareHelper(Context context) {
        connectionsClient = Nearby.getConnectionsClient(context);
    }

    public void startAdvertising() {
        connectionsClient.startAdvertising(
                deviceName,
                SERVICE_ID,
                connectionLifecycleCallback,
                new com.google.android.gms.nearby.connection.AdvertisingOptions.Builder()
                        .setStrategy(Strategy.P2P_CLUSTER)
                        .build()
        );
    }

    public void startDiscovery() {
        connectionsClient.startDiscovery(
                SERVICE_ID,
                new com.google.android.gms.nearby.connection.EndpointDiscoveryCallback() {
                    @Override
                    public void onEndpointFound(String endpointId, com.google.android.gms.nearby.connection.DiscoveredEndpointInfo discoveredEndpointInfo) {
                        connectionsClient.requestConnection(deviceName, endpointId, connectionLifecycleCallback);
                    }

                    @Override
                    public void onEndpointLost(String endpointId) {
                        Log.d("NearbyShare", "Endpoint lost: " + endpointId);
                    }
                },
                new com.google.android.gms.nearby.connection.DiscoveryOptions.Builder()
                        .setStrategy(Strategy.P2P_CLUSTER)
                        .build()
        );
    }

    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
            connectionsClient.acceptConnection(endpointId, payloadCallback);
        }

        @Override
        public void onConnectionResult(String endpointId, ConnectionResolution resolution) {
            if (resolution.getStatus().isSuccess()) {
                Log.d("NearbyShare", "Connected to: " + endpointId);
            }
        }

        @Override
        public void onDisconnected(String endpointId) {
            Log.d("NearbyShare", "Disconnected from: " + endpointId);
        }
    };

    private final PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(String endpointId, Payload payload) {
            String receivedData = new String(payload.asBytes());
            Log.d("NearbyShare", "Received data: " + receivedData);
        }

        @Override
        public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
            // Handle transfer updates
        }
    };

    public void sendPayload(String endpointId, String message) {
        Payload payload = Payload.fromBytes(message.getBytes());
        connectionsClient.sendPayload(endpointId, payload);
    }
}
