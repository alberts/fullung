package com.microsoft.visualstudio  ;

import com4j.*;

public enum RemoteDebuggerType implements ComEnum {
    DbgRemote(1),
    DbgRemoteTCPIP(2),
    ;

    private final int value;
    RemoteDebuggerType(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
