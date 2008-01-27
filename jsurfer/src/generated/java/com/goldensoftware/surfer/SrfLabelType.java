package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfLabelType implements ComEnum {
    srfLabFixed(1), srfLabExpon(2), srfLabGeneral(3), ;

    private final int value;

    SrfLabelType(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
