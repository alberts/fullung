package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCRuntimeEnumValue Interface
 */
@IID("{DDDA0C6B-F8E7-4A9A-8A7E-91D7600AFA96}")
public interface VCRuntimeEnumValue extends Com4jObject {
    @VTID(7)
    java.lang.String _switch();

    @VTID(8)
    void _switch(
        java.lang.String pbstrSwitch);

    @VTID(9)
    int value();

    @VTID(10)
    void value(
        int plValue);

    @VTID(11)
    java.lang.String displayName();

    @VTID(12)
    void displayName(
        java.lang.String pbstrDisplayName);

}
