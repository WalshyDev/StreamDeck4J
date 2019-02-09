package com.walshydev.streamdeck4j.events;

import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class ApplicationTerminatedEvent extends Event {

    private final String applicationName;

    public ApplicationTerminatedEvent(
            @Nonnull String applicationName
    ) {
        super(null);
        this.applicationName = applicationName;
    }
}
