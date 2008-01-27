package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfGridFormat implements ComEnum {
    srfGridFmtBinary(1), srfGridFmtAscii(2), srfGridFmtS7(3), srfGridFmtXYZ(4), ;

    private final int value;

    SrfGridFormat(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
