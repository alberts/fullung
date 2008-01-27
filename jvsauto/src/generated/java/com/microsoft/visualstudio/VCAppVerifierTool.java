package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * AppVerifierTool
 */
@IID("{52FCB878-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCAppVerifierTool extends Com4jObject {
    @VTID(7)
    @DefaultMethod
    java.lang.String toolName();

    @VTID(8)
    java.lang.String toolKind();

    @VTID(9)
    com.microsoft.visualstudio.AppVrfBaseLayerOptions heapVerification();

    @VTID(10)
    void heapVerification(
        com.microsoft.visualstudio.AppVrfBaseLayerOptions heapOptions);

    @VTID(11)
    com.microsoft.visualstudio.AppVrfBaseLayerOptions handleVerification();

    @VTID(12)
    void handleVerification(
        com.microsoft.visualstudio.AppVrfBaseLayerOptions options);

    @VTID(13)
    com.microsoft.visualstudio.AppVrfBaseLayerOptions locksVerification();

    @VTID(14)
    void locksVerification(
        com.microsoft.visualstudio.AppVrfBaseLayerOptions options);

    @VTID(15)
    boolean pageHeapConserveMemory();

    @VTID(16)
    void pageHeapConserveMemory(
        boolean conserveMemory);

    @VTID(17)
    com.microsoft.visualstudio.AVPageHeapProtectionDirection pageHeapProtectionLocation();

    @VTID(18)
    void pageHeapProtectionLocation(
        com.microsoft.visualstudio.AVPageHeapProtectionDirection protectionType);

    @VTID(19)
    java.lang.String toolPath();

    @VTID(20)
    java.lang.String get_PropertyOption(
        java.lang.String prop,
        int dispidProp);

    @VTID(21)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(22)
    int executionBucket();

    @VTID(23)
    void executionBucket(
        int pVal);

}
