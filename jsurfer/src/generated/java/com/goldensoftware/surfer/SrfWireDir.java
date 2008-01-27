package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfWireDir implements ComEnum {
    srfWireXDir(1), srfWireYDir(2), srfWireZDir(4), ;

    private final int value;

    SrfWireDir(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
