package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfDetrendMethod implements ComEnum {
    srfDetrendNone(1), srfDetrendLinear(2), srfDetrendQuadratic(3), ;

    private final int value;

    SrfDetrendMethod(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
