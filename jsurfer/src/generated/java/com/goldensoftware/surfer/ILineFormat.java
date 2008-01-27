package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * ILineFormat Interface
 */
@IID("{B2933410-9788-11D2-9780-00104B6D9C80}")
public interface ILineFormat extends Com4jObject {
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
     * Returns/sets the line color as an RGB value
     */
    @VTID(9)
    com.goldensoftware.surfer.srfColor foreColor();

    /**
     * Returns/sets the line color as an RGB value
     */
    @VTID(10)
    void foreColor(com.goldensoftware.surfer.srfColor pColor);

    /**
     * Returns/sets the line style
     */
    @VTID(11)
    java.lang.String style();

    /**
     * Returns/sets the line style
     */
    @VTID(12)
    void style(java.lang.String pStyle);

    /**
     * Returns/sets the line width in page units
     */
    @VTID(13)
    double width();

    /**
     * Returns/sets the line width in page units
     */
    @VTID(14)
    void width(double pWidth);

}
