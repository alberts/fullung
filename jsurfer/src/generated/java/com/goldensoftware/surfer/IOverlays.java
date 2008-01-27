package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.DefaultMethod;
import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IOverlays Interface
 */
@IID("{B2933420-9788-11D2-9780-00104B6D9C80}")
public interface IOverlays extends Com4jObject, Iterable<Com4jObject> {
    /**
     * Returns the Application object
     */
    @VTID(7)
    com.goldensoftware.surfer.IApplication application();

    /**
     * Returns the parent object
     */
    @VTID(8)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject parent();

    /**
     * Returns the number of overlays
     */
    @VTID(9)
    int count();

    @VTID(10)
    java.util.Iterator<Com4jObject> iterator();

    /**
     * Returns an individual overlay
     */
    @VTID(11)
    @DefaultMethod
    com.goldensoftware.surfer.IShape item(@MarshalAs(NativeType.VARIANT)
    java.lang.Object index);

    /**
     * Break apart the specified overlay into a stand-alone map
     */
    @VTID(12)
    com.goldensoftware.surfer.IMapFrame breakApart(com.goldensoftware.surfer.IShape pOverlay);

}
