package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfVarioFitMethod implements ComEnum {
    srfVarioFitDefault(1), srfVarioFitLeastAbsVal(2), srfVarioFitLeastSquares(3), ;

    private final int value;

    SrfVarioFitMethod(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
