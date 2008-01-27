package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCFxCopTool
 */
@IID("{01B8E469-7081-4B35-9D08-994B0E96D8B6}")
public interface VCFxCopTool extends Com4jObject {
    @VTID(7)
    java.lang.String inputAssemblyFileName();

    @VTID(8)
    void inputAssemblyFileName(
        java.lang.String fileName);

    @VTID(9)
    java.lang.String rules();

    @VTID(10)
    void rules(
        java.lang.String fileName);

    @VTID(11)
    java.lang.String ruleAssemblies();

    @VTID(12)
    void ruleAssemblies(
        java.lang.String ruleAssemblies);

    @VTID(13)
    java.lang.String outputFile();

    @VTID(14)
    void outputFile(
        java.lang.String fileName);

    @VTID(15)
    boolean enableFxCop();

    @VTID(16)
    void enableFxCop(
        boolean pbExcludedFromBuild);

    @VTID(17)
    boolean fxCopUseTypeNameInSuppression();

    @VTID(18)
    void fxCopUseTypeNameInSuppression(
        boolean pbFxCopUseTypeNameInSuppression);

    @VTID(19)
    java.lang.String fxCopModuleSuppressionsFile();

    @VTID(20)
    void fxCopModuleSuppressionsFile(
        java.lang.String fileName);

    @VTID(21)
    @DefaultMethod
    java.lang.String toolName();

    @VTID(22)
    java.lang.String toolKind();

    @VTID(23)
    java.lang.String toolPath();

    @VTID(24)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(25)
    int executionBucket();

    @VTID(26)
    void executionBucket(
        int pVal);

}
