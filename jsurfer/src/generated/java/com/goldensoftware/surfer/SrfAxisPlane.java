package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfAxisPlane implements ComEnum {
    srfAxisPlaneXY(1), srfAxisPlaneXZ(2), srfAxisPlaneYZ(3), ;

    private final int value;

    SrfAxisPlane(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
