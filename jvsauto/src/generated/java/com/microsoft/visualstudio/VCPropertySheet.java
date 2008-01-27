package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCPropertySheet
 */
@IID("{238B5183-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCPropertySheet extends Com4jObject {
    @VTID(7)
    java.lang.String name();

    @VTID(8)
    void name(
        java.lang.String styleName);

    @VTID(9)
    boolean matchName(
        java.lang.String nameToMatch,
        boolean fullOnly);

    @VTID(10)
    java.lang.String propertySheetName();

    @VTID(11)
    void propertySheetName(
        java.lang.String propertySheetName);

    @VTID(12)
    java.lang.String propertySheetFile();

    @VTID(13)
    void propertySheetFile(
        java.lang.String file);

    @VTID(14)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject tools();

    @VTID(15)
    com.microsoft.visualstudio.IVCCollection fileTools();

    @VTID(15)
    @ReturnValue(type=NativeType.Dispatch,defaultPropertyThrough={com.microsoft.visualstudio.IVCCollection.class})
    com4j.Com4jObject fileTools(
        @MarshalAs(NativeType.VARIANT) java.lang.Object index);

    @VTID(16)
    boolean isDirty();

    @VTID(17)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject propertySheets();

    @VTID(18)
    java.lang.String intermediateDirectory();

    @VTID(19)
    void intermediateDirectory(
        java.lang.String intDir);

    @VTID(20)
    java.lang.String outputDirectory();

    @VTID(21)
    void outputDirectory(
        java.lang.String outDir);

    @VTID(22)
    java.lang.String inheritedPropertySheets();

    @VTID(23)
    void inheritedPropertySheets(
        java.lang.String propertySheetNames);

    @VTID(24)
    boolean deprecateD1();

    @VTID(25)
    void deprecateD1(
        boolean bsc);

    @VTID(26)
    com.microsoft.visualstudio.useOfMfc useOfMfc();

    @VTID(27)
    void useOfMfc(
        com.microsoft.visualstudio.useOfMfc useMfc);

    @VTID(28)
    com.microsoft.visualstudio.useOfATL useOfATL();

    @VTID(29)
    void useOfATL(
        com.microsoft.visualstudio.useOfATL useATL);

    @VTID(30)
    boolean atlMinimizesCRunTimeLibraryUsage();

    @VTID(31)
    void atlMinimizesCRunTimeLibraryUsage(
        boolean useCRT);

    @VTID(32)
    com.microsoft.visualstudio.charSet characterSet();

    @VTID(33)
    void characterSet(
        com.microsoft.visualstudio.charSet optSetting);

    @VTID(34)
    com.microsoft.visualstudio.compileAsManagedOptions managedExtensions();

    @VTID(35)
    void managedExtensions(
        com.microsoft.visualstudio.compileAsManagedOptions managed);

    @VTID(36)
    java.lang.String deleteExtensionsOnClean();

    @VTID(37)
    void deleteExtensionsOnClean(
        java.lang.String extList);

    @VTID(38)
    boolean wholeProgramOptimization();

    @VTID(39)
    void wholeProgramOptimization(
        boolean optimize);

    @VTID(40)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(41)
    java.lang.String propertySheetDirectory();

    @VTID(42)
    java.lang.String buildLogFile();

    @VTID(43)
    void buildLogFile(
        java.lang.String pbstrBuildLogFile);

    @VTID(44)
    com.microsoft.visualstudio.enumFileFormat fileFormat();

    @VTID(45)
    void fileFormat(
        com.microsoft.visualstudio.enumFileFormat fileType);

    @VTID(46)
    java.lang.String fileEncoding();

    @VTID(47)
    void fileEncoding(
        java.lang.String encoding);

    @VTID(48)
    com.microsoft.visualstudio.ConfigurationTypes configurationType();

    @VTID(49)
    void configurationType(
        com.microsoft.visualstudio.ConfigurationTypes configType);

    @VTID(50)
    void clearToolProperty(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject pTool,
        java.lang.String bstrPropertyName);

    @VTID(51)
    void save();

    @VTID(52)
    com.microsoft.visualstudio.IVCCollection userMacros();

    @VTID(52)
    @ReturnValue(type=NativeType.Dispatch,defaultPropertyThrough={com.microsoft.visualstudio.IVCCollection.class})
    com4j.Com4jObject userMacros(
        @MarshalAs(NativeType.VARIANT) java.lang.Object index);

    @VTID(53)
    com.microsoft.visualstudio.VCUserMacro addUserMacro(
        java.lang.String name,
        java.lang.String value);

    @VTID(54)
    void removeUserMacro(
        com.microsoft.visualstudio.VCUserMacro userMacro);

    @VTID(55)
    void removeAllUserMacros();

}
