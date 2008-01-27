package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfLightModel implements ComEnum {
    srfLMNone(1), srfLMSmooth(2), srfLMFlat(3), ;

    private final int value;

    SrfLightModel(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
