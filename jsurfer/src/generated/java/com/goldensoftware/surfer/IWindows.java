package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.DefaultMethod;
import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IWindows Interface
 */
@IID("{B2933406-9788-11D2-9780-00104B6D9C80}")
public interface IWindows extends Com4jObject, Iterable<Com4jObject> {
    /**
     * Returns the application object
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
     * Returns the number of windows
     */
    @VTID(9)
    int count();

    @VTID(10)
    java.util.Iterator<Com4jObject> iterator();

    /**
     * Returns an individual window
     */
    @VTID(11)
    @DefaultMethod
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject item(@MarshalAs(NativeType.VARIANT)
    java.lang.Object index);

    /**
     * Tiles, cascades, or minimizes all windows in the collection
     */
    @VTID(12)
    void arrange(com.goldensoftware.surfer.SrfArrangeType arrangeType);

}
