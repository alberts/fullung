package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfPageUnits implements ComEnum {
    srfUnitsInch(1), srfUnitsCentimeter(2), ;

    private final int value;

    SrfPageUnits(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
