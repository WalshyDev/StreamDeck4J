package com.walshydev.streamdeck4j.events;

import javax.annotation.Nullable;

public class DeviceConnectedEvent extends Event {

    private final String deviceId;

    public DeviceConnectedEvent(@Nullable String context, String deviceId) {
        super(context);
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
