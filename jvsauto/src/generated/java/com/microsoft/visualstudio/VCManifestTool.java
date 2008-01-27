package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCManifestTool
 */
@IID("{52FCB876-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCManifestTool extends Com4jObject {
    @VTID(7)
    @DefaultMethod
    java.lang.String toolName();

    @VTID(8)
    java.lang.String toolKind();

    @VTID(9)
    java.lang.String additionalOptions();

    @VTID(10)
    void additionalOptions(
        java.lang.String options);

    @VTID(11)
    boolean suppressStartupBanner();

    @VTID(12)
    void suppressStartupBanner(
        boolean noLogo);

    @VTID(13)
    boolean verboseOutput();

    @VTID(14)
    void verboseOutput(
        boolean verboseOutput);

    @VTID(15)
    boolean generateCatalogFiles();

    @VTID(16)
    void generateCatalogFiles(
        boolean generateCatalogs);

    @VTID(17)
    java.lang.String outputManifestFile();

    @VTID(18)
    void outputManifestFile(
        java.lang.String outputFile);

    @VTID(19)
    boolean updateFileHashes();

    @VTID(20)
    void updateFileHashes(
        boolean updateHashes);

    @VTID(21)
    java.lang.String updateFileHashesSearchPath();

    @VTID(22)
    void updateFileHashesSearchPath(
        java.lang.String updateHashesSearchPath);

    @VTID(23)
    java.lang.String assemblyIdentity();

    @VTID(24)
    void assemblyIdentity(
        java.lang.String identity);

    @VTID(25)
    java.lang.String replacementsFile();

    @VTID(26)
    void replacementsFile(
        java.lang.String replacements);

    @VTID(27)
    java.lang.String typeLibraryFile();

    @VTID(28)
    void typeLibraryFile(
        java.lang.String typeLibrary);

    @VTID(29)
    java.lang.String registrarScriptFile();

    @VTID(30)
    void registrarScriptFile(
        java.lang.String regScript);

    @VTID(31)
    java.lang.String additionalManifestFiles();

    @VTID(32)
    void additionalManifestFiles(
        java.lang.String additionalManifests);

    @VTID(33)
    java.lang.String inputResourceManifests();

    @VTID(34)
    void inputResourceManifests(
        java.lang.String inputManifests);

    @VTID(35)
    boolean embedManifest();

    @VTID(36)
    void embedManifest(
        boolean embedManifest);

    @VTID(37)
    java.lang.String dependencyInformationFile();

    @VTID(38)
    void dependencyInformationFile(
        java.lang.String dependencyFile);

    @VTID(39)
    java.lang.String componentFileName();

    @VTID(40)
    void componentFileName(
        java.lang.String componentFileName);

    @VTID(41)
    java.lang.String manifestResourceFile();

    @VTID(42)
    void manifestResourceFile(
        java.lang.String manifestResourceFile);

    @VTID(43)
    boolean useUnicodeResponseFiles();

    @VTID(44)
    void useUnicodeResponseFiles(
        boolean useUnicodeRSP);

    @VTID(45)
    boolean useFAT32Workaround();

    @VTID(46)
    void useFAT32Workaround(
        boolean useWorkaround);

    @VTID(47)
    java.lang.String toolPath();

    @VTID(48)
    java.lang.String get_PropertyOption(
        java.lang.String prop,
        int dispidProp);

    @VTID(49)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(50)
    int executionBucket();

    @VTID(51)
    void executionBucket(
        int pVal);

}
