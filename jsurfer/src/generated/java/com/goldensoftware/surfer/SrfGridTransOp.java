package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfGridTransOp implements ComEnum {
    srfGridTransOffset(1), srfGridTransScale(2), srfGridTransRotate(3), srfGridTransMirrorX(4), srfGridTransMirrorY(5), ;

    private final int value;

    SrfGridTransOp(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
