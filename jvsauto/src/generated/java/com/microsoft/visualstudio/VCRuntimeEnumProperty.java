package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCRuntimeEnumProperty Interface
 */
@IID("{9D622602-4147-4F90-B458-C497F08F4301}")
public interface VCRuntimeEnumProperty extends com.microsoft.visualstudio.VCRuntimeProperty {
    @VTID(27)
    com.microsoft.visualstudio.IVCCollection values();

    @VTID(27)
    @ReturnValue(type=NativeType.Dispatch,defaultPropertyThrough={com.microsoft.visualstudio.IVCCollection.class})
    com4j.Com4jObject values(
        @MarshalAs(NativeType.VARIANT) java.lang.Object index);

    @VTID(28)
    int defaultValue();

    @VTID(29)
    void defaultValue(
        int plDefaultValue);

    @VTID(30)
    com.microsoft.visualstudio.VCRuntimeEnumValue addValue(
        int value);

    @VTID(31)
    void removeValue(
        com.microsoft.visualstudio.VCRuntimeEnumValue enumValue);

}
