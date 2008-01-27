package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * IVCCollection Interface
 */
@IID("{238B5170-2429-11D7-8BF6-00B0D03DAA06}")
public interface IVCCollection extends Com4jObject,Iterable<Com4jObject> {
    @VTID(7)
    java.util.Iterator<Com4jObject> iterator();

    @VTID(8)
    @DefaultMethod
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject item(
        @MarshalAs(NativeType.VARIANT) java.lang.Object index);

    @VTID(9)
    int count();

    @VTID(10)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

}
