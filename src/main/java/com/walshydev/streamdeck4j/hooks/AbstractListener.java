package com.walshydev.streamdeck4j.hooks;

// TODO: Document

import com.walshydev.streamdeck4j.events.*;
import org.slf4j.LoggerFactory;

public abstract class AbstractListener implements EventListener {

    public void onDeviceConnected(DeviceConnectedEvent event) {
    }

    public void onActionAppeared(ActionAppearedEvent event) {
    }

    public void onActionDisappeared(ActionDisappearedEvent event) {
    }

    public void onKeyDown(KeyDownEvent event) {
    }

    public void onKeyUp(KeyUpEvent event) {
    }

    public void onDidReceiveSettings(DidReceiveSettingsEvent event) {
    }

    public void onDidReceiveGlobalSettings(DidReceiveGlobalSettingsEvent event) {
    }

    public void onPropertyInspectorDidAppear(PropertyInspectorDidAppearEvent event) {
    }

    public void onPropertyInspectorDidDisappear(PropertyInspectorDidDisappearEvent event) {
    }

    @Override
    public final void onEvent(Event event) {
        if (event instanceof DeviceConnectedEvent)
            onDeviceConnected((DeviceConnectedEvent) event);
        else if (event instanceof ActionAppearedEvent)
            onActionAppeared((ActionAppearedEvent) event);
        else if (event instanceof ActionDisappearedEvent)
            onActionDisappeared((ActionDisappearedEvent) event);
        else if (event instanceof KeyDownEvent)
            onKeyDown((KeyDownEvent) event);
        else if (event instanceof KeyUpEvent)
            onKeyUp((KeyUpEvent) event);
        else if (event instanceof DidReceiveSettingsEvent)
            onDidReceiveSettings((DidReceiveSettingsEvent) event);
        else if(event instanceof DidReceiveGlobalSettingsEvent)
            onDidReceiveGlobalSettings((DidReceiveGlobalSettingsEvent) event);
        else if (event instanceof PropertyInspectorDidAppearEvent)
            onPropertyInspectorDidAppear((PropertyInspectorDidAppearEvent) event);
        else if(event instanceof PropertyInspectorDidDisappearEvent)
            onPropertyInspectorDidDisappear((PropertyInspectorDidDisappearEvent) event);
        else
            LoggerFactory.getLogger(AbstractListener.class)
                .error("Unknown event was thrown! {}", event.getClass().getSimpleName());
    }
}
