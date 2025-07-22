package com.skysoftsolution.basictoadavance.webRTC.entity;

public class DeviceItem {
    private String endpointId;
    private String deviceName;

    public DeviceItem(String endpointId, String deviceName) {
        this.endpointId = endpointId;
        this.deviceName = deviceName;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public String getDeviceName() {
        return deviceName;
    }
}

