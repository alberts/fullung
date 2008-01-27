package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfFilterEdge implements ComEnum {
    srfFltEdgeBlank(1),
    srfFltEdgeIgnore(2),
    srfFltEdgeReplicate(3),
    srfFltEdgeMirror(4),
    srfFltEdgeCyclic(5),
    srfFltEdgeFill(6), ;

    private final int value;

    SrfFilterEdge(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
