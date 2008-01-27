package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCActiveXReference
 */
@IID("{238B5181-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCActiveXReference extends com.microsoft.visualstudio.VCReference {
    @VTID(42)
    java.lang.String controlGUID();

    @VTID(43)
    java.lang.String controlVersion();

    @VTID(44)
    int controlLocale();

    @VTID(45)
    java.lang.String wrapperTool();

    @VTID(46)
    void wrapperTool(
        java.lang.String wrapper);

    @VTID(47)
    java.lang.String controlFullPath();

    @VTID(48)
    java.lang.String typeLibraryName();

    @VTID(49)
    boolean wrapperSuccessfullyGenerated();

    @VTID(50)
    java.lang.String generationErrorMessage();

}
