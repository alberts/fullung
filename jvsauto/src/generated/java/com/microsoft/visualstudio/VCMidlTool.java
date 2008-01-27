package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCMidlTool
 */
@IID("{52FCB860-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCMidlTool extends Com4jObject {
    @VTID(7)
    @DefaultMethod
    java.lang.String toolName();

    @VTID(8)
    java.lang.String additionalOptions();

    @VTID(9)
    void additionalOptions(
        java.lang.String options);

    @VTID(10)
    java.lang.String preprocessorDefinitions();

    @VTID(11)
    void preprocessorDefinitions(
        java.lang.String defines);

    @VTID(12)
    java.lang.String additionalIncludeDirectories();

    @VTID(13)
    void additionalIncludeDirectories(
        java.lang.String includePath);

    @VTID(14)
    boolean ignoreStandardIncludePath();

    @VTID(15)
    void ignoreStandardIncludePath(
        boolean ignore);

    @VTID(16)
    boolean mkTypLibCompatible();

    @VTID(17)
    void mkTypLibCompatible(
        boolean compatible);

    @VTID(18)
    com.microsoft.visualstudio.midlWarningLevelOption warningLevel();

    @VTID(19)
    void warningLevel(
        com.microsoft.visualstudio.midlWarningLevelOption optSetting);

    @VTID(20)
    boolean warnAsError();

    @VTID(21)
    void warnAsError(
        boolean warnAsError);

    @VTID(22)
    boolean suppressStartupBanner();

    @VTID(23)
    void suppressStartupBanner(
        boolean noLogo);

    @VTID(24)
    com.microsoft.visualstudio.midlCharOption defaultCharType();

    @VTID(25)
    void defaultCharType(
        com.microsoft.visualstudio.midlCharOption optSetting);

    @VTID(26)
    com.microsoft.visualstudio.midlTargetEnvironment targetEnvironment();

    @VTID(27)
    void targetEnvironment(
        com.microsoft.visualstudio.midlTargetEnvironment optSetting);

    @VTID(28)
    boolean generateStublessProxies();

    @VTID(29)
    void generateStublessProxies(
        boolean optSetting);

    @VTID(30)
    java.lang.String outputDirectory();

    @VTID(31)
    void outputDirectory(
        java.lang.String out);

    @VTID(32)
    java.lang.String headerFileName();

    @VTID(33)
    void headerFileName(
        java.lang.String headerFile);

    @VTID(34)
    java.lang.String dllDataFileName();

    @VTID(35)
    void dllDataFileName(
        java.lang.String dllData);

    @VTID(36)
    java.lang.String interfaceIdentifierFileName();

    @VTID(37)
    void interfaceIdentifierFileName(
        java.lang.String iid);

    @VTID(38)
    java.lang.String proxyFileName();

    @VTID(39)
    void proxyFileName(
        java.lang.String proxyFile);

    @VTID(40)
    boolean generateTypeLibrary();

    @VTID(41)
    void generateTypeLibrary(
        boolean optSetting);

    @VTID(42)
    java.lang.String typeLibraryName();

    @VTID(43)
    void typeLibraryName(
        java.lang.String tlbFile);

    @VTID(44)
    com.microsoft.visualstudio.midlErrorCheckOption enableErrorChecks();

    @VTID(45)
    void enableErrorChecks(
        com.microsoft.visualstudio.midlErrorCheckOption optSetting);

    @VTID(46)
    boolean errorCheckAllocations();

    @VTID(47)
    void errorCheckAllocations(
        boolean errorCheck);

    @VTID(48)
    boolean errorCheckBounds();

    @VTID(49)
    void errorCheckBounds(
        boolean errorCheck);

    @VTID(50)
    boolean errorCheckEnumRange();

    @VTID(51)
    void errorCheckEnumRange(
        boolean errorCheck);

    @VTID(52)
    boolean errorCheckRefPointers();

    @VTID(53)
    void errorCheckRefPointers(
        boolean errorCheck);

    @VTID(54)
    boolean errorCheckStubData();

    @VTID(55)
    void errorCheckStubData(
        boolean errorCheck);

    @VTID(56)
    boolean validateParameters();

    @VTID(57)
    void validateParameters(
        boolean validate);

    @VTID(58)
    java.lang.String redirectOutputAndErrors();

    @VTID(59)
    void redirectOutputAndErrors(
        java.lang.String output);

    @VTID(60)
    com.microsoft.visualstudio.midlStructMemberAlignOption structMemberAlignment();

    @VTID(61)
    void structMemberAlignment(
        com.microsoft.visualstudio.midlStructMemberAlignOption optSetting);

    @VTID(62)
    java.lang.String cPreprocessOptions();

    @VTID(63)
    void cPreprocessOptions(
        java.lang.String opt);

    @VTID(64)
    java.lang.String undefinePreprocessorDefinitions();

    @VTID(65)
    void undefinePreprocessorDefinitions(
        java.lang.String undefines);

    @VTID(66)
    java.lang.String toolPath();

    @VTID(67)
    java.lang.String get_PropertyOption(
        java.lang.String prop,
        int dispidProp);

    @VTID(68)
    java.lang.String fullIncludePath();

    @VTID(69)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(70)
    java.lang.String toolKind();

    @VTID(71)
    int executionBucket();

    @VTID(72)
    void executionBucket(
        int pVal);

}
