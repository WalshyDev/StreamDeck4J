package com.walshydev.streamdeck4j.events;

import com.walshydev.streamdeck4j.Plugin;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class ApplicationLaunchedEvent extends Event {

    private final String applicationName;

    public ApplicationLaunchedEvent(
        @Nonnull Plugin plugin,
        @Nonnull String applicationName
    ) {
        super(plugin, null);
        this.applicationName = applicationName;
    }

}
