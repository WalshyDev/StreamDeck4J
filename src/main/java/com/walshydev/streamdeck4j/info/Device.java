package com.walshydev.streamdeck4j.info;

import lombok.Getter;

public class Device {

    private String id;
    private Size size;
    private byte type;

    /**
     * The unique ID of this device. You can use this to identify exactly which device is being used if there's
     * multiple.
     *
     * @return The unique identifier for this StreamDeck device.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Get the size of the StreamDeck, this returns a {@link Size} class. You can get the amount of columms,
     * rows and total amount of keys from this.
     *
     * @return The {@link Size} of the device.
     */
    public Size getSize() {
        return this.size;
    }

    /**
     * What type of device this is, there are currently only two types:
     * <ol>
     * <li> StreamDeck (5x3)</li>
     * <li> StreamDeck Mini (3x2)</li>
     * </ol>
     *
     * @return A byte representative of the device used.
     */
    public byte getType() {
        return this.type;
    }
}

@Getter
class Size {

    private int columns;
    private int rows;

    public int getTotalKeys() {
        return columns * rows;
    }
}