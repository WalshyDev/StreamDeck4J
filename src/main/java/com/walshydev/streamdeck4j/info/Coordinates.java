package com.walshydev.streamdeck4j.info;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Coordinates {

    private final int column;
    private final int row;

    public Coordinates(int column, int row) {
        this.column = column;
        this.row = row;
    }
}
