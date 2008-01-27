package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCNMakeTool
 */
@IID("{52FCB868-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCNMakeTool extends Com4jObject {
    @VTID(7)
    @DefaultMethod
    java.lang.String toolName();

    @VTID(8)
    java.lang.String buildCommandLine();

    @VTID(9)
    void buildCommandLine(
        java.lang.String pVal);

    @VTID(10)
    java.lang.String reBuildCommandLine();

    @VTID(11)
    void reBuildCommandLine(
        java.lang.String pVal);

    @VTID(12)
    java.lang.String cleanCommandLine();

    @VTID(13)
    void cleanCommandLine(
        java.lang.String pVal);

    @VTID(14)
    java.lang.String output();

    @VTID(15)
    void output(
        java.lang.String pVal);

    @VTID(16)
    java.lang.String toolPath();

    @VTID(17)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(18)
    java.lang.String toolKind();

    @VTID(19)
    java.lang.String preprocessorDefinitions();

    @VTID(20)
    void preprocessorDefinitions(
        java.lang.String pVal);

    @VTID(21)
    java.lang.String includeSearchPath();

    @VTID(22)
    void includeSearchPath(
        java.lang.String pVal);

    @VTID(23)
    java.lang.String forcedIncludes();

    @VTID(24)
    void forcedIncludes(
        java.lang.String pVal);

    @VTID(25)
    java.lang.String assemblySearchPath();

    @VTID(26)
    void assemblySearchPath(
        java.lang.String pVal);

    @VTID(27)
    java.lang.String forcedUsingAssemblies();

    @VTID(28)
    void forcedUsingAssemblies(
        java.lang.String pVal);

    @VTID(29)
    com.microsoft.visualstudio.compileAsManagedOptions compileAsManaged();

    @VTID(30)
    void compileAsManaged(
        com.microsoft.visualstudio.compileAsManagedOptions optSetting);

}
