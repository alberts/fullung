package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfZOrder implements ComEnum {
    srfZOToFront(1), srfZOToBack(2), srfZOForward(3), srfZOBackward(4), ;

    private final int value;

    SrfZOrder(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
