package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCXDCMakeTool
 */
@IID("{52FCB877-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCXDCMakeTool extends Com4jObject {
    @VTID(7)
    @DefaultMethod
    java.lang.String toolName();

    @VTID(8)
    java.lang.String toolKind();

    @VTID(9)
    java.lang.String additionalOptions();

    @VTID(10)
    void additionalOptions(
        java.lang.String options);

    @VTID(11)
    boolean suppressStartupBanner();

    @VTID(12)
    void suppressStartupBanner(
        boolean noLogo);

    @VTID(13)
    boolean validateIntelliSense();

    @VTID(14)
    void validateIntelliSense(
        boolean validate);

    @VTID(15)
    java.lang.String additionalDocumentFiles();

    @VTID(16)
    void additionalDocumentFiles(
        java.lang.String additionalFiles);

    @VTID(17)
    java.lang.String outputDocumentFile();

    @VTID(18)
    void outputDocumentFile(
        java.lang.String outputFile);

    @VTID(19)
    boolean documentLibraryDependencies();

    @VTID(20)
    void documentLibraryDependencies(
        boolean docDependencies);

    @VTID(21)
    boolean useUnicodeResponseFiles();

    @VTID(22)
    void useUnicodeResponseFiles(
        boolean pbUseUnicodeRSP);

    @VTID(23)
    java.lang.String toolPath();

    @VTID(24)
    java.lang.String get_PropertyOption(
        java.lang.String prop,
        int dispidProp);

    @VTID(25)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(26)
    int executionBucket();

    @VTID(27)
    void executionBucket(
        int pVal);

}
