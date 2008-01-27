package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfHAlign implements ComEnum {
    srfHANone(1), srfHALeft(2), srfHACenter(3), srfHARight(4), ;

    private final int value;

    SrfHAlign(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
