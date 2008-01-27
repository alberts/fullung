package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfWindowState implements ComEnum {
    srfWindowStateMaximized(1), srfWindowStateMinimized(2), srfWindowStateNormal(3), ;

    private final int value;

    SrfWindowState(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
