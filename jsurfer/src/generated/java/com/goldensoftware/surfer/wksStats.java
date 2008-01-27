package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum wksStats implements ComEnum {
    wksStatsFirstRow(1),
    wksStatsLastRow(2),
    wksStatsCount(4),
    wksStatsMissing(8),
    wksStatsSum(16),
    wksStatsMinimum(32),
    wksStatsMaximum(64),
    wksStatsRange(128),
    wksStatsMean(256),
    wksStatsMedian(512),
    wksStatsFirstQuartile(1024),
    wksStatsThirdQuartile(2048),
    wksStatsStandardError(4096),
    wksStatsConfidenceInterval95(8192),
    wksStatsConfidenceInterval99(16384),
    wksStatsVariance(32768),
    wksStatsAverageDeviation(65536),
    wksStatsStandardDeviation(131072),
    wksStatsCoefficientOfVariation(262144),
    wksStatsSkewness(524288),
    wksStatsKurtosis(1048576),
    wksStatsKSStatistic(2097152),
    wksStatsKSCriticalValue90(4194304),
    wksStatsKSCriticalValue95(8388608),
    wksStatsKSCriticalValue99(16777216),
    wksStatsAll(33554431), ;

    private final int value;

    wksStats(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
