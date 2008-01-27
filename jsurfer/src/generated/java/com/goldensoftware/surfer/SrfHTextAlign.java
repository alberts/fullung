package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfHTextAlign implements ComEnum {
    srfTALeft(1), srfTACenter(2), srfTARight(3), ;

    private final int value;

    SrfHTextAlign(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
