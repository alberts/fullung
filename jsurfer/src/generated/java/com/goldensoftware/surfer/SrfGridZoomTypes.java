package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfGridZoomTypes implements ComEnum {
    srfGridZoomFitToWindow(1), srfGridZoomIn(2), srfGridZoomOut(3), ;

    private final int value;

    SrfGridZoomTypes(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
