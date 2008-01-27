package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum wksOrientation implements ComEnum {
    wksOrientationPortrait(1), wksOrientationLandscape(2), ;

    private final int value;

    wksOrientation(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
