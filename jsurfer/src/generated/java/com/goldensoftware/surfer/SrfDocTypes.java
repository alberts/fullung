package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfDocTypes implements ComEnum {
    srfDocPlot(1), srfDocWks(2), srfDocGrid(3), ;

    private final int value;

    SrfDocTypes(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
