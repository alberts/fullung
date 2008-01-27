package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.DefaultMethod;
import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * Represents the collection of worksheet windows.
 */
@IID("{00222CC2-2B05-11D2-9F99-482637000000}")
public interface IWksWindows extends Com4jObject, Iterable<Com4jObject> {
    @VTID(7)
    java.util.Iterator<Com4jObject> iterator();

    /**
     * Returns a Window object from the collection given its index or caption
     */
    @VTID(8)
    @DefaultMethod
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject item(@MarshalAs(NativeType.VARIANT)
    java.lang.Object item);

    /**
     * Returns the Application object.
     */
    @VTID(9)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject application();

    /**
     * Returns the worksheet document object.
     */
    @VTID(10)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject parent();

    /**
     * Returns the number of Window objects in the collection.
     */
    @VTID(11)
    int count();

    /**
     * Adds a Window to the window collection (creates a new view of the
     * worksheet).
     */
    @VTID(12)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject add();

    @VTID(13)
    void reserved0();

    @VTID(14)
    void reserved1();

    @VTID(15)
    void reserved2();

    @VTID(16)
    void reserved3();

    @VTID(17)
    void reserved4();

}
