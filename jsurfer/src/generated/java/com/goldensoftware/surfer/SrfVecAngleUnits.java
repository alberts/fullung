package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfVecAngleUnits implements ComEnum {
    srfVecDegrees(1), srfVecRadians(2), ;

    private final int value;

    SrfVecAngleUnits(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
