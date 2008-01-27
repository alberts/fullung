package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfFilter implements ComEnum {
    srfFilterAvg(1),
    srfFilterDist(2),
    srfFilterInvDist(3),
    srfFilterGaussianLP(4),
    srfFilterUserSpecified(5),
    srfFilter5PixelPAvg(6),
    srfFilter5PixelXAvg(7),
    srfFilter9PixelAvg(8),
    srfFilterGaussian(9),
    srfFilterLowPass1(10),
    srfFilterLowPass2(11),
    srfFilterLowPass3(12),
    srfFilterMeanRemoval(13),
    srfFilterHighPass1(14),
    srfFilterHighPass2(15),
    srfFilterHighPass3(16),
    srfFilterRobertsRow(17),
    srfFilterRobertsCol(18),
    srfFilterPrewittRow(19),
    srfFilterPrewittCol(20),
    srfFilterSobelRow(21),
    srfFilterSobelCol(22),
    srfFilterFreichenRow(23),
    srfFilterFreichenCol(24),
    srfFilterLap1(25),
    srfFilterLap2(26),
    srfFilterLap3(27),
    srfFilterLap4(28),
    srfFilterLapDiff(29),
    srfFilterGaussDiff7(30),
    srfFilterGaussDiff9(31),
    srfFilterShiftDiffHorz(32),
    srfFilterShiftDiffVert(33),
    srfFilterGradEast(34),
    srfFilterGradSoutheast(35),
    srfFilterGradSouth(36),
    srfFilterGradSouthwest(37),
    srfFilterGradWest(38),
    srfFilterGradNorthwest(39),
    srfFilterGradNorth(40),
    srfFilterGradNortheast(41),
    srfFilterEmbEast(42),
    srfFilterEmbSoutheast(43),
    srfFilterEmbSouth(44),
    srfFilterEmbSouthwest(45),
    srfFilterEmbWest(46),
    srfFilterEmbNorthwest(47),
    srfFilterEmbNorth(48),
    srfFilterEmbNortheast(49),
    srfFilterMin(50),
    srfFilterLowerQuartile(51),
    srfFilterMedian(52),
    srfFilterUpperQuartile(53),
    srfFilterMax(54),
    srfFilterRange(55),
    srfFilterStdDev(56),
    srfFilterVariance(57),
    srfFilterCoefVar(58),
    srfFilterMedianDiff(59),
    srfFilterThresholdAvg(60),
    srfFilterPrewittCompass(61),
    srfFilterKirschCompass(62),
    srfFilterRobinson3Compass(63),
    srfFilterRobinson5Compass(64), ;

    private final int value;

    SrfFilter(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
