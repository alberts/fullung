package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfVAlign implements ComEnum {
    srfVANone(1), srfVATop(2), srfVACenter(3), srfVABottom(4), ;

    private final int value;

    SrfVAlign(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
