package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCLibrarianTool
 */
@IID("{52FCB858-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCLibrarianTool extends Com4jObject {
    @VTID(7)
    java.lang.String additionalOptions();

    @VTID(8)
    void additionalOptions(
        java.lang.String options);

    @VTID(9)
    @DefaultMethod
    java.lang.String toolName();

    @VTID(10)
    java.lang.String outputFile();

    @VTID(11)
    void outputFile(
        java.lang.String out);

    @VTID(12)
    java.lang.String additionalDependencies();

    @VTID(13)
    void additionalDependencies(
        java.lang.String dependencies);

    @VTID(14)
    java.lang.String additionalLibraryDirectories();

    @VTID(15)
    void additionalLibraryDirectories(
        java.lang.String libPath);

    @VTID(16)
    boolean suppressStartupBanner();

    @VTID(17)
    void suppressStartupBanner(
        boolean noLogo);

    @VTID(18)
    java.lang.String moduleDefinitionFile();

    @VTID(19)
    void moduleDefinitionFile(
        java.lang.String defFile);

    @VTID(20)
    boolean ignoreAllDefaultLibraries();

    @VTID(21)
    void ignoreAllDefaultLibraries(
        boolean noDefault);

    @VTID(22)
    java.lang.String ignoreDefaultLibraryNames();

    @VTID(23)
    void ignoreDefaultLibraryNames(
        java.lang.String lib);

    @VTID(24)
    java.lang.String exportNamedFunctions();

    @VTID(25)
    void exportNamedFunctions(
        java.lang.String symbols);

    @VTID(26)
    java.lang.String forceSymbolReferences();

    @VTID(27)
    void forceSymbolReferences(
        java.lang.String symbol);

    @VTID(28)
    java.lang.String toolPath();

    @VTID(29)
    java.lang.String get_PropertyOption(
        java.lang.String prop,
        int dispidProp);

    @VTID(30)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(31)
    java.lang.String toolKind();

    @VTID(32)
    int executionBucket();

    @VTID(33)
    void executionBucket(
        int pVal);

    @VTID(34)
    boolean useUnicodeResponseFiles();

    @VTID(35)
    void useUnicodeResponseFiles(
        boolean pbUseUnicodeRSP);

    @VTID(36)
    boolean linkTimeCodeGeneration();

    @VTID(37)
    void linkTimeCodeGeneration(
        boolean codeGen);

    @VTID(38)
    java.lang.String inputs();

    @VTID(39)
    boolean linkLibraryDependencies();

    @VTID(40)
    void linkLibraryDependencies(
        boolean linkLibraryDependencies);

}
