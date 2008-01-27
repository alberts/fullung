package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfFilterBlank implements ComEnum {
    srfFltBlankExpand(1), srfFltBlankLeave(2), srfFltBlankShrink(3), srfFltBlankFill(4), ;

    private final int value;

    SrfFilterBlank(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
