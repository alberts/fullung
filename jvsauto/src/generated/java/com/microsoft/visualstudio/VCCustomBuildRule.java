package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCCustomBuildRule
 */
@IID("{52FCB875-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCCustomBuildRule extends Com4jObject {
    @VTID(7)
    java.lang.String additionalOptions();

    @VTID(8)
    void additionalOptions(
        java.lang.String options);

    @VTID(9)
    @DefaultMethod
    java.lang.String name();

    @VTID(10)
    @DefaultMethod
    void name(
        java.lang.String name);

    @VTID(11)
    java.lang.String displayName();

    @VTID(12)
    void displayName(
        java.lang.String name);

    @VTID(13)
    java.lang.String commandLine();

    @VTID(14)
    void commandLine(
        java.lang.String cmdLine);

    @VTID(15)
    java.lang.String outputs();

    @VTID(16)
    void outputs(
        java.lang.String outputs);

    @VTID(17)
    java.lang.String additionalDependencies();

    @VTID(18)
    void additionalDependencies(
        java.lang.String dependencies);

    @VTID(19)
    java.lang.String fileExtensions();

    @VTID(20)
    void fileExtensions(
        java.lang.String fileExtensions);

    @VTID(21)
    java.lang.String executionDescription();

    @VTID(22)
    void executionDescription(
        java.lang.String description);

    @VTID(23)
    boolean targetRule();

    @VTID(24)
    boolean supportsFileBatching();

    @VTID(25)
    void supportsFileBatching(
        boolean pbSupportsBatching);

    @VTID(26)
    java.lang.String batchingSeparator();

    @VTID(27)
    void batchingSeparator(
        java.lang.String separator);

    @VTID(28)
    boolean showOnlyRuleProperties();

    @VTID(29)
    void showOnlyRuleProperties(
        boolean pbShowOnlyRuleProps);

    @VTID(30)
    com.microsoft.visualstudio.IVCCollection properties();

    @VTID(30)
    @ReturnValue(type=NativeType.Dispatch,defaultPropertyThrough={com.microsoft.visualstudio.IVCCollection.class})
    com4j.Com4jObject properties(
        @MarshalAs(NativeType.VARIANT) java.lang.Object index);

    @VTID(31)
    com.microsoft.visualstudio.VCRuntimeStringProperty addStringProperty(
        java.lang.String name);

    @VTID(32)
    com.microsoft.visualstudio.VCRuntimeIntegerProperty addIntegerProperty(
        java.lang.String name);

    @VTID(33)
    com.microsoft.visualstudio.VCRuntimeEnumProperty addEnumProperty(
        java.lang.String name);

    @VTID(34)
    com.microsoft.visualstudio.VCRuntimeBooleanProperty addBooleanProperty(
        java.lang.String name);

    @VTID(35)
    void removeProperty(
        com.microsoft.visualstudio.VCRuntimeProperty property);

}
