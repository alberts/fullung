package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCRuntimeIntegerProperty Interface
 */
@IID("{368263D3-70B8-49CE-B473-46FF42A2E79B}")
public interface VCRuntimeIntegerProperty extends com.microsoft.visualstudio.VCRuntimeProperty {
    @VTID(27)
    java.lang.String _switch();

    @VTID(28)
    void _switch(
        java.lang.String pbstrSwitch);

    @VTID(29)
    int defaultValue();

    @VTID(30)
    void defaultValue(
        int plDefaultValue);

}
