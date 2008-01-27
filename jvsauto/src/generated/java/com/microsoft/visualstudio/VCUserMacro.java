package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCUserMacro Interface
 */
@IID("{238B5188-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCUserMacro extends Com4jObject {
    @VTID(7)
    java.lang.String name();

    @VTID(8)
    java.lang.String value();

    @VTID(9)
    void value(
        java.lang.String variableValue);

    @VTID(10)
    boolean inheritsFromParent();

    @VTID(11)
    void inheritsFromParent(
        boolean variableType);

    @VTID(12)
    java.lang.String delimiter();

    @VTID(13)
    void delimiter(
        java.lang.String variableDelimiter);

    @VTID(14)
    boolean performEnvironmentSet();

    @VTID(15)
    void performEnvironmentSet(
        boolean pbSetInEnvironment);

    @VTID(16)
    com.microsoft.visualstudio.VCPropertySheet propertySheet();

    @VTID(17)
    boolean matchName(
        java.lang.String nameToMatch,
        boolean fullOnly);

}
