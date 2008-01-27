package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * IRectangle Interface
 */
@IID("{B2933416-9788-11D2-9780-00104B6D9C80}")
public interface IRectangle extends com.goldensoftware.surfer.IShape {
    /**
     * Returns the line format object
     */
    @VTID(30)
    com.goldensoftware.surfer.ILineFormat line();

    /**
     * Returns the fill format object
     */
    @VTID(31)
    com.goldensoftware.surfer.IFillFormat fill();

    /**
     * Returns/sets the corner rounding radius in the X direction
     */
    @VTID(32)
    double xRadius();

    /**
     * Returns/sets the corner rounding radius in the X direction
     */
    @VTID(33)
    void xRadius(double pxRadius);

    /**
     * Returns/sets the corner rounding radius in the Y direction
     */
    @VTID(34)
    double yRadius();

    /**
     * Returns/sets the corner rounding radius in the Y direction
     */
    @VTID(35)
    void yRadius(double pyRadius);

}
