package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.DefaultMethod;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IShape Interface
 */
@IID("{B293340D-9788-11D2-9780-00104B6D9C80}")
public interface IShape extends Com4jObject {
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
     * Returns the shape type
     */
    @VTID(9)
    com.goldensoftware.surfer.SrfShapeType type();

    /**
     * Returns/sets the name or id of this shape
     */
    @VTID(10)
    @DefaultMethod
    java.lang.String name();

    /**
     * Returns/sets the name or id of this shape
     */
    @VTID(11)
    @DefaultMethod
    void name(java.lang.String pName);

    /**
     * Returns/sets the visibility state of the shape
     */
    @VTID(12)
    boolean visible();

    /**
     * Returns/sets the visibility state of the shape
     */
    @VTID(13)
    void visible(boolean pVisible);

    /**
     * Returns/sets the selection state of the shape
     */
    @VTID(14)
    boolean selected();

    /**
     * Returns/sets the selection state of the shape
     */
    @VTID(15)
    void selected(boolean pSelected);

    /**
     * Returns/sets the coordinate for the left edge of the shape
     */
    @VTID(16)
    double left();

    /**
     * Returns/sets the coordinate for the left edge of the shape
     */
    @VTID(17)
    void left(double pLeft);

    /**
     * Returns/sets the coordinate for the top edge of the shape
     */
    @VTID(18)
    double top();

    /**
     * Returns/sets the coordinate for the top edge of the shape
     */
    @VTID(19)
    void top(double pTop);

    /**
     * Returns/sets the width of the shape
     */
    @VTID(20)
    double width();

    /**
     * Returns/sets the width of the shape
     */
    @VTID(21)
    void width(double pWidth);

    /**
     * Returns/sets the height of the shape
     */
    @VTID(22)
    double height();

    /**
     * Returns/sets the height of the shape
     */
    @VTID(23)
    void height(double pHeight);

    /**
     * Returns/sets the rotation (in degrees) of the shape
     */
    @VTID(24)
    double rotation();

    /**
     * Returns/sets the rotation (in degrees) of the shape
     */
    @VTID(25)
    void rotation(double pRotation);

    /**
     * Deletes the shape
     */
    @VTID(26)
    void delete();

    /**
     * Move the object forward or backward in the Z (drawing) order
     */
    @VTID(27)
    void setZOrder(com.goldensoftware.surfer.SrfZOrder zOrder);

    /**
     * Select the object
     */
    @VTID(28)
    void select();

    /**
     * Deselect the object
     */
    @VTID(29)
    void deselect();

}
