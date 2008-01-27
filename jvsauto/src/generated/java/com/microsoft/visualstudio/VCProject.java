package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCProject
 */
@IID("{238B5174-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCProject extends com.microsoft.visualstudio.VCProjectItem {
    @VTID(13)
    java.lang.String name();

    @VTID(14)
    void name(
        java.lang.String val);

    @VTID(15)
    java.lang.String projectDirectory();

    @VTID(16)
    java.lang.String projectFile();

    @VTID(17)
    void projectFile(
        java.lang.String val);

    @VTID(18)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject platforms();

    @VTID(19)
    void addPlatform(
        java.lang.String platformName);

    @VTID(20)
    void removePlatform(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject platform);

    @VTID(21)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject configurations();

    @VTID(22)
    void addConfiguration(
        java.lang.String configurationName);

    @VTID(23)
    void removeConfiguration(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject config);

    @VTID(24)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject files();

    @VTID(25)
    boolean canAddFile(
        java.lang.String file);

    @VTID(26)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addFile(
        java.lang.String bstrPath);

    @VTID(27)
    void removeFile(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject file);

    @VTID(28)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject filters();

    @VTID(29)
    boolean canAddFilter(
        java.lang.String filter);

    @VTID(30)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addFilter(
        java.lang.String bstrFilterName);

    @VTID(31)
    void removeFilter(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject filter);

    @VTID(32)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject items();

    @VTID(33)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addWebReference(
        java.lang.String url,
        java.lang.String name);

    @VTID(34)
    void save();

    @VTID(35)
    boolean isDirty();

    @VTID(36)
    com.microsoft.visualstudio.enumFileFormat fileFormat();

    @VTID(37)
    void fileFormat(
        com.microsoft.visualstudio.enumFileFormat fileType);

    @VTID(38)
    java.lang.String fileEncoding();

    @VTID(39)
    void fileEncoding(
        java.lang.String encoding);

    @VTID(40)
    void saveProjectOptions(
        com4j.Com4jObject streamUnk);

    @VTID(41)
    void loadProjectOptions(
        com4j.Com4jObject streamUnk);

    @VTID(42)
    java.lang.String sccProjectName();

    @VTID(43)
    void sccProjectName(
        java.lang.String name);

    @VTID(44)
    java.lang.String sccAuxPath();

    @VTID(45)
    void sccAuxPath(
        java.lang.String name);

    @VTID(46)
    java.lang.String sccLocalPath();

    @VTID(47)
    void sccLocalPath(
        java.lang.String name);

    @VTID(48)
    java.lang.String sccProvider();

    @VTID(49)
    void sccProvider(
        java.lang.String name);

    @VTID(50)
    java.lang.String keyword();

    @VTID(51)
    void keyword(
        java.lang.String keyword);

    @VTID(52)
    void ownerKey(
        java.lang.String name);

    @VTID(53)
    java.lang.String ownerKey();

    @VTID(54)
    void projectGUID(
        java.lang.String guid);

    @VTID(55)
    java.lang.String projectGUID();

    @VTID(56)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addAssemblyReference(
        java.lang.String path);

    @VTID(57)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addActiveXReference(
        java.lang.String typeLibGuid,
        int majorVersion,
        int minorVersion,
        int localeID,
        java.lang.String wrapper);

    @VTID(58)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addProjectReference(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject proj);

    @VTID(59)
    boolean canAddAssemblyReference(
        java.lang.String bstrRef);

    @VTID(60)
    boolean canAddActiveXReference(
        java.lang.String typeLibGuid,
        int majorVersion,
        int minorVersion,
        int localeID,
        java.lang.String wrapper);

    @VTID(61)
    boolean canAddProjectReference(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject proj);

    @VTID(62)
    void removeReference(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject pDispRef);

    @VTID(63)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcReferences();

    @VTID(64)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject references();

    @VTID(65)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject referencesConsumableByDesigners();

    @VTID(66)
    void rootNamespace(
        java.lang.String guid);

    @VTID(67)
    java.lang.String rootNamespace();

    @VTID(68)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject object();

    @VTID(69)
    void version(
        Holder<Integer> major,
        Holder<Integer> minor);

    @VTID(70)
    boolean showAllFiles();

    @VTID(71)
    void showAllFiles(
        boolean pbShowAllFiles);

    @VTID(72)
    void addToolFile(
        com.microsoft.visualstudio.VCToolFile toolFile);

    @VTID(73)
    void removeToolFile(
        com.microsoft.visualstudio.VCToolFile toolFile);

    @VTID(74)
    com.microsoft.visualstudio.IVCCollection toolFiles();

    @VTID(74)
    @ReturnValue(type=NativeType.Dispatch,defaultPropertyThrough={com.microsoft.visualstudio.IVCCollection.class})
    com4j.Com4jObject toolFiles(
        @MarshalAs(NativeType.VARIANT) java.lang.Object index);

    @VTID(75)
    void managedDBConnection(
        java.lang.String con);

    @VTID(76)
    java.lang.String managedDBConnection();

    @VTID(77)
    void managedDBProvider(
        java.lang.String con);

    @VTID(78)
    java.lang.String managedDBProvider();

    @VTID(79)
    void makeManagedDBConnection(
        @DefaultValue("0")boolean forceNew);

    @VTID(80)
    java.lang.String assemblyReferenceSearchPaths();

    @VTID(81)
    void assemblyReferenceSearchPaths(
        java.lang.String paths);

    @VTID(82)
    void loadUserFile();

    @VTID(83)
    void saveUserFile();

    @VTID(84)
    void includeHeaderFile(
        java.lang.String headerFile,
        java.lang.String fileName);

}
