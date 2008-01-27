package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
    private ClassFactory() {} // instanciation is not allowed


    public static com.microsoft.visualstudio._VCProjectEngineEvents createVCProjectEngineEvents() {
        return COM4J.createInstance( com.microsoft.visualstudio._VCProjectEngineEvents.class, "{FBBF3C65-2428-11D7-8BF6-00B0D03DAA06}" );
    }

    public static com.microsoft.visualstudio.VCProjectEngine createVCProjectEngineObject() {
        return COM4J.createInstance( com.microsoft.visualstudio.VCProjectEngine.class, "{FBBF3C66-2428-11D7-8BF6-00B0D03DAA06}" );
    }
}
