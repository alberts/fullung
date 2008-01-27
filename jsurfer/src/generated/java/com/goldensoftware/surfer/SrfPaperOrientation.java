package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfPaperOrientation implements ComEnum {
    srfPortrait(1), srfLandscape(2), ;

    private final int value;

    SrfPaperOrientation(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
