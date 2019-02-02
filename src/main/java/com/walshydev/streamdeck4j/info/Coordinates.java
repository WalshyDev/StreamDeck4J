package com.walshydev.streamdeck4j.info;

import lombok.Getter;

@Getter
public class Coordinates {

    private final int column;
    private final int row;

    public Coordinates(int column, int row) {
        this.column = column;
        this.row = row;
    }
}
