package com.walshydev.streamdeck4j;

import com.google.gson.JsonObject;
import com.walshydev.streamdeck4j.events.ActionAppearedEvent;
import com.walshydev.streamdeck4j.events.KeyDownEvent;
import com.walshydev.streamdeck4j.hooks.AbstractListener;
import com.walshydev.streamdeck4j.info.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SD4JMain {

    private static final Logger logger = LoggerFactory.getLogger(SD4JMain.class);
    private static StreamDeck4J streamDeck4J;

    public static void main(String[] args) {
        logger.info("Starting main test app for StreamDeck4J");
        streamDeck4J = new StreamDeck4J();

        streamDeck4J.addListener(new AbstractListener() {
            @Override
            public void onActionAppeared(ActionAppearedEvent event) {
                streamDeck4J.setTitle(
                    event.getContext(),
                    "Made with\nStreamDeck4J",
                    Destination.HARDWARE_AND_SOFTWARE
                );
            }

            @Override
            public void onKeyDown(KeyDownEvent event) {
                runTests();
            }
        });

        logger.info("Connecting!");
        streamDeck4J.connect(args);
    }

    private static void runTests() {
        logger.trace("---------------------------------------");
        logger.trace("Debug: Calling `setGlobalSettings`");
        JsonObject data = new JsonObject();
        data.addProperty("test", 123);

        streamDeck4J.setGlobalSettings(data);
        logger.trace("----------------------------------------");

        logger.trace("---------------------------------------");
        logger.trace("Debug: Calling `getGlobalSettings`");
        streamDeck4J.getGlobalSettings();
        logger.trace("----------------------------------------");
    }
}
