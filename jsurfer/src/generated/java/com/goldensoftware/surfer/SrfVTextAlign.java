package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfVTextAlign implements ComEnum {
    srfTATop(1), srfTABaseline(2), srfTABottom(3), srfTAVCenter(4), ;

    private final int value;

    SrfVTextAlign(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
