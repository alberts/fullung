package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum wksFileFormat implements ComEnum {
    wksFileFormatDat(1),
    wksFileFormatSlk(2),
    wksFileFormatCsv(3),
    wksFileFormatText(4),
    wksFileFormatExcel(5),
    wksFileFormatBna(12),
    wksFileFormatBln(14),
    wksFileFormatWk1(6),
    wksFileFormatWk3(7),
    wksFileFormatWk4(8),
    wksFileFormatWks(9),
    wksFileFormatWrk(10),
    wksFileFormatWr1(11),
    wksFileFormatUnknown(-3), ;

    private final int value;

    wksFileFormat(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
