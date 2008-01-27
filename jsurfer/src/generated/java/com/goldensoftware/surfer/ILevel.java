package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.DefaultMethod;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * ILevel Interface
 */
@IID("{B2933428-9788-11D2-9780-00104B6D9C80}")
public interface ILevel extends Com4jObject {
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
     * Returns the contour value
     */
    @VTID(9)
    @DefaultMethod
    double value();

    /**
     * Returns the line format object for this level
     */
    @VTID(10)
    com.goldensoftware.surfer.ILineFormat line();

    /**
     * Returns the fill format object for this level
     */
    @VTID(11)
    com.goldensoftware.surfer.IFillFormat fill();

    /**
     * Returns/sets the show label state
     */
    @VTID(12)
    boolean showLabel();

    /**
     * Returns/sets the show label state
     */
    @VTID(13)
    void showLabel(boolean pShowLabel);

    /**
     * Returns/sets the show hachure state
     */
    @VTID(14)
    boolean showHach();

    /**
     * Returns/sets the show hachure state
     */
    @VTID(15)
    void showHach(boolean pShowHach);

}
