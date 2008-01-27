package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfDataMetric implements ComEnum {
    srfDMMinimum(1),
    srfDMLowerQ(2),
    srfDMMedian(3),
    srfDMUpperQ(4),
    srfDMMaximum(5),
    srfDMRange(6),
    srfDMMidrange(7),
    srfDMInterQRange(8),
    srfDMMean(9),
    srfDMStdDev(10),
    srfDMVariance(11),
    srfDMCoefVar(12),
    srfDMMad(13),
    srfDMRms(14),
    srfDMSum(15),
    srfDMCount(16),
    srfDMDensity(17),
    srfDMNearest(18),
    srfDMFarthest(19),
    srfDMMedianDist(20),
    srfDMAvgDist(21),
    srfDMOffset(22),
    srfDMSlope(23),
    srfDMAspect(24), ;

    private final int value;

    SrfDataMetric(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
