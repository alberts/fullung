package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfSplineMethod implements ComEnum {
    srfSplineInsert(1), srfSplineRecalc(2), ;

    private final int value;

    SrfSplineMethod(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
