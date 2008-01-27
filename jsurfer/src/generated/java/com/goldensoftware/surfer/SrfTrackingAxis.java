package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfTrackingAxis implements ComEnum {
    srfTrackX(1), srfTrackY(2), srfTrackZ(3), ;

    private final int value;

    SrfTrackingAxis(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
