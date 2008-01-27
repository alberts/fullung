package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfPostLegSymSize implements ComEnum {
    srfPostSizeFont(1), srfPostSizePlot(2), srfPostSizeUser(3), ;

    private final int value;

    SrfPostLegSymSize(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
