package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfZoomTypes implements ComEnum {
    srfZoomFitToWindow(1), srfZoomPage(2), srfZoomActualSize(3), srfZoomSelected(4), srfZoomFullScreen(5), ;

    private final int value;

    SrfZoomTypes(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
