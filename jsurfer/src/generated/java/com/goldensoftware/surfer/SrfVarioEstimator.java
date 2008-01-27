package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfVarioEstimator implements ComEnum {
    srfVarioVariogram(1), srfVarioStdVariogram(2), srfVarioAutocovariance(3), srfVarioAutocorrelation(4), ;

    private final int value;

    SrfVarioEstimator(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
