package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfPostLabelPlane implements ComEnum {
    srfPostXY(1), srfPostScreen(2), ;

    private final int value;

    SrfPostLabelPlane(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
