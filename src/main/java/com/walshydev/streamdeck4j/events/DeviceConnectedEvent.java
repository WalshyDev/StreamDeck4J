package com.walshydev.streamdeck4j.events;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeviceConnectedEvent extends Event {

    @Nonnull
    private final String deviceId;

    public DeviceConnectedEvent(@Nonnull String deviceId) {
        super(null);
        this.deviceId = deviceId;
    }

    @Nonnull
    public String getDeviceId() {
        return deviceId;
    }
}
