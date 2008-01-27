package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfDupMethod implements ComEnum {
    srfDupAll(1),
    srfDupNone(2),
    srfDupFirst(3),
    srfDupLast(4),
    srfDupMinX(5),
    srfDupMaxX(6),
    srfDupMedX(7),
    srfDupMinY(8),
    srfDupMaxY(9),
    srfDupMedY(10),
    srfDupMinZ(11),
    srfDupMaxZ(12),
    srfDupMedZ(13),
    srfDupSum(14),
    srfDupAvg(15),
    srfDupMid(16),
    srfDupRand(17), ;

    private final int value;

    SrfDupMethod(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
