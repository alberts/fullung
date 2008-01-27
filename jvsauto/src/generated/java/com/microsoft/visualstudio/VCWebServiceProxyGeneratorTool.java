package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCWebServiceProxyGeneratorTool
 */
@IID("{52FCB869-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCWebServiceProxyGeneratorTool extends Com4jObject {
    @VTID(7)
    @DefaultMethod
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
    com.microsoft.visualstudio.genProxyLanguage generatedProxyLanguage();

    @VTID(13)
    void generatedProxyLanguage(
        com.microsoft.visualstudio.genProxyLanguage language);

    @VTID(14)
    java.lang.String additionalOptions();

    @VTID(15)
    void additionalOptions(
        java.lang.String options);

    @VTID(16)
    java.lang.String url();

    @VTID(17)
    void url(
        java.lang.String urlPath);

    @VTID(18)
    java.lang.String toolPath();

    @VTID(19)
    java.lang.String get_PropertyOption(
        java.lang.String prop,
        int dispidProp);

    @VTID(20)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(21)
    java.lang.String toolKind();

    @VTID(22)
    java.lang.String namespace();

    @VTID(23)
    void namespace(
        java.lang.String out);

    @VTID(24)
    java.lang.String references();

    @VTID(25)
    void references(
        java.lang.String out);

    @VTID(26)
    int executionBucket();

    @VTID(27)
    void executionBucket(
        int pVal);

}
