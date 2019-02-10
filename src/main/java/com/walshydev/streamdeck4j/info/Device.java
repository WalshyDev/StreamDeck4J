package com.walshydev.streamdeck4j.info;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Device {

    private Size size;
    private byte type;

    /**
     * Get the size of the StreamDeck, this returns a {@link Size} class. You can get the amount of columms, rows and
     * total amount of keys from this.
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

    @Getter
    @AllArgsConstructor
    public static class Size {

        private int columns;
        private int rows;

        public int getTotalKeys() {
            return columns * rows;
        }
    }
}