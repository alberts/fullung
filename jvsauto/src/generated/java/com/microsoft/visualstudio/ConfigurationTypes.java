package com.microsoft.visualstudio  ;

import com4j.*;

public enum ConfigurationTypes implements ComEnum {
    typeUnknown(0),
    typeApplication(1),
    typeDynamicLibrary(2),
    typeStaticLibrary(4),
    typeGeneric(10),
    ;

    private final int value;
    ConfigurationTypes(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
