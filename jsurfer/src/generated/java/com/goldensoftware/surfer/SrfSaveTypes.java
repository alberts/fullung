package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfSaveTypes implements ComEnum {
    srfSaveChangesYes(1), srfSaveChangesNo(2), srfSaveChangesAsk(3), ;

    private final int value;

    SrfSaveTypes(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
