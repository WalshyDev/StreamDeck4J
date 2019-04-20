package com.walshydev.streamdeck4j;

import com.google.gson.JsonObject;
import com.walshydev.streamdeck4j.events.ActionAppearedEvent;
import com.walshydev.streamdeck4j.events.KeyDownEvent;
import com.walshydev.streamdeck4j.hooks.AbstractListener;
import com.walshydev.streamdeck4j.info.Destination;
import com.walshydev.streamdeck4j.utils.SD4JLogger;
import org.slf4j.Logger;

public class SD4JMain {

    private static final Logger logger = SD4JLogger.getLog(SD4JMain.class);
    private static Plugin api;

    public static void main(String[] args) {
        logger.info("Starting main test app for StreamDeck4J");
        api = new PluginBuilder(args)
            .registerListener(new AbstractListener() {
                @Override
                public void onActionAppeared(ActionAppearedEvent event) {
                    event.getPlugin().setTitle(
                        event.getContext(),
                        "Testing123\n" + System.currentTimeMillis(),
                        Destination.HARDWARE_AND_SOFTWARE
                    );
                }

                @Override
                public void onKeyDown(KeyDownEvent event) {
                    runTests();
                }
            })
            .buildAsync();

        logger.info("Using StreamDeck version: {}", api.getApplication().getVersion());
    }

    private static void runTests() {
        logger.trace("---------------------------------------");
        logger.trace("Debug: Calling `setGlobalSettings`");
        JsonObject data = new JsonObject();
        data.addProperty("test", 123);

        api.setGlobalSettings(data);
        logger.trace("----------------------------------------");

        logger.trace("---------------------------------------");
        logger.trace("Debug: Calling `getGlobalSettings`");
        api.getGlobalSettings();
        logger.trace("----------------------------------------");
    }
}
