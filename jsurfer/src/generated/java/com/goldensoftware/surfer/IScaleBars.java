package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.DefaultMethod;
import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IScaleBars Interface
 */
@IID("{B2933421-9788-11D2-9780-00104B6D9C80}")
public interface IScaleBars extends Com4jObject, Iterable<Com4jObject> {
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
     * Returns the number of scale bars
     */
    @VTID(9)
    int count();

    @VTID(10)
    java.util.Iterator<Com4jObject> iterator();

    /**
     * Returns an individual scale bar
     */
    @VTID(11)
    @DefaultMethod
    com.goldensoftware.surfer.IScaleBar item(@MarshalAs(NativeType.VARIANT)
    java.lang.Object index);

    /**
     * Adds a new scale bar to the collection
     */
    @VTID(12)
    com.goldensoftware.surfer.IScaleBar add();

}
