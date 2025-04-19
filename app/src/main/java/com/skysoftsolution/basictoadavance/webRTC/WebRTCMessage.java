package com.skysoftsolution.basictoadavance.webRTC;
import com.google.gson.Gson;

public class WebRTCMessage {
    public String type;        // "offer", "answer", "ice"
    public String to;          // Target device ID
    public String sdp;         // SDP (Session Description)
    public String iceCandidate; // ICE candidate data

    // Constructor
    public WebRTCMessage(String type, String to, String data) {
        this.type = type;
        this.to = to;
        if (type.equals("ice")) {
            this.iceCandidate = data;
        } else {
            this.sdp = data;
        }
    }

    // Convert object to JSON
    public String toJson() {
        return new Gson().toJson(this);
    }

    // Convert JSON string to WebRTCMessage object
    public static WebRTCMessage fromJson(String json) {
        return new Gson().fromJson(json, WebRTCMessage.class);
    }
}
