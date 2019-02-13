package com.walshydev.streamdeck4j;

import com.google.gson.JsonObject;
import com.walshydev.streamdeck4j.hooks.EventListener;
import com.walshydev.streamdeck4j.info.Application;
import com.walshydev.streamdeck4j.info.Destination;
import com.walshydev.streamdeck4j.info.Device;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Set;

@SuppressWarnings({"unused", "WeakerAccess", "SameParameterValue"})
public interface Plugin {

    /**
     * Adds an event listener to the plugin
     *
     * @param eventListener The listener to add
     */
    void addListener(@Nonnull EventListener eventListener);

    /**
     * Get the {@link Application} info, this contains the language, platform and version being used.
     *
     * @return {@link Application} containing application info.
     */
    Application getApplication();

    /**
     * A collection of the {@link Device}s being used.
     *
     * @return A collection of the {@link Device}s being used.
     */
    Set<Device> getDevices();

    /**
     * This can be used to see if the Stream Deck application is running
     * on a HiDPI screen
     */
    int getDevicePixelRatio();

    /**
     * Get the plugin UUID.
     *
     * @return The plugin UUID passed into the program arguments.
     */
    String getPluginUUID();

    /**
     * Opens a URL on the PC the Stream Deck is connected to.
     *
     * @param url The URL to open
     */
    void openURL(@Nonnull URL url);

    /**
     * Sets the title of a specific button on the Stream Deck
     *
     * @param context     The unique identifier for the button
     * @param title       The new title for the button
     * @param destination The destination for the event (Hardware, Software or Both)
     */
    void setTitle(@Nonnull String context, @Nonnull String title, @Nonnull Destination destination);

    /**
     * Sets the image of a specific button on the Stream Deck
     *
     * @param context     The unique identifier for the button
     * @param image       The image to display on the button
     * @param destination The destination for the event (Hardware, Software or Both)
     */
    void setImage(@Nonnull String context, BufferedImage image, Destination destination);

    /**
     * Sets the image of a specific button on the Stream Deck
     *
     * @param context     The unique identifier for the button
     * @param image       The image to display on the button
     * @param type        The image's file type (jpg, png, etc.)
     * @param destination The destination for the event (Hardware, Software or Both)
     */
    void setImage(@Nonnull String context, BufferedImage image, String type, Destination destination);

    /**
     * Sets the image of a specific button on the Stream Deck Note that the Base64 string of the image does not require
     * the 'data:image/{type};base64' at the beginning of it. This is appended in the function itself.
     *
     * @param context       The unique identifier of the button
     * @param base64Encoded The Base64-encoded string of the image to display on the button
     * @param type          The image's file type (jpg, png, etc.)
     * @param destination   The destination for the event (Hardware, Software or Both)
     */
    void setImage(@Nonnull String context, String base64Encoded, String type, Destination destination);

    /**
     * Shows an alert on the Stream Deck
     *
     * @param context The unique identifier of the button to show the alert on.
     */
    void showAlert(@Nonnull String context);

    /**
     * Shows a checkmark on a specific button on the Stream Deck
     *
     * @param context The unique identifier of the button to show the checkmark on
     */
    void showOk(@Nonnull String context);

    /**
     * Gets the persistent data of an instance of an action.
     *
     * @param context The unique identifier of the button with the action you want to grab data for.
     *
     * @since StreamDeck 4.1
     */
    void getSettings(@Nonnull String context);

    /**
     * Gets the persistent global settings
     *
     * @since StreamDeck 4.1
     */
    void getGlobalSettings();

    /**
     * Sets the persistent global settings
     *
     * @param dataToSave The JSON data to save to the global settings
     * @since StreamDeck 4.1
     */
    void setGlobalSettings(@Nonnull JsonObject dataToSave);

    /**
     * Saves persistent data for the instance of the action. This data is found through events such as keyDown, keyUp,
     * willAppear, etc. You can get it by calling `getSettings()` on the event inside an EventListener. ELI5: saves some
     * data to the stream deck so if you restart it, it's still there
     *
     * @param context    The unique identifier of the button with the action you want to change data for
     * @param dataToSave The data you want to save (in JSON-format)
     */
    void setSettings(@Nonnull String context, @Nonnull JsonObject dataToSave);

    /**
     * Allows you to change the state of an action that has multiple states
     *
     * @param context The unique identifier of the button with the action you want to change
     * @param state   A 0-based integer for the state (0, 1, 2, etc.)
     */
    void setState(@Nonnull String context, int state);

    /**
     * Sends an action to the Property Inspector.
     *
     * @param context The unique identifier of the button with the action you want to send.
     * @param action  The unique identifier of the action you want to send.
     * @param payload The JSON object that will be received by the Property Inspector.
     */
    void sendToPropertyInspector(@Nonnull String context, @Nonnull String action, @Nonnull JsonObject payload);

    /**
     * Changes to a profile on the Stream Deck based on the profile's name
     *
     * @param deviceId The unique ID for the Stream Deck. This value changes each time you relaunch the Stream Deck
     *                 app.
     * @param profile  The name of the profile you want to switch to.
     */
    void switchToProfile(@Nonnull String deviceId, @Nonnull String profile);

    /**
     * Writes a debug message to the logs file.
     *
     * <p>
     * Note that logging is disabled by default. To enable logging, maintain the alt/option key down while opening the
     * tray menu/menubar and enable Debug Logs. Future logs will be saved to disk in the folder
     * {@code ~/Library/Logs/StreamDeck/} on macOS and {@code %appdata%\Roaming\Elgato\StreamDeck\logs\} on Windows.
     * Note that the log files are rotated each time the Stream Deck application is relaunched.
     * </p>
     *
     * @param message The message to send to the logs file.
     *
     * @since StreamDeck 4.1
     */
    void logMessage(@Nonnull String message);
}
