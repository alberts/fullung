package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfKrigType implements ComEnum {
    srfKrigPoint(1), srfKrigBlock(2), ;

    private final int value;

    SrfKrigType(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
