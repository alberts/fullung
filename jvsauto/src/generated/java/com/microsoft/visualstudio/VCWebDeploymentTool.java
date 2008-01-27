package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCWebDeploymentTool
 */
@IID("{52FCB871-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCWebDeploymentTool extends Com4jObject {
    @VTID(7)
    @DefaultMethod
    java.lang.String toolName();

    @VTID(8)
    boolean excludedFromBuild();

    @VTID(9)
    void excludedFromBuild(
        boolean disableDeploy);

    @VTID(10)
    java.lang.String relativePath();

    @VTID(11)
    void relativePath(
        java.lang.String dir);

    @VTID(12)
    java.lang.String additionalFiles();

    @VTID(13)
    void additionalFiles(
        java.lang.String files);

    @VTID(14)
    boolean unloadBeforeCopy();

    @VTID(15)
    void unloadBeforeCopy(
        boolean unloadFirst);

    @VTID(16)
    boolean registerOutput();

    @VTID(17)
    void registerOutput(
        boolean regDLL);

    @VTID(18)
    java.lang.String virtualDirectoryName();

    @VTID(19)
    void virtualDirectoryName(
        java.lang.String virtRoot);

    @VTID(20)
    java.lang.String applicationMappings();

    @VTID(21)
    void applicationMappings(
        java.lang.String mapping);

    @VTID(22)
    com.microsoft.visualstudio.eAppProtectionOption applicationProtection();

    @VTID(23)
    void applicationProtection(
        com.microsoft.visualstudio.eAppProtectionOption option);

    @VTID(24)
    java.lang.String toolPath();

    @VTID(25)
    java.lang.String get_PropertyOption(
        java.lang.String prop,
        int dispidProp);

    @VTID(26)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(27)
    java.lang.String toolKind();

    @VTID(28)
    int executionBucket();

    @VTID(29)
    void executionBucket(
        int pVal);

}
