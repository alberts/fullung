package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfVecSymOrg implements ComEnum {
    srfOrgTail(1), srfOrgCenter(2), srfOrgHead(3), ;

    private final int value;

    SrfVecSymOrg(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
