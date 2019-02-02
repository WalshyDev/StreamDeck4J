package com.walshydev.streamdeck4j.hooks;

import com.walshydev.streamdeck4j.events.Event;

// TODO: Document

@FunctionalInterface
public interface EventListener {

    void onEvent(Event event);
}
