package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCBscMakeTool
 */
@IID("{52FCB867-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCBscMakeTool extends Com4jObject {
    @VTID(7)
    @DefaultMethod
    java.lang.String toolName();

    @VTID(8)
    java.lang.String additionalOptions();

    @VTID(9)
    void additionalOptions(
        java.lang.String options);

    @VTID(10)
    boolean suppressStartupBanner();

    @VTID(11)
    void suppressStartupBanner(
        boolean noLogo);

    @VTID(12)
    java.lang.String outputFile();

    @VTID(13)
    void outputFile(
        java.lang.String out);

    @VTID(14)
    boolean deprecateD1();

    @VTID(15)
    void deprecateD1(
        boolean run);

    @VTID(16)
    java.lang.String toolPath();

    @VTID(17)
    java.lang.String get_PropertyOption(
        java.lang.String prop,
        int dispidProp);

    @VTID(18)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(19)
    java.lang.String toolKind();

    @VTID(20)
    int executionBucket();

    @VTID(21)
    void executionBucket(
        int pVal);

}
