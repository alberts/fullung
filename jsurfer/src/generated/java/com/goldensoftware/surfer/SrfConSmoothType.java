package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfConSmoothType implements ComEnum {
    srfConSmoothNone(1), srfConSmoothLow(2), srfConSmoothMed(3), srfConSmoothHigh(4), ;

    private final int value;

    SrfConSmoothType(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
