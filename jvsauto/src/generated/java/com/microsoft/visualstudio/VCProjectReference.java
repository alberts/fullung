package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCProjectReference
 */
@IID("{238B5180-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCProjectReference extends com.microsoft.visualstudio.VCReference {
    @VTID(42)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject referencedProject();

    @VTID(43)
    java.lang.String referencedProjectIdentifier();

    @VTID(44)
    boolean isProjectLoaded();

}
