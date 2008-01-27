package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfGLFillStyle implements ComEnum {
    srfFillNone(1), srfFillSolid(2), srfFillLighted(3), ;

    private final int value;

    SrfGLFillStyle(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
