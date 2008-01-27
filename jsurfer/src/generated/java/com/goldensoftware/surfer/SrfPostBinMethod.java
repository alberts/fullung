package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfPostBinMethod implements ComEnum {
    srfPostEqNum(1), srfPostEqInt(2), srfPostUser(3), ;

    private final int value;

    SrfPostBinMethod(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
