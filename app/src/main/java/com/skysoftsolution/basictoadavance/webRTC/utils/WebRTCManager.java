package com.skysoftsolution.basictoadavance.webRTC.utils;

import android.content.Context;

import com.skysoftsolution.basictoadavance.webRTC.utils.NearbyShareHelper;

import org.webrtc.*;
import java.util.ArrayList;
import java.util.List;

public class WebRTCManager {
    private PeerConnection peerConnection;
    private PeerConnectionFactory factory;
    private EglBase eglBase;
    private NearbyShareHelper nearbyHelper;
    private String connectedEndpointId;

    public WebRTCManager(Context context, NearbyShareHelper nearbyHelper) {
        this.nearbyHelper = nearbyHelper;
        this.eglBase = EglBase.create();

        // Initialize PeerConnectionFactory
        PeerConnectionFactory.InitializationOptions options =
                PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions();
        PeerConnectionFactory.initialize(options);
        factory = PeerConnectionFactory.builder().createPeerConnectionFactory();

        // Add STUN servers for proper ICE candidate exchange
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());

        PeerConnection.RTCConfiguration config = new PeerConnection.RTCConfiguration(iceServers);
        peerConnection = factory.createPeerConnection(config, peerConnectionObserver);
    }

    // Set connected endpoint ID when a connection is established
    public void setConnectedEndpointId(String endpointId) {
        this.connectedEndpointId = endpointId;
    }

    public void createOffer() {
        MediaConstraints constraints = new MediaConstraints();
        peerConnection.createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                peerConnection.setLocalDescription(new SdpObserver() {
                    @Override
                    public void onSetSuccess() {
                        nearbyHelper.sendPayload(connectedEndpointId, "OFFER:" + sessionDescription.description);
                    }

                    @Override public void onSetFailure(String error) {}
                    @Override public void onCreateSuccess(SessionDescription sessionDescription) {}
                    @Override public void onCreateFailure(String error) {}
                }, sessionDescription);
            }

            @Override public void onSetSuccess() {}
            @Override public void onCreateFailure(String error) {}
            @Override public void onSetFailure(String error) {}
        }, constraints);
    }

    public void handleReceivedSDP(String sdpMessage) {
        if (sdpMessage.startsWith("OFFER:")) {
            SessionDescription remoteSdp = new SessionDescription(SessionDescription.Type.OFFER, sdpMessage.substring(6));
            peerConnection.setRemoteDescription(new SdpObserver() {
                @Override
                public void onSetSuccess() {
                    createAnswer();
                }

                @Override public void onSetFailure(String error) {}
                @Override public void onCreateSuccess(SessionDescription sessionDescription) {}
                @Override public void onCreateFailure(String error) {}
            }, remoteSdp);
        } else if (sdpMessage.startsWith("ANSWER:")) {
            SessionDescription remoteSdp = new SessionDescription(SessionDescription.Type.ANSWER, sdpMessage.substring(7));
            peerConnection.setRemoteDescription(new SdpObserver() {
                @Override public void onSetSuccess() {}
                @Override public void onSetFailure(String error) {}
                @Override public void onCreateSuccess(SessionDescription sessionDescription) {}
                @Override public void onCreateFailure(String error) {}
            }, remoteSdp);
        }
    }

    public void createAnswer() {
        MediaConstraints constraints = new MediaConstraints();
        peerConnection.createAnswer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                peerConnection.setLocalDescription(new SdpObserver() {
                    @Override
                    public void onSetSuccess() {
                        nearbyHelper.sendPayload(connectedEndpointId, "ANSWER:" + sessionDescription.description);
                    }

                    @Override public void onSetFailure(String error) {}
                    @Override public void onCreateSuccess(SessionDescription sessionDescription) {}
                    @Override public void onCreateFailure(String error) {}
                }, sessionDescription);
            }

            @Override public void onSetSuccess() {}
            @Override public void onCreateFailure(String error) {}
            @Override public void onSetFailure(String error) {}
        }, constraints);
    }

    // Handling ICE Candidates
    public void handleReceivedIceCandidate(String iceMessage) {
        if (iceMessage.startsWith("ICE:")) {
            String iceData = iceMessage.substring(4);
            String[] parts = iceData.split(",");
            if (parts.length == 3) {
                IceCandidate candidate = new IceCandidate(parts[0], Integer.parseInt(parts[1]), parts[2]);
                peerConnection.addIceCandidate(candidate);
            }
        }
    }

    private final PeerConnection.Observer peerConnectionObserver = new PeerConnection.Observer() {
        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            String candidateMessage = "ICE:" + iceCandidate.sdpMid + "," + iceCandidate.sdpMLineIndex + "," + iceCandidate.sdp;
            nearbyHelper.sendPayload(connectedEndpointId, candidateMessage);
        }

        @Override public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {}
        @Override public void onSignalingChange(PeerConnection.SignalingState signalingState) {}
        @Override public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {}
        @Override public void onIceConnectionReceivingChange(boolean b) {}
        @Override public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {}
        @Override public void onAddStream(MediaStream mediaStream) {}
        @Override public void onRemoveStream(MediaStream mediaStream) {}
        @Override public void onDataChannel(DataChannel dataChannel) {}
        @Override public void onRenegotiationNeeded() {}
        @Override public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {}
    };
}
