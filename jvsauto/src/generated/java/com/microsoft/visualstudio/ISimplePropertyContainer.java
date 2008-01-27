package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * ISimplePropertyContainer Interface
 */
@IID("{4F0F5FC0-A5C3-4FFE-B2AC-0D4782F0E835}")
public interface ISimplePropertyContainer extends Com4jObject {
    @VTID(3)
    com4j.Com4jObject configuration();

    @VTID(4)
    void putProperty(
        int id,
        @MarshalAs(NativeType.VARIANT) java.lang.Object val);

    @VTID(5)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object getProperty(
        int id);

    @VTID(6)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object getPropertyWithOffset(
        int id,
        int offset);

    @VTID(7)
    java.lang.String evaluate(
        java.lang.String inVal);

}
