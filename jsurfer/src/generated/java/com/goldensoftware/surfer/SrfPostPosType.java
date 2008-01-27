package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfPostPosType implements ComEnum {
    srfPostPosCenter(1), srfPostPosLeft(2), srfPostPosRight(3), srfPostPosAbove(4), srfPostPosBelow(5), srfPostPosUser(
            6), ;

    private final int value;

    SrfPostPosType(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
