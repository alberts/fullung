package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCResourceCompilerTool
 */
@IID("{52FCB861-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCResourceCompilerTool extends Com4jObject {
    @VTID(7)
    @DefaultMethod
    java.lang.String toolName();

    @VTID(8)
    java.lang.String additionalOptions();

    @VTID(9)
    void additionalOptions(
        java.lang.String additionalOptions);

    @VTID(10)
    java.lang.String preprocessorDefinitions();

    @VTID(11)
    void preprocessorDefinitions(
        java.lang.String defines);

    @VTID(12)
    com.microsoft.visualstudio.enumResourceLangID culture();

    @VTID(13)
    void culture(
        com.microsoft.visualstudio.enumResourceLangID langID);

    @VTID(14)
    java.lang.String additionalIncludeDirectories();

    @VTID(15)
    void additionalIncludeDirectories(
        java.lang.String includePath);

    @VTID(16)
    boolean ignoreStandardIncludePath();

    @VTID(17)
    void ignoreStandardIncludePath(
        boolean ignoreInclPath);

    @VTID(18)
    boolean showProgress();

    @VTID(19)
    void showProgress(
        boolean showProgress);

    @VTID(20)
    java.lang.String resourceOutputFileName();

    @VTID(21)
    void resourceOutputFileName(
        java.lang.String resFile);

    @VTID(22)
    java.lang.String toolPath();

    @VTID(23)
    java.lang.String get_PropertyOption(
        java.lang.String prop,
        int dispidProp);

    @VTID(24)
    java.lang.String fullIncludePath();

    @VTID(25)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(26)
    java.lang.String toolKind();

    @VTID(27)
    int executionBucket();

    @VTID(28)
    void executionBucket(
        int pVal);

}
