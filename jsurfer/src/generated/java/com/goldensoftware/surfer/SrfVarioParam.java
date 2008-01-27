package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfVarioParam implements ComEnum {
    srfVarParam1(1), srfVarParam2(2), srfVarParamPower(3), srfVarParamRatio(4), srfVarParamAngle(5), ;

    private final int value;

    SrfVarioParam(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
