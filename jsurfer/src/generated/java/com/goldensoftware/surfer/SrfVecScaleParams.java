package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfVecScaleParams implements ComEnum {
    srfVSMagnitude(1), srfVSShaftLength(2), srfVSHeadLength(3), srfVSSymWidth(4), ;

    private final int value;

    SrfVecScaleParams(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
