package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfArrangeType implements ComEnum {
    srfCascade(1), srfTileHorz(2), srfTileVert(3), srfArrangeIcons(4), ;

    private final int value;

    SrfArrangeType(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
