package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCManagedResourceCompilerTool
 */
@IID("{52FCB862-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCManagedResourceCompilerTool extends Com4jObject {
    @VTID(7)
    java.lang.String resourceFileName();

    @VTID(8)
    void resourceFileName(
        java.lang.String fileName);

    @VTID(9)
    java.lang.String outputFileName();

    @VTID(10)
    boolean defaultLocalizedResources();

    @VTID(11)
    java.lang.String additionalOptions();

    @VTID(12)
    void additionalOptions(
        java.lang.String options);

    @VTID(13)
    @DefaultMethod
    java.lang.String toolName();

    @VTID(14)
    java.lang.String toolKind();

    @VTID(15)
    java.lang.String toolPath();

    @VTID(16)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(17)
    int executionBucket();

    @VTID(18)
    void executionBucket(
        int pVal);

}
