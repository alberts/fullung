package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfPostSizeType implements ComEnum {
    srfPostFixed(1), srfPostLinear(2), srfPostSqRoot(3), ;

    private final int value;

    SrfPostSizeType(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
