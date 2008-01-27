package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCALinkTool
 */
@IID("{52FCB863-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCALinkTool extends Com4jObject {
    @VTID(7)
    java.lang.String outputBaseFileName();

    @VTID(8)
    @DefaultMethod
    java.lang.String toolName();

    @VTID(9)
    java.lang.String toolKind();

    @VTID(10)
    java.lang.String toolPath();

    @VTID(11)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(12)
    int executionBucket();

    @VTID(13)
    void executionBucket(
        int pVal);

}
