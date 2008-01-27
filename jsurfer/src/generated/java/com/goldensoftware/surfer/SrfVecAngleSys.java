package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfVecAngleSys implements ComEnum {
    srfVecBearing(1), srfVecAngle(2), ;

    private final int value;

    SrfVecAngleSys(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
