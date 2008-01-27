package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfPasteFormat implements ComEnum {
    srfPasteBest(1),
    srfPasteSurfer(2),
    srfPasteText(3),
    srfPasteMetafile(4),
    srfPasteMetaBreak(5),
    srfPasteBitmap(6),
    srfPasteEnhMetafile(7),
    srfPasteEnhMetaBreak(8), ;

    private final int value;

    SrfPasteFormat(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
