package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfArrowStyle implements ComEnum {
    srfASNone(1), srfASSimple(2), srfASFilled(3), srfASTriangle(4), srfAS2Stick(5), ;

    private final int value;

    SrfArrowStyle(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
