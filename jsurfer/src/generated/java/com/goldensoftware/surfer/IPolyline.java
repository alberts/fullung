package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * IPolyline Interface
 */
@IID("{B293341A-9788-11D2-9780-00104B6D9C80}")
public interface IPolyline extends com.goldensoftware.surfer.IShape {
    /**
     * Returns the line format object
     */
    @VTID(30)
    com.goldensoftware.surfer.ILineFormat line();

    /**
     * Returns/sets the start line end style
     */
    @VTID(31)
    com.goldensoftware.surfer.SrfArrowStyle startArrow();

    /**
     * Returns/sets the start line end style
     */
    @VTID(32)
    void startArrow(com.goldensoftware.surfer.SrfArrowStyle pStyle);

    /**
     * Returns/sets the end line end style
     */
    @VTID(33)
    com.goldensoftware.surfer.SrfArrowStyle endArrow();

    /**
     * Returns/sets the end line end style
     */
    @VTID(34)
    void endArrow(com.goldensoftware.surfer.SrfArrowStyle pStyle);

    /**
     * Returns/sets the arrow scale factor
     */
    @VTID(35)
    double arrowScale();

    /**
     * Returns/sets the arrow scale factor
     */
    @VTID(36)
    void arrowScale(double pScale);

    /**
     * Returns/sets the polyline vertices
     */
    @VTID(37)
    double[] vertices();

    /**
     * Returns/sets the polyline vertices
     */
    @VTID(38)
    void vertices(double[] pVertices);

}
