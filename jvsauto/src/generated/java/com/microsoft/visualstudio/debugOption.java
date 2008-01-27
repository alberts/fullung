package com.microsoft.visualstudio  ;

import com4j.*;

public enum debugOption implements ComEnum {
    debugDisabled(0),
    debugOldStyleInfo(1),
    debugEnabled(3),
    debugEditAndContinue(4),
    ;

    private final int value;
    debugOption(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
