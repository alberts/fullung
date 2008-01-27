package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfTickType implements ComEnum {
    srfTickNone(1), srfTickOut(2), srfTickIn(3), srfTickCross(4), ;

    private final int value;

    SrfTickType(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
