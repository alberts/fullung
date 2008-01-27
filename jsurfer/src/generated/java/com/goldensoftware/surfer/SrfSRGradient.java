package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfSRGradient implements ComEnum {
    srfSRCentral(1), srfSRMidpoint(2), ;

    private final int value;

    SrfSRGradient(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
