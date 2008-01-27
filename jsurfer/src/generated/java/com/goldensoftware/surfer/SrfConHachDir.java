package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfConHachDir implements ComEnum {
    srfConHachDownhill(1), srfConHachUphill(2), ;

    private final int value;

    SrfConHachDir(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
