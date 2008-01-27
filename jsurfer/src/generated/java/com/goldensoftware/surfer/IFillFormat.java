package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IFillFormat Interface
 */
@IID("{B2933411-9788-11D2-9780-00104B6D9C80}")
public interface IFillFormat extends Com4jObject {
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
     * Returns/sets the foreground color as an RGB value
     */
    @VTID(9)
    com.goldensoftware.surfer.srfColor foreColor();

    /**
     * Returns/sets the foreground color as an RGB value
     */
    @VTID(10)
    void foreColor(com.goldensoftware.surfer.srfColor pColor);

    /**
     * Returns/sets the foreground color as an RGB value
     * <p>
     * This function was manually added to the generated code.
     */
    @VTID(10)
    void foreColor(int pColor);

    /**
     * Returns/sets the background color as an RGB value
     */
    @VTID(11)
    com.goldensoftware.surfer.srfColor backColor();

    /**
     * Returns/sets the background color as an RGB value
     */
    @VTID(12)
    void backColor(com.goldensoftware.surfer.srfColor pColor);

    /**
     * Returns/sets the background color as an RGB value
     * <p>
     * This function was manually added to the generated code.
     */
    @VTID(12)
    void backColor(int pColor);

    /**
     * Returns/sets the fill pattern
     */
    @VTID(13)
    java.lang.String pattern();

    /**
     * Returns/sets the fill pattern
     */
    @VTID(14)
    void pattern(java.lang.String pPattern);

    /**
     * Returns/sets the background transparency mode
     */
    @VTID(15)
    boolean transparent();

    /**
     * Returns/sets the background transparency mode
     */
    @VTID(16)
    void transparent(boolean pTransparent);

}
