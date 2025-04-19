package com.skysoftsolution.basictoadavance.webRTC.callBacks;

 public interface SignalingInterface {
    void sendOffer(String offer);
    void sendAnswer(String answer);
    void sendIceCandidate(String candidate);
}
