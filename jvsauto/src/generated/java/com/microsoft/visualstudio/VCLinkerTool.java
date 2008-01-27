package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCLinkerTool
 */
@IID("{52FCB857-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCLinkerTool extends Com4jObject {
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
    com.microsoft.visualstudio.linkProgressOption showProgress();

    @VTID(13)
    void showProgress(
        com.microsoft.visualstudio.linkProgressOption optSetting);

    @VTID(14)
    java.lang.String version();

    @VTID(15)
    void version(
        java.lang.String version);

    @VTID(16)
    com.microsoft.visualstudio.linkIncrementalType linkIncremental();

    @VTID(17)
    void linkIncremental(
        com.microsoft.visualstudio.linkIncrementalType optSetting);

    @VTID(18)
    boolean suppressStartupBanner();

    @VTID(19)
    void suppressStartupBanner(
        boolean noLogo);

    @VTID(20)
    boolean ignoreImportLibrary();

    @VTID(21)
    void ignoreImportLibrary(
        boolean ignoreImportLib);

    @VTID(22)
    boolean registerOutput();

    @VTID(23)
    void registerOutput(
        boolean doRegister);

    @VTID(24)
    java.lang.String additionalLibraryDirectories();

    @VTID(25)
    void additionalLibraryDirectories(
        java.lang.String libPath);

    @VTID(26)
    boolean linkDLL();

    @VTID(27)
    void linkDLL(
        boolean buildDLL);

    @VTID(28)
    java.lang.String additionalDependencies();

    @VTID(29)
    void additionalDependencies(
        java.lang.String dependencies);

    @VTID(30)
    boolean ignoreAllDefaultLibraries();

    @VTID(31)
    void ignoreAllDefaultLibraries(
        boolean noDefaults);

    @VTID(32)
    java.lang.String ignoreDefaultLibraryNames();

    @VTID(33)
    void ignoreDefaultLibraryNames(
        java.lang.String lib);

    @VTID(34)
    java.lang.String moduleDefinitionFile();

    @VTID(35)
    void moduleDefinitionFile(
        java.lang.String defFile);

    @VTID(36)
    java.lang.String addModuleNamesToAssembly();

    @VTID(37)
    void addModuleNamesToAssembly(
        java.lang.String moduleName);

    @VTID(38)
    java.lang.String embedManagedResourceFile();

    @VTID(39)
    void embedManagedResourceFile(
        java.lang.String res);

    @VTID(40)
    java.lang.String forceSymbolReferences();

    @VTID(41)
    void forceSymbolReferences(
        java.lang.String symbol);

    @VTID(42)
    java.lang.String delayLoadDLLs();

    @VTID(43)
    void delayLoadDLLs(
        java.lang.String dllName);

    @VTID(44)
    java.lang.String midlCommandFile();

    @VTID(45)
    void midlCommandFile(
        java.lang.String midlCmdFile);

    @VTID(46)
    boolean ignoreEmbeddedIDL();

    @VTID(47)
    void ignoreEmbeddedIDL(
        boolean ignoreIDL);

    @VTID(48)
    java.lang.String mergedIDLBaseFileName();

    @VTID(49)
    void mergedIDLBaseFileName(
        java.lang.String idlFile);

    @VTID(50)
    java.lang.String typeLibraryFile();

    @VTID(51)
    void typeLibraryFile(
        java.lang.String tlbFile);

    @VTID(52)
    int typeLibraryResourceID();

    @VTID(53)
    void typeLibraryResourceID(
        int resourceID);

    @VTID(54)
    boolean generateDebugInformation();

    @VTID(55)
    void generateDebugInformation(
        boolean genDebug);

    @VTID(56)
    java.lang.String programDatabaseFile();

    @VTID(57)
    void programDatabaseFile(
        java.lang.String file);

    @VTID(58)
    java.lang.String stripPrivateSymbols();

    @VTID(59)
    void stripPrivateSymbols(
        java.lang.String strippedPDB);

    @VTID(60)
    boolean generateMapFile();

    @VTID(61)
    void generateMapFile(
        boolean map);

    @VTID(62)
    java.lang.String mapFileName();

    @VTID(63)
    void mapFileName(
        java.lang.String mapFile);

    @VTID(64)
    boolean mapExports();

    @VTID(65)
    void mapExports(
        boolean exports);

    @VTID(66)
    com.microsoft.visualstudio.subSystemOption subSystem();

    @VTID(67)
    void subSystem(
        com.microsoft.visualstudio.subSystemOption optSetting);

    @VTID(68)
    int heapReserveSize();

    @VTID(69)
    void heapReserveSize(
        int reserveSize);

    @VTID(70)
    int heapCommitSize();

    @VTID(71)
    void heapCommitSize(
        int commitSize);

    @VTID(72)
    int stackReserveSize();

    @VTID(73)
    void stackReserveSize(
        int reserveSize);

    @VTID(74)
    int stackCommitSize();

    @VTID(75)
    void stackCommitSize(
        int commitSize);

    @VTID(76)
    com.microsoft.visualstudio.addressAwarenessType largeAddressAware();

    @VTID(77)
    void largeAddressAware(
        com.microsoft.visualstudio.addressAwarenessType optSetting);

    @VTID(78)
    com.microsoft.visualstudio.termSvrAwarenessType terminalServerAware();

    @VTID(79)
    void terminalServerAware(
        com.microsoft.visualstudio.termSvrAwarenessType optSetting);

    @VTID(80)
    boolean swapRunFromCD();

    @VTID(81)
    void swapRunFromCD(
        boolean run);

    @VTID(82)
    boolean swapRunFromNet();

    @VTID(83)
    void swapRunFromNet(
        boolean run);

    @VTID(84)
    com.microsoft.visualstudio.driverOption driver();

    @VTID(85)
    void driver(
        com.microsoft.visualstudio.driverOption pDriver);

    @VTID(86)
    com.microsoft.visualstudio.optRefType optimizeReferences();

    @VTID(87)
    void optimizeReferences(
        com.microsoft.visualstudio.optRefType optSetting);

    @VTID(88)
    com.microsoft.visualstudio.optFoldingType enableCOMDATFolding();

    @VTID(89)
    void enableCOMDATFolding(
        com.microsoft.visualstudio.optFoldingType optSetting);

    @VTID(90)
    com.microsoft.visualstudio.optWin98Type optimizeForWindows98();

    @VTID(91)
    void optimizeForWindows98(
        com.microsoft.visualstudio.optWin98Type optSetting);

    @VTID(92)
    java.lang.String functionOrder();

    @VTID(93)
    void functionOrder(
        java.lang.String order);

    @VTID(94)
    com.microsoft.visualstudio.LinkTimeCodeGenerationOption linkTimeCodeGeneration();

    @VTID(95)
    void linkTimeCodeGeneration(
        com.microsoft.visualstudio.LinkTimeCodeGenerationOption codeGen);

    @VTID(96)
    java.lang.String entryPointSymbol();

    @VTID(97)
    void entryPointSymbol(
        java.lang.String entry);

    @VTID(98)
    boolean resourceOnlyDLL();

    @VTID(99)
    void resourceOnlyDLL(
        boolean noEntry);

    @VTID(100)
    boolean setChecksum();

    @VTID(101)
    void setChecksum(
        boolean release);

    @VTID(102)
    java.lang.String baseAddress();

    @VTID(103)
    void baseAddress(
        java.lang.String address);

    @VTID(104)
    boolean turnOffAssemblyGeneration();

    @VTID(105)
    void turnOffAssemblyGeneration(
        boolean noAssembly);

    @VTID(106)
    boolean supportUnloadOfDelayLoadedDLL();

    @VTID(107)
    void supportUnloadOfDelayLoadedDLL(
        boolean supportUnload);

    @VTID(108)
    java.lang.String importLibrary();

    @VTID(109)
    void importLibrary(
        java.lang.String importLib);

    @VTID(110)
    java.lang.String mergeSections();

    @VTID(111)
    void mergeSections(
        java.lang.String merge);

    @VTID(112)
    com.microsoft.visualstudio.machineTypeOption targetMachine();

    @VTID(113)
    void targetMachine(
        com.microsoft.visualstudio.machineTypeOption optSetting);

    @VTID(114)
    boolean profile();

    @VTID(115)
    void profile(
        boolean profile);

    @VTID(116)
    com.microsoft.visualstudio.eCLRThreadAttribute clrThreadAttribute();

    @VTID(117)
    void clrThreadAttribute(
        com.microsoft.visualstudio.eCLRThreadAttribute ta);

    @VTID(118)
    com.microsoft.visualstudio.eCLRImageType clrImageType();

    @VTID(119)
    void clrImageType(
        com.microsoft.visualstudio.eCLRImageType ta);

    @VTID(120)
    java.lang.String toolPath();

    @VTID(121)
    java.lang.String get_PropertyOption(
        java.lang.String propName,
        int propID);

    @VTID(122)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(123)
    java.lang.String toolKind();

    @VTID(124)
    com.microsoft.visualstudio.linkFixedBaseAddress fixedBaseAddress();

    @VTID(125)
    void fixedBaseAddress(
        com.microsoft.visualstudio.linkFixedBaseAddress fixed);

    @VTID(126)
    com.microsoft.visualstudio.linkAssemblyDebug assemblyDebug();

    @VTID(127)
    void assemblyDebug(
        com.microsoft.visualstudio.linkAssemblyDebug assemblyDebug);

    @VTID(128)
    java.lang.String assemblyLinkResource();

    @VTID(129)
    void assemblyLinkResource(
        java.lang.String assemblyLinkResource);

    @VTID(130)
    boolean linkLibraryDependencies();

    @VTID(131)
    void linkLibraryDependencies(
        boolean linkLibraryDependencies);

    @VTID(132)
    int executionBucket();

    @VTID(133)
    void executionBucket(
        int pVal);

    @VTID(134)
    void keyFile(
        java.lang.String keyFile);

    @VTID(135)
    java.lang.String keyFile();

    @VTID(136)
    void keyContainer(
        java.lang.String keyContainer);

    @VTID(137)
    java.lang.String keyContainer();

    @VTID(138)
    void delaySign(
        boolean delaySign);

    @VTID(139)
    boolean delaySign();

    @VTID(140)
    boolean useUnicodeResponseFiles();

    @VTID(141)
    void useUnicodeResponseFiles(
        boolean pbUseUnicodeRSP);

    @VTID(142)
    java.lang.String profileGuidedDatabase();

    @VTID(143)
    void profileGuidedDatabase(
        java.lang.String pbstrDatabaseFile);

    @VTID(144)
    boolean generateManifest();

    @VTID(145)
    void generateManifest(
        boolean generateManifest);

    @VTID(146)
    java.lang.String manifestFile();

    @VTID(147)
    void manifestFile(
        java.lang.String manifestFile);

    @VTID(148)
    java.lang.String additionalManifestDependencies();

    @VTID(149)
    void additionalManifestDependencies(
        java.lang.String dependencies);

    @VTID(150)
    boolean allowIsolation();

    @VTID(151)
    void allowIsolation(
        boolean allowIsolation);

    @VTID(152)
    com.microsoft.visualstudio.linkerErrorReportingType errorReporting();

    @VTID(153)
    void errorReporting(
        com.microsoft.visualstudio.linkerErrorReportingType type);

    @VTID(154)
    boolean useLibraryDependencyInputs();

    @VTID(155)
    void useLibraryDependencyInputs(
        boolean useLibraryDependencyInputs);

    @VTID(156)
    boolean clrUnmanagedCodeCheck();

    @VTID(157)
    void clrUnmanagedCodeCheck(
        boolean bVal);

}
