package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCPlatform
 */
@IID("{238B5171-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCPlatform extends Com4jObject {
    @VTID(7)
    java.lang.String executableDirectories();

    @VTID(8)
    void executableDirectories(
        java.lang.String dir);

    @VTID(9)
    java.lang.String includeDirectories();

    @VTID(10)
    void includeDirectories(
        java.lang.String dir);

    @VTID(11)
    java.lang.String referenceDirectories();

    @VTID(12)
    void referenceDirectories(
        java.lang.String dir);

    @VTID(13)
    java.lang.String libraryDirectories();

    @VTID(14)
    void libraryDirectories(
        java.lang.String dir);

    @VTID(15)
    java.lang.String sourceDirectories();

    @VTID(16)
    void sourceDirectories(
        java.lang.String dir);

    @VTID(17)
    java.lang.String excludeDirectories();

    @VTID(18)
    void excludeDirectories(
        java.lang.String dir);

    @VTID(19)
    java.lang.String name();

    @VTID(20)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject tools();

    @VTID(21)
    boolean matchName(
        java.lang.String nameToMatch,
        boolean fullOnly);

    @VTID(22)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(23)
    java.lang.String evaluate(
        java.lang.String in);

    @VTID(24)
    java.lang.String defaultDirectory();

    @VTID(25)
    void commitChanges();

    @VTID(26)
    java.lang.String getToolNameForKeyword(
        java.lang.String keyword);

    @VTID(27)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject deploymentTool();

    @VTID(28)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject debuggerTool();

    @VTID(29)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject generalPageTool();

    @VTID(30)
    int numberOfPlatformMacros();

    @VTID(31)
    java.lang.String platformMacro(
        int index);

    @VTID(32)
    java.lang.String getMacroValue(
        java.lang.String in);

    @VTID(33)
    boolean disableAlternateDebuggers();

    @VTID(34)
    java.lang.String executableExtensions();

    @VTID(35)
    java.lang.String dumpfileExtensions();

    @VTID(36)
    boolean isExecutable(
        java.lang.String path);

    @VTID(37)
    boolean isDumpfile(
        java.lang.String path);

}
