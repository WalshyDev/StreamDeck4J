package com.walshydev.streamdeck4j.events;

import com.walshydev.streamdeck4j.Plugin;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class ApplicationTerminatedEvent extends Event {

    private final String applicationName;

    public ApplicationTerminatedEvent(
        @Nonnull Plugin plugin,
        @Nonnull String applicationName
    ) {
        super(plugin, null);
        this.applicationName = applicationName;
    }
}
