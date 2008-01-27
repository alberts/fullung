package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfOverlayResample implements ComEnum {
    srfORLinear(1), srfORNearest(2), ;

    private final int value;

    SrfOverlayResample(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
