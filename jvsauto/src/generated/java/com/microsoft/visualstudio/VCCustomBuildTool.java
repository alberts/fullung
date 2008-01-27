package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCCustomBuildTool
 */
@IID("{52FCB859-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCCustomBuildTool extends Com4jObject {
    @VTID(7)
    @DefaultMethod
    java.lang.String toolName();

    @VTID(8)
    java.lang.String commandLine();

    @VTID(9)
    void commandLine(
        java.lang.String cmdLine);

    @VTID(10)
    java.lang.String description();

    @VTID(11)
    void description(
        java.lang.String desc);

    @VTID(12)
    java.lang.String outputs();

    @VTID(13)
    void outputs(
        java.lang.String outputs);

    @VTID(14)
    java.lang.String additionalDependencies();

    @VTID(15)
    void additionalDependencies(
        java.lang.String dependencies);

    @VTID(16)
    java.lang.String toolPath();

    @VTID(17)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(18)
    java.lang.String toolKind();

    @VTID(19)
    int executionBucket();

    @VTID(20)
    void executionBucket(
        int pVal);

}
