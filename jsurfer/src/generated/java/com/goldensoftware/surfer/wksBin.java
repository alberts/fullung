package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum wksBin implements ComEnum {
    wksBinUpper(1),
    wksBinOnlyOne(1),
    wksBinLower(2),
    wksBinMiddle(3),
    wksBinManual(4),
    wksBinEnvelope(5),
    wksBinEnvManual(6),
    wksBinAuto(7),
    wksBinTractor(8),
    wksBinSmallFmt(9),
    wksBinLargeFmt(10),
    wksBinLargeCapacity(11),
    wksBinCassette(14),
    wksBinFormSource(15), ;

    private final int value;

    wksBin(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
