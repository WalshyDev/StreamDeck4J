package com.walshydev.streamdeck4j.events;

import com.walshydev.streamdeck4j.Plugin;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class PropertyInspectorDidDisappearEvent extends Event {

    private final String action;
    private final String deviceId;

    public PropertyInspectorDidDisappearEvent(
        @Nonnull Plugin plugin,
        @Nonnull String context,
        @Nonnull String action,
        @Nonnull String deviceId
    ) {
        super(plugin, context);
        this.action = action;
        this.deviceId = deviceId;
    }
}
