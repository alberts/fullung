package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCRuntimeStringProperty Interface
 */
@IID("{9C894C02-B512-4B23-9B29-E3195B26C6BA}")
public interface VCRuntimeStringProperty extends com.microsoft.visualstudio.VCRuntimeProperty {
    @VTID(27)
    java.lang.String _switch();

    @VTID(28)
    void _switch(
        java.lang.String pbstrSwitch);

    @VTID(29)
    java.lang.String defaultValue();

    @VTID(30)
    void defaultValue(
        java.lang.String pbstrDefaultValue);

    @VTID(31)
    boolean delimited();

    @VTID(32)
    void delimited(
        boolean pbDelimited);

    @VTID(33)
    java.lang.String delimiters();

    @VTID(34)
    void delimiters(
        java.lang.String pbstrDelimiter);

    @VTID(35)
    boolean inheritable();

    @VTID(36)
    void inheritable(
        boolean pbInheritable);

}
