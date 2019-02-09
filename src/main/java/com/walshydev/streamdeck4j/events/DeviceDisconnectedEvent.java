package com.walshydev.streamdeck4j.events;

import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class DeviceDisconnectedEvent extends Event {

    private final String deviceId;

    public DeviceDisconnectedEvent(
        @Nonnull String deviceId
    ) {
        super(null);
        this.deviceId = deviceId;
    }

}
