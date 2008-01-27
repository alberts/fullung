package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfConFormat implements ComEnum {
    srfConFormatDefault(1), srfConFormatDXF(2), ;

    private final int value;

    SrfConFormat(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
