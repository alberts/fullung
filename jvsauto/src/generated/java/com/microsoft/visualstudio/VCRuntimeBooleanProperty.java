package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCRuntimeBooleanProperty Interface
 */
@IID("{8667A38D-E190-42EC-B856-A862BA4BAA23}")
public interface VCRuntimeBooleanProperty extends com.microsoft.visualstudio.VCRuntimeProperty {
    @VTID(27)
    java.lang.String _switch();

    @VTID(28)
    void _switch(
        java.lang.String pbstrSwitch);

    @VTID(29)
    boolean defaultValue();

    @VTID(30)
    void defaultValue(
        boolean pbDefaultValue);

}
