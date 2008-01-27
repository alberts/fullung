package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCCLCompilerTool
 */
@IID("{52FCB856-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCCLCompilerTool extends Com4jObject {
    @VTID(7)
    java.lang.String additionalOptions();

    @VTID(8)
    void additionalOptions(
        java.lang.String options);

    @VTID(9)
    @DefaultMethod
    java.lang.String toolName();

    @VTID(10)
    com.microsoft.visualstudio.warningLevelOption warningLevel();

    @VTID(11)
    void warningLevel(
        com.microsoft.visualstudio.warningLevelOption optSetting);

    @VTID(12)
    boolean warnAsError();

    @VTID(13)
    void warnAsError(
        boolean warnAsError);

    @VTID(14)
    boolean suppressStartupBanner();

    @VTID(15)
    void suppressStartupBanner(
        boolean noLogo);

    @VTID(16)
    boolean detect64BitPortabilityProblems();

    @VTID(17)
    void detect64BitPortabilityProblems(
        boolean detectPortabilityProblems);

    @VTID(18)
    com.microsoft.visualstudio.debugOption debugInformationFormat();

    @VTID(19)
    void debugInformationFormat(
        com.microsoft.visualstudio.debugOption optSetting);

    @VTID(20)
    com.microsoft.visualstudio.compileAsManagedOptions compileAsManaged();

    @VTID(21)
    void compileAsManaged(
        com.microsoft.visualstudio.compileAsManagedOptions optSetting);

    @VTID(22)
    java.lang.String additionalIncludeDirectories();

    @VTID(23)
    void additionalIncludeDirectories(
        java.lang.String includePath);

    @VTID(24)
    java.lang.String additionalUsingDirectories();

    @VTID(25)
    void additionalUsingDirectories(
        java.lang.String includePath);

    @VTID(26)
    com.microsoft.visualstudio.optimizeOption optimization();

    @VTID(27)
    void optimization(
        com.microsoft.visualstudio.optimizeOption optSetting);

    @VTID(28)
    com.microsoft.visualstudio.inlineExpansionOption inlineFunctionExpansion();

    @VTID(29)
    void inlineFunctionExpansion(
        com.microsoft.visualstudio.inlineExpansionOption optSetting);

    @VTID(30)
    boolean enableIntrinsicFunctions();

    @VTID(31)
    void enableIntrinsicFunctions(
        boolean enableIntrinsic);

    @VTID(32)
    com.microsoft.visualstudio.favorSizeOrSpeedOption favorSizeOrSpeed();

    @VTID(33)
    void favorSizeOrSpeed(
        com.microsoft.visualstudio.favorSizeOrSpeedOption optSetting);

    @VTID(34)
    boolean omitFramePointers();

    @VTID(35)
    void omitFramePointers(
        boolean optSetting);

    @VTID(36)
    boolean enableFiberSafeOptimizations();

    @VTID(37)
    void enableFiberSafeOptimizations(
        boolean enable);

    @VTID(38)
    boolean wholeProgramOptimization();

    @VTID(39)
    void wholeProgramOptimization(
        boolean wholeProgOp);

    @VTID(40)
    java.lang.String preprocessorDefinitions();

    @VTID(41)
    void preprocessorDefinitions(
        java.lang.String defines);

    @VTID(42)
    boolean ignoreStandardIncludePath();

    @VTID(43)
    void ignoreStandardIncludePath(
        boolean bIgnoreInclPath);

    @VTID(44)
    com.microsoft.visualstudio.preprocessOption generatePreprocessedFile();

    @VTID(45)
    void generatePreprocessedFile(
        com.microsoft.visualstudio.preprocessOption optSetting);

    @VTID(46)
    boolean keepComments();

    @VTID(47)
    void keepComments(
        boolean bkeepComments);

    @VTID(48)
    boolean stringPooling();

    @VTID(49)
    void stringPooling(
        boolean optSetting);

    @VTID(50)
    boolean minimalRebuild();

    @VTID(51)
    void minimalRebuild(
        boolean minimalRebuild);

    @VTID(52)
    com.microsoft.visualstudio.cppExceptionHandling exceptionHandling();

    @VTID(53)
    void exceptionHandling(
        com.microsoft.visualstudio.cppExceptionHandling optSetting);

    @VTID(54)
    com.microsoft.visualstudio.basicRuntimeCheckOption basicRuntimeChecks();

    @VTID(55)
    void basicRuntimeChecks(
        com.microsoft.visualstudio.basicRuntimeCheckOption optSetting);

    @VTID(56)
    boolean smallerTypeCheck();

    @VTID(57)
    void smallerTypeCheck(
        boolean smallerType);

    @VTID(58)
    com.microsoft.visualstudio.runtimeLibraryOption runtimeLibrary();

    @VTID(59)
    void runtimeLibrary(
        com.microsoft.visualstudio.runtimeLibraryOption optSetting);

    @VTID(60)
    com.microsoft.visualstudio.structMemberAlignOption structMemberAlignment();

    @VTID(61)
    void structMemberAlignment(
        com.microsoft.visualstudio.structMemberAlignOption optSetting);

    @VTID(62)
    boolean bufferSecurityCheck();

    @VTID(63)
    void bufferSecurityCheck(
        boolean secure);

    @VTID(64)
    boolean enableFunctionLevelLinking();

    @VTID(65)
    void enableFunctionLevelLinking(
        boolean enable);

    @VTID(66)
    com.microsoft.visualstudio.floatingPointModel floatingPointModel();

    @VTID(67)
    void floatingPointModel(
        com.microsoft.visualstudio.floatingPointModel fp);

    @VTID(68)
    boolean floatingPointExceptions();

    @VTID(69)
    void floatingPointExceptions(
        boolean enable);

    @VTID(70)
    boolean disableLanguageExtensions();

    @VTID(71)
    void disableLanguageExtensions(
        boolean disableExtensions);

    @VTID(72)
    boolean defaultCharIsUnsigned();

    @VTID(73)
    void defaultCharIsUnsigned(
        boolean isUnsigned);

    @VTID(74)
    boolean treatWChar_tAsBuiltInType();

    @VTID(75)
    void treatWChar_tAsBuiltInType(
        boolean builtInType);

    @VTID(76)
    boolean forceConformanceInForLoopScope();

    @VTID(77)
    void forceConformanceInForLoopScope(
        boolean conform);

    @VTID(78)
    boolean runtimeTypeInfo();

    @VTID(79)
    void runtimeTypeInfo(
        boolean rtti);

    @VTID(80)
    boolean openMP();

    @VTID(81)
    void openMP(
        boolean openMP);

    @VTID(82)
    com.microsoft.visualstudio.pchOption usePrecompiledHeader();

    @VTID(83)
    void usePrecompiledHeader(
        com.microsoft.visualstudio.pchOption optSetting);

    @VTID(84)
    java.lang.String precompiledHeaderThrough();

    @VTID(85)
    void precompiledHeaderThrough(
        java.lang.String file);

    @VTID(86)
    java.lang.String precompiledHeaderFile();

    @VTID(87)
    void precompiledHeaderFile(
        java.lang.String pch);

    @VTID(88)
    boolean expandAttributedSource();

    @VTID(89)
    void expandAttributedSource(
        boolean bExpandAttributedSource);

    @VTID(90)
    com.microsoft.visualstudio.asmListingOption assemblerOutput();

    @VTID(91)
    void assemblerOutput(
        com.microsoft.visualstudio.asmListingOption optSetting);

    @VTID(92)
    java.lang.String assemblerListingLocation();

    @VTID(93)
    void assemblerListingLocation(
        java.lang.String name);

    @VTID(94)
    java.lang.String objectFile();

    @VTID(95)
    void objectFile(
        java.lang.String name);

    @VTID(96)
    java.lang.String programDataBaseFileName();

    @VTID(97)
    void programDataBaseFileName(
        java.lang.String name);

    @VTID(98)
    com.microsoft.visualstudio.browseInfoOption browseInformation();

    @VTID(99)
    void browseInformation(
        com.microsoft.visualstudio.browseInfoOption optSetting);

    @VTID(100)
    java.lang.String browseInformationFile();

    @VTID(101)
    void browseInformationFile(
        java.lang.String file);

    @VTID(102)
    com.microsoft.visualstudio.callingConventionOption callingConvention();

    @VTID(103)
    void callingConvention(
        com.microsoft.visualstudio.callingConventionOption optSetting);

    @VTID(104)
    com.microsoft.visualstudio.CompileAsOptions compileAs();

    @VTID(105)
    void compileAs(
        com.microsoft.visualstudio.CompileAsOptions compileAs);

    @VTID(106)
    java.lang.String disableSpecificWarnings();

    @VTID(107)
    void disableSpecificWarnings(
        java.lang.String warnings);

    @VTID(108)
    java.lang.String forcedIncludeFiles();

    @VTID(109)
    void forcedIncludeFiles(
        java.lang.String name);

    @VTID(110)
    java.lang.String forcedUsingFiles();

    @VTID(111)
    void forcedUsingFiles(
        java.lang.String name);

    @VTID(112)
    boolean showIncludes();

    @VTID(113)
    void showIncludes(
        boolean showInc);

    @VTID(114)
    java.lang.String undefinePreprocessorDefinitions();

    @VTID(115)
    void undefinePreprocessorDefinitions(
        java.lang.String undefines);

    @VTID(116)
    boolean undefineAllPreprocessorDefinitions();

    @VTID(117)
    void undefineAllPreprocessorDefinitions(
        boolean undefinePredefinedMacros);

    @VTID(118)
    boolean enablePREfast();

    @VTID(119)
    void enablePREfast(
        boolean pbEnablePREfast);

    @VTID(120)
    java.lang.String toolPath();

    @VTID(121)
    java.lang.String get_PropertyOption(
        java.lang.String prop,
        int dispidProp);

    @VTID(122)
    java.lang.String fullIncludePath();

    @VTID(123)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(124)
    boolean compileOnly();

    @VTID(125)
    void compileOnly(
        boolean compileOnly);

    @VTID(126)
    java.lang.String toolKind();

    @VTID(127)
    com.microsoft.visualstudio.enhancedInstructionSetType enableEnhancedInstructionSet();

    @VTID(128)
    void enableEnhancedInstructionSet(
        com.microsoft.visualstudio.enhancedInstructionSetType setType);

    @VTID(129)
    int executionBucket();

    @VTID(130)
    void executionBucket(
        int pVal);

    @VTID(131)
    boolean useUnicodeResponseFiles();

    @VTID(132)
    void useUnicodeResponseFiles(
        boolean pbUseUnicodeRSP);

    @VTID(133)
    boolean generateXMLDocumentationFiles();

    @VTID(134)
    void generateXMLDocumentationFiles(
        boolean generateDocumentationFiles);

    @VTID(135)
    java.lang.String xmlDocumentationFileName();

    @VTID(136)
    void xmlDocumentationFileName(
        java.lang.String documentFile);

    @VTID(137)
    boolean useFullPaths();

    @VTID(138)
    void useFullPaths(
        boolean useFullPaths);

    @VTID(139)
    boolean omitDefaultLibName();

    @VTID(140)
    void omitDefaultLibName(
        boolean omit);

    @VTID(141)
    com.microsoft.visualstudio.compilerErrorReportingType errorReporting();

    @VTID(142)
    void errorReporting(
        com.microsoft.visualstudio.compilerErrorReportingType type);

}
