package com.walshydev.streamdeck4j.events;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeviceConnectedEvent extends Event {

    @Nonnull
    private final String deviceId;

    public DeviceConnectedEvent(@Nullable String context, @Nonnull String deviceId) {
        super(context);
        this.deviceId = deviceId;
    }

    @Nonnull
    public String getDeviceId() {
        return deviceId;
    }
}
