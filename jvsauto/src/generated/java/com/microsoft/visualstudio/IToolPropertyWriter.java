package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * IToolPropertyWriter Interface
 */
@IID("{4F0F5FC1-A5C3-4FFE-B2AC-0D4782F0E835}")
public interface IToolPropertyWriter extends Com4jObject {
    @VTID(3)
    void writeProperty(
        java.lang.String name,
        java.lang.String value);

}
