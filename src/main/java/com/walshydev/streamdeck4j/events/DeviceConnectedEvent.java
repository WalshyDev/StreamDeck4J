package com.walshydev.streamdeck4j.events;

import com.walshydev.streamdeck4j.Plugin;
import com.walshydev.streamdeck4j.info.Device;

import javax.annotation.Nonnull;

public class DeviceConnectedEvent extends Event {

    @Nonnull
    private final String deviceId;
    @Nonnull
    private final Device device;

    public DeviceConnectedEvent(@Nonnull Plugin plugin, @Nonnull String deviceId, @Nonnull Device device) {
        super(plugin, null);
        this.deviceId = deviceId;
        this.device = device;
    }

    @Nonnull
    public String getDeviceId() {
        return this.deviceId;
    }

    @Nonnull
    public Device getDevice() {
        return device;
    }
}
