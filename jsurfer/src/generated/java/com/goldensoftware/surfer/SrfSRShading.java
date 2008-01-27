package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfSRShading implements ComEnum {
    srfSRSimple(1), srfSRPeuckers(2), srfSRLambertian(3), srfSRLommel(4), ;

    private final int value;

    SrfSRShading(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
