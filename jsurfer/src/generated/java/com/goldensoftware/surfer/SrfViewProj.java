package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfViewProj implements ComEnum {
    srfPerspective(1), srfOrthographic(2), ;

    private final int value;

    SrfViewProj(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
