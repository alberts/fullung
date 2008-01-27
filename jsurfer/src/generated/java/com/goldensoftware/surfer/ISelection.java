package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.DefaultMethod;
import com4j.DefaultValue;
import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * ISelection Interface
 */
@IID("{B293340E-9788-11D2-9780-00104B6D9C80}")
public interface ISelection extends Com4jObject, Iterable<Com4jObject> {
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
     * Returns the number of shapes in the selection
     */
    @VTID(9)
    int count();

    @VTID(10)
    java.util.Iterator<Com4jObject> iterator();

    /**
     * Returns an individual shape
     */
    @VTID(11)
    @DefaultMethod
    com.goldensoftware.surfer.IShape item(@MarshalAs(NativeType.VARIANT)
    java.lang.Object index);

    /**
     * Returns/sets the coordinate for the left edge of the selection
     */
    @VTID(12)
    double left();

    /**
     * Returns/sets the coordinate for the left edge of the selection
     */
    @VTID(13)
    void left(double pLeft);

    /**
     * Returns/sets the coordinate for the top edge of the selection
     */
    @VTID(14)
    double top();

    /**
     * Returns/sets the coordinate for the top edge of the selection
     */
    @VTID(15)
    void top(double pTop);

    /**
     * Returns/sets the width of the selection
     */
    @VTID(16)
    double width();

    /**
     * Returns/sets the width of the selection
     */
    @VTID(17)
    void width(double pWidth);

    /**
     * Returns/sets the height of the selection
     */
    @VTID(18)
    double height();

    /**
     * Returns/sets the height of the selection
     */
    @VTID(19)
    void height(double pHeight);

    /**
     * Move the selected objects forward or backward in the Z (drawing) order
     */
    @VTID(20)
    void setZOrder(com.goldensoftware.surfer.SrfZOrder zOrder);

    /**
     * Rotate the selected objects by the specified number of degrees
     */
    @VTID(21)
    void rotate(double angle);

    /**
     * Deselects all objects in the selection
     */
    @VTID(22)
    void deselectAll();

    /**
     * Overlays all maps in the selection
     */
    @VTID(23)
    com.goldensoftware.surfer.IMapFrame overlayMaps();

    /**
     * Stacks all maps in the selection
     */
    @VTID(24)
    boolean stackMaps();

    /**
     * Aligns the objects in the selection
     */
    @VTID(25)
    void align(@DefaultValue("1")
    com.goldensoftware.surfer.SrfHAlign horzAlign, @DefaultValue("1")
    com.goldensoftware.surfer.SrfVAlign vertAlign);

    /**
     * Combine the selected objects into a single composite object
     */
    @VTID(26)
    com.goldensoftware.surfer.IComposite combine();

    /**
     * Cuts the selection to the clipboard
     */
    @VTID(27)
    void cut();

    /**
     * Copies the selection to the clipboard
     */
    @VTID(28)
    void copy();

    /**
     * Deletes the selected objects
     */
    @VTID(29)
    void delete();

}
