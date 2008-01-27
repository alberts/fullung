package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfVecCoordSys implements ComEnum {
    srfVecCartesian(1), srfVecPolar(2), ;

    private final int value;

    SrfVecCoordSys(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
