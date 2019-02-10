package com.walshydev.streamdeck4j.events;

import com.walshydev.streamdeck4j.Plugin;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class DeviceDisconnectedEvent extends Event {

    private final String deviceId;

    public DeviceDisconnectedEvent(
        @Nonnull Plugin plugin,
        @Nonnull String deviceId
    ) {
        super(plugin, null);
        this.deviceId = deviceId;
    }

}
