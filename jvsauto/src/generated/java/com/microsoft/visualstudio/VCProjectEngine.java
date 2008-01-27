package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCProjectEngine
 */
@IID("{238B5186-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCProjectEngine extends Com4jObject {
    @VTID(7)
    boolean buildLogging();

    @VTID(8)
    void buildLogging(
        boolean log);

    @VTID(9)
    boolean buildTiming();

    @VTID(10)
    void buildTiming(
        boolean time);

    @VTID(11)
    boolean performanceLogging();

    @VTID(12)
    void performanceLogging(
        boolean time);

    @VTID(13)
    boolean isSystemInclude(
        java.lang.String include);

    @VTID(14)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject events();

    @VTID(15)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject platforms();

    @VTID(16)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject projects();

    @VTID(17)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject createProject(
        java.lang.String projectName);

    @VTID(18)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject loadProject(
        java.lang.String projectName);

    @VTID(19)
    void removeProject(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject project);

    @VTID(20)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject propertySheets();

    @VTID(21)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject loadPropertySheet(
        java.lang.String bstrName);

    @VTID(22)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject createPropertySheet(
        java.lang.String name);

    @VTID(23)
    void removePropertySheet(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject propertySheet);

    @VTID(24)
    java.lang.String evaluate(
        java.lang.String in);

    @VTID(25)
    boolean showEnvironmentInBuildLog();

    @VTID(26)
    void showEnvironmentInBuildLog(
        boolean showEnvironment);

    @VTID(27)
    void addFakeProps(
        int idStart,
        int idEnd,
        int idOffset);

    @VTID(28)
    java.lang.String toolFileSearchPaths();

    @VTID(29)
    void toolFileSearchPaths(
        java.lang.String searchPaths);

    @VTID(30)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject toolFiles();

    @VTID(31)
    com.microsoft.visualstudio.VCToolFile loadToolFile(
        java.lang.String file);

    @VTID(32)
    com.microsoft.visualstudio.VCToolFile createToolFile(
        java.lang.String name);

    @VTID(33)
    boolean validateSchemas();

    @VTID(34)
    void validateSchemas(
        boolean validate);

}
