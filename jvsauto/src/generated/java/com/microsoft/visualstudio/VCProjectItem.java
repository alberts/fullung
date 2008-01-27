package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCProjectItem
 */
@IID("{238B5173-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCProjectItem extends Com4jObject {
    @VTID(7)
    boolean matchName(
        java.lang.String nameToMatch,
        boolean fullOnly);

    @VTID(8)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject project();

    @VTID(9)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject parent();

    @VTID(10)
    java.lang.String itemName();

    @VTID(11)
    java.lang.String kind();

    @VTID(12)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

}
