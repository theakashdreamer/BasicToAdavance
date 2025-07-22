package com.skysoftsolution.basictoadavance.webRTC;
import android.util.Log;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.SessionDescription;

public class CustomPeerConnectionObserver implements PeerConnection.Observer {
    private static final String TAG = "PeerConnectionObserver";

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Log.d(TAG, "Signaling state changed: " + signalingState);
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        Log.d(TAG, "ICE Connection state changed: " + iceConnectionState);
    }

    @Override
    public void onIceConnectionReceivingChange(boolean receiving) {
        Log.d(TAG, "ICE Connection receiving change: " + receiving);
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Log.d(TAG, "ICE Gathering state changed: " + iceGatheringState);
    }

    @Override
    public void onIceCandidate(IceCandidate candidate) {
        Log.d(TAG, "New ICE Candidate: " + candidate.sdp);
        // Handle ICE candidate sending (e.g., send via Bluetooth)
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] candidates) {
        Log.d(TAG, "ICE Candidates removed");
    }

    @Override
    public void onAddStream(org.webrtc.MediaStream mediaStream) {
        Log.d(TAG, "Stream added: " + mediaStream.getId());
    }

    @Override
    public void onRemoveStream(org.webrtc.MediaStream mediaStream) {
        Log.d(TAG, "Stream removed: " + mediaStream.getId());
    }

    @Override
    public void onDataChannel(org.webrtc.DataChannel dataChannel) {
        Log.d(TAG, "DataChannel opened: " + dataChannel.label());
    }

    @Override
    public void onRenegotiationNeeded() {
        Log.d(TAG, "Renegotiation needed");
    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {

    }

    @Override
    public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
        Log.d(TAG, "Connection state changed: " + newState);
    }

    @Override
    public void onTrack(org.webrtc.RtpTransceiver transceiver) {
        Log.d(TAG, "New Track received");
    }
}

