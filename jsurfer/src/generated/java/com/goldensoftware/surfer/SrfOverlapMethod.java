package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfOverlapMethod implements ComEnum {
    srfAverage(1), srfFirst(2), srfLast(3), srfMinimum(4), srfMaximum(5), ;

    private final int value;

    SrfOverlapMethod(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
