package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfDataStats implements ComEnum {
    srfStatActiveCount(1),
    srfStatOriginalCount(2),
    srfStatExcludedCount(3),
    srfStatDeletedDups(4),
    srfStatRetainedDups(5),
    srfStatArtificial(6),
    srfStatXMinimum(7),
    srfStatXMaximum(8),
    srfStatXRange(9),
    srfStatXMidRange(10),
    srfStatX25Percentile(11),
    srfStatX75Percentile(12),
    srfStatXMedian(13),
    srfStatXAverage(14),
    srfStatXVariance(15),
    srfStatXStdDev(16),
    srfStatYMinimum(17),
    srfStatYMaximum(18),
    srfStatYRange(19),
    srfStatYMidRange(20),
    srfStatY25Percentile(21),
    srfStatY75Percentile(22),
    srfStatYMedian(23),
    srfStatYAverage(24),
    srfStatYVariance(25),
    srfStatYStdDev(26),
    srfStatZMinimum(27),
    srfStatZMaximum(28),
    srfStatZRange(29),
    srfStatZMidRange(30),
    srfStatZ25Percentile(31),
    srfStatZ75Percentile(32),
    srfStatZMedian(33),
    srfStatZAverage(34),
    srfStatZVariance(35),
    srfStatZStdDev(36),
    srfStatCoefVariation(37),
    srfStatCoefSkew(38),
    srfStatXYCorrelation(39),
    srfStatXZCorrelation(40),
    srfStatYZCorrelation(41),
    srfStatXYCovariance(42),
    srfStatXZCovariance(43),
    srfStatYZCovariance(44),
    srfStatNNAvgDist(45),
    srfStatNNMinDist(46),
    srfStatNNMaxDist(47),
    srfStatNNGamma(48), ;

    private final int value;

    SrfDataStats(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
