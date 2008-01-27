package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfResampleMethod implements ComEnum {
    srfNearest(1), srfBilinear(2), srfCubic(3), ;

    private final int value;

    SrfResampleMethod(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
