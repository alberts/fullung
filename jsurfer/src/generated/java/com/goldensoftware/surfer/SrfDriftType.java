package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfDriftType implements ComEnum {
    srfDriftNone(1), srfDriftLinear(2), srfDriftQuadratic(3), ;

    private final int value;

    SrfDriftType(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
