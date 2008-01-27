package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum wksClipboard implements ComEnum {
    wksClipboardText(1),
    wksClibboardCsv(2),
    wksClibboardBiff8(3),
    wksClibboardBiff5(4),
    wksClipboardBiff4(5),
    wksClipboardBiff3(6),
    wksClipboardBiff(7),
    wksClipboardSylk(8),
    wksClipboardWk4(9),
    wksClipboardWk3(10),
    wksClipboardWk1(11), ;

    private final int value;

    wksClipboard(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
