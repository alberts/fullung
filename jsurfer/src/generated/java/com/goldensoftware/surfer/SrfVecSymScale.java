package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfVecSymScale implements ComEnum {
    srfVecLinear(1), srfVecSqRoot(2), srfVecLog(3), ;

    private final int value;

    SrfVecSymScale(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
