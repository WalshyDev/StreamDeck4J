package com.walshydev.streamdeck4j.events;

import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class ApplicationLaunchedEvent extends Event {

    private final String applicationName;

    public ApplicationLaunchedEvent(
        @Nonnull String applicationName
    ) {
        super(null);
        this.applicationName = applicationName;
    }

}
