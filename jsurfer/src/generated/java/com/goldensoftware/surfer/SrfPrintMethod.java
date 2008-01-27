package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfPrintMethod implements ComEnum {
    srfTruncate(1), srfFitToPage(2), srfTile(3), ;

    private final int value;

    SrfPrintMethod(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
