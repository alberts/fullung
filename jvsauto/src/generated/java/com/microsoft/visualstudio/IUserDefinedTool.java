package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * IUserDefinedTool Interface
 */
@IID("{4F0F5FBF-A5C3-4FFE-B2AC-0D4782F0E835}")
public interface IUserDefinedTool extends com.microsoft.visualstudio.IGenericUserDefinedTool {
    @VTID(12)
    int defaultBucket();

    @VTID(13)
    java.lang.String defaultFileExtensions();

    @VTID(14)
    java.lang.String outputs();

    @VTID(15)
    int supportsSingleFileMode();

    @VTID(16)
    int supportsBatchMode();

    @VTID(17)
    int supportsTargetMode();

    @VTID(18)
    void setInputs(
        Holder<java.lang.String> bstrInputs,
        int numInputs);

    @VTID(19)
    void exec(
        java.lang.String bstrProjectDirectory,
        com.microsoft.visualstudio.ISimpleErrorContext pErrCtxt);

}
