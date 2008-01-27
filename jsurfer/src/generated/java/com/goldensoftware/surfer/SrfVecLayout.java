package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfVecLayout implements ComEnum {
    srfVecLegHorz(1), srfVecLegVert(2), ;

    private final int value;

    SrfVecLayout(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
