package com.walshydev.streamdeck4j.events;

import javax.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public abstract class Event {

    private final String context;

    public Event(@Nullable String context) {
        this.context = context;
    }

    public String getContext() {
        return this.context;
    }
}
