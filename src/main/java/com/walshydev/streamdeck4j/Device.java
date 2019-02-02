package com.walshydev.streamdeck4j;

import lombok.Getter;

@Getter
public class Device {

    private String id;
    private Size size;
    private int type;
}

@Getter
class Size {

    private int columns;
    private int rows;

    public int getTotalKeys() {
        return columns * rows;
    }
}