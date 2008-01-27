package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfToolbars implements ComEnum {
    srfTBMain(1), srfTBDraw(2), srfTBMap(4), ;

    private final int value;

    SrfToolbars(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
