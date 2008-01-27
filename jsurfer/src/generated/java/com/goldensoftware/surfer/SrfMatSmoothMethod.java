package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfMatSmoothMethod implements ComEnum {
    srfMatSmoothAverage(1), srfMatSmoothWeighted(2), ;

    private final int value;

    SrfMatSmoothMethod(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
