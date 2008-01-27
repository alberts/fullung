package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfWireBorder implements ComEnum {
    srfWireNone(1), srfWireAll(2), srfWireFront(3), ;

    private final int value;

    SrfWireBorder(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
