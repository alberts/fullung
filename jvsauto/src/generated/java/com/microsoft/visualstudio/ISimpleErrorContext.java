package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * ISimpleErrorContext Interface
 */
@IID("{4F0F5FC2-A5C3-4FFE-B2AC-0D4782F0E835}")
public interface ISimpleErrorContext extends Com4jObject {
    @VTID(3)
    void addError(
        java.lang.String text,
        java.lang.String errorID,
        java.lang.String file,
        int line);

    @VTID(4)
    void addWarning(
        java.lang.String text,
        java.lang.String warningID,
        java.lang.String file,
        int line);

    @VTID(5)
    void addInfo(
        java.lang.String text);

}
