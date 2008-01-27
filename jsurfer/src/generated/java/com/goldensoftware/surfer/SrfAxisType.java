package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfAxisType implements ComEnum {
    srfATLeft(1), srfATRight(2), srfATBottom(3), srfATTop(4), srfATZ(5), ;

    private final int value;

    SrfAxisType(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
