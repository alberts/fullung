package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfBasisType implements ComEnum {
    srfInverseMultiquadric(1), srfMultiLog(2), srfMultiQuadric(3), srfNaturalCubicSpline(4), srfThinPlateSpline(5), ;

    private final int value;

    SrfBasisType(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
