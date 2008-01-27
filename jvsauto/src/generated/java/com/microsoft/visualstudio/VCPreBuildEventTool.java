package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCPreBuildEventTool
 */
@IID("{52FCB864-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCPreBuildEventTool extends Com4jObject {
    @VTID(7)
    @DefaultMethod
    java.lang.String toolName();

    @VTID(8)
    java.lang.String commandLine();

    @VTID(9)
    void commandLine(
        java.lang.String pVal);

    @VTID(10)
    java.lang.String description();

    @VTID(11)
    void description(
        java.lang.String pVal);

    @VTID(12)
    boolean excludedFromBuild();

    @VTID(13)
    void excludedFromBuild(
        boolean bExcludedFromBuild);

    @VTID(14)
    java.lang.String toolPath();

    @VTID(15)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(16)
    java.lang.String toolKind();

    @VTID(17)
    int executionBucket();

    @VTID(18)
    void executionBucket(
        int pVal);

}
