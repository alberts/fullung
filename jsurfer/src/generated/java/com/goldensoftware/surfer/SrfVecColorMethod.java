package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfVecColorMethod implements ComEnum {
    srfVecDisabled(1), srfVecMagnitude(2), srfVecGrid(3), ;

    private final int value;

    SrfVecColorMethod(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
