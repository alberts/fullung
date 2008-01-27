package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfLegendFrame implements ComEnum {
    srfLegFrameNone(1), srfLegFrameSquare(2), srfLegFrameRounded(3), ;

    private final int value;

    SrfLegendFrame(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
