package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfGridAlgorithm implements ComEnum {
    srfInverseDistance(1),
    srfKriging(2),
    srfMinCurvature(3),
    srfShepards(4),
    srfNaturalNeighbor(5),
    srfNearestNeighbor(6),
    srfRegression(7),
    srfRadialBasis(8),
    srfTriangulation(9),
    srfMovingAverage(10),
    srfDataMetrics(11),
    srfLocalPolynomial(12), ;

    private final int value;

    SrfGridAlgorithm(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
