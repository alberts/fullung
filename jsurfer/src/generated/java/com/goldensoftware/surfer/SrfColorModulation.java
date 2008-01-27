package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfColorModulation implements ComEnum {
    srfCMSurfaceOnly(1), srfCMOverlayOnly(2), srfCMBoth(3), ;

    private final int value;

    SrfColorModulation(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
