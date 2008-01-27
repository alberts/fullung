package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfSaveFormat implements ComEnum {
    srfSaveFormatDat(1),
    srfSaveFormatSlk(2),
    srfSaveFormatCsv(3),
    srfSaveFormatText(4),
    srfSaveFormatExcel(5),
    srfSaveFormatBna(12),
    srfSaveFormatBln(14),
    srfSaveFormatUnknown(-3), ;

    private final int value;

    SrfSaveFormat(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
