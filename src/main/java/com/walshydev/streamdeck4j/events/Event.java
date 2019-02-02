package com.walshydev.streamdeck4j.events;

import javax.annotation.Nullable;

public abstract class Event {

    @Nullable
    private final String context;

    protected Event(@Nullable String context) {
        this.context = context;
    }

    @Nullable
    public String getContext() {
        return this.context;
    }
}
