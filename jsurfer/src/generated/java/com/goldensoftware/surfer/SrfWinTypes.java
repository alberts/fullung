package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfWinTypes implements ComEnum {
    srfWinPlot(1), srfWinWks(2), srfWinGrid(3), ;

    private final int value;

    SrfWinTypes(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
