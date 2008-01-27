package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCXMLDataGeneratorTool
 */
@IID("{52FCB870-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCXMLDataGeneratorTool extends Com4jObject {
    @VTID(7)
    java.lang.String toolName();

    @VTID(8)
    java.lang.String output();

    @VTID(9)
    void output(
        java.lang.String out);

    @VTID(10)
    boolean suppressStartupBanner();

    @VTID(11)
    void suppressStartupBanner(
        boolean suppress);

    @VTID(12)
    java.lang.String namespace();

    @VTID(13)
    void namespace(
        java.lang.String namespace);

    @VTID(14)
    java.lang.String additionalOptions();

    @VTID(15)
    void additionalOptions(
        java.lang.String options);

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
