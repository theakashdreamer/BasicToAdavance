package com.skysoftsolution.basictoadavance.webRTC.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessage {
    private String message;
    private boolean isSentByMe;
    private String timestamp; // New field for message time

    public ChatMessage(String message, boolean isSentByMe) {
        this.message = message;
        this.isSentByMe = isSentByMe;
        this.timestamp = getCurrentTimestamp();
    }

    public String getMessage() {
        return message;
    }

    public boolean isSentByMe() {
        return isSentByMe;
    }

    public String getTimestamp() {
        return timestamp;
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date());
    }
}
