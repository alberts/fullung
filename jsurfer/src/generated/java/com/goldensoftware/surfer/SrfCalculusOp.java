package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfCalculusOp implements ComEnum {
    srfGCFirstDeriv(1),
    srfGCSecondDeriv(2),
    srfGCCurvature(3),
    srfGCSlope(4),
    srfGCAspect(5),
    srfGCProfCurv(6),
    srfGCPlanCurv(7),
    srfGCTanCurv(8),
    srfGCGradient(9),
    srfGCLaplacian(10),
    srfGCBiharmonic(11),
    srfGCVolume(12),
    srfGCCorrelogram(13),
    srfGCPeriodogram(14), ;

    private final int value;

    SrfCalculusOp(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
