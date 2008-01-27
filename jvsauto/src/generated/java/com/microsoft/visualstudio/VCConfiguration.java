package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCConfiguration
 */
@IID("{238B5182-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCConfiguration extends Com4jObject {
    @VTID(7)
    java.lang.String name();

    @VTID(8)
    void name(
        java.lang.String cfgName);

    @VTID(9)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject platform();

    @VTID(10)
    java.lang.String outputDirectory();

    @VTID(11)
    void outputDirectory(
        java.lang.String outDir);

    @VTID(12)
    java.lang.String intermediateDirectory();

    @VTID(13)
    void intermediateDirectory(
        java.lang.String intDir);

    @VTID(14)
    void delete();

    @VTID(15)
    void build();

    @VTID(16)
    void rebuild();

    @VTID(17)
    void clean();

    @VTID(18)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject debugSettings();

    @VTID(19)
    java.lang.String primaryOutput();

    @VTID(20)
    java.lang.String importLibrary();

    @VTID(21)
    java.lang.String programDatabase();

    @VTID(22)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject project();

    @VTID(23)
    boolean matchName(
        java.lang.String nameToMatch,
        boolean fullOnly);

    @VTID(24)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject tools();

    @VTID(25)
    com.microsoft.visualstudio.IVCCollection fileTools();

    @VTID(25)
    @ReturnValue(type=NativeType.Dispatch,defaultPropertyThrough={com.microsoft.visualstudio.IVCCollection.class})
    com4j.Com4jObject fileTools(
        @MarshalAs(NativeType.VARIANT) java.lang.Object index);

    @VTID(26)
    java.lang.String configurationName();

    @VTID(27)
    void configurationName(
        java.lang.String cfgName);

    @VTID(28)
    boolean upToDate();

    @VTID(29)
    com.microsoft.visualstudio.ConfigurationTypes configurationType();

    @VTID(30)
    void configurationType(
        com.microsoft.visualstudio.ConfigurationTypes configType);

    @VTID(31)
    void copyTo(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject config);

    @VTID(32)
    java.lang.String inheritedPropertySheets();

    @VTID(33)
    void inheritedPropertySheets(
        java.lang.String propertySheets);

    @VTID(34)
    boolean deprecateD1();

    @VTID(35)
    void deprecateD1(
        boolean bsc);

    @VTID(36)
    com.microsoft.visualstudio.useOfMfc useOfMfc();

    @VTID(37)
    void useOfMfc(
        com.microsoft.visualstudio.useOfMfc useMfc);

    @VTID(38)
    com.microsoft.visualstudio.useOfATL useOfATL();

    @VTID(39)
    void useOfATL(
        com.microsoft.visualstudio.useOfATL useATL);

    @VTID(40)
    boolean atlMinimizesCRunTimeLibraryUsage();

    @VTID(41)
    void atlMinimizesCRunTimeLibraryUsage(
        boolean useCRT);

    @VTID(42)
    com.microsoft.visualstudio.charSet characterSet();

    @VTID(43)
    void characterSet(
        com.microsoft.visualstudio.charSet optSetting);

    @VTID(44)
    com.microsoft.visualstudio.compileAsManagedOptions managedExtensions();

    @VTID(45)
    void managedExtensions(
        com.microsoft.visualstudio.compileAsManagedOptions managed);

    @VTID(46)
    java.lang.String deleteExtensionsOnClean();

    @VTID(47)
    void deleteExtensionsOnClean(
        java.lang.String extList);

    @VTID(48)
    com.microsoft.visualstudio.WholeProgramOptimizationTypes wholeProgramOptimization();

    @VTID(49)
    void wholeProgramOptimization(
        com.microsoft.visualstudio.WholeProgramOptimizationTypes optimize);

    @VTID(50)
    boolean registerOutput();

    @VTID(51)
    java.lang.String evaluate(
        java.lang.String in);

    @VTID(52)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject propertySheets();

    @VTID(53)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(54)
    java.lang.String satelliteDLLs();

    @VTID(55)
    void stopBuild();

    @VTID(56)
    void waitForBuild();

    @VTID(57)
    java.lang.String excludeBuckets();

    @VTID(58)
    void excludeBuckets(
        java.lang.String buckets);

    @VTID(59)
    java.lang.String buildLogFile();

    @VTID(60)
    void buildLogFile(
        java.lang.String pbstrBuildLogFile);

    @VTID(61)
    void clearToolProperty(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject pTool,
        java.lang.String bstrPropertyName);

    @VTID(62)
    void deploy();

    @VTID(63)
    void buildWithPropertySheet(
        com.microsoft.visualstudio.VCPropertySheet pPropertySheet,
        com.microsoft.visualstudio.BuildWithPropertySheetType buildType);

    @VTID(64)
    void buildWithPropertySheetPath(
        java.lang.String propertySheetPath,
        com.microsoft.visualstudio.BuildWithPropertySheetType buildType);

    @VTID(65)
    boolean sqlDeploySource();

    @VTID(66)
    void sqlDeploySource(
        boolean bDeploySrc);

    @VTID(67)
    java.lang.String sqlDebugScript();

    @VTID(68)
    void sqlDebugScript(
        java.lang.String debugScript);

    @VTID(69)
    java.lang.String sqlPreDeployScript();

    @VTID(70)
    void sqlPreDeployScript(
        java.lang.String preDeployScript);

    @VTID(71)
    java.lang.String sqlPostDeployScript();

    @VTID(72)
    void sqlPostDeployScript(
        java.lang.String postDeployScript);

    @VTID(73)
    com.microsoft.visualstudio.eSqlClrPermissionLevel sqlPermissionLevel();

    @VTID(74)
    void sqlPermissionLevel(
        com.microsoft.visualstudio.eSqlClrPermissionLevel permission);

    @VTID(75)
    java.lang.String intrinsicPropertySheets();

    @VTID(76)
    void intrinsicPropertySheets(
        java.lang.String propertySheets);

    @VTID(77)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject deploymentTool();

    @VTID(78)
    java.lang.String sqlAssemblyOwner();

    @VTID(79)
    void sqlAssemblyOwner(
        java.lang.String owner);

    @VTID(80)
    void relink();

}
