package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * ILegend Interface
 */
@IID("{B293342F-9788-11D2-9780-00104B6D9C80}")
public interface ILegend extends com.goldensoftware.surfer.IShape {
    /**
     * Returns/sets the legend title
     */
    @VTID(30)
    java.lang.String title();

    /**
     * Returns/sets the legend title
     */
    @VTID(31)
    void title(java.lang.String pTitle);

    /**
     * Returns the title font format object
     */
    @VTID(32)
    com.goldensoftware.surfer.IFontFormat titleFont();

    /**
     * Returns/sets the frame style
     */
    @VTID(33)
    com.goldensoftware.surfer.SrfLegendFrame frameStyle();

    /**
     * Returns/sets the frame style
     */
    @VTID(34)
    void frameStyle(com.goldensoftware.surfer.SrfLegendFrame pStyle);

    /**
     * Returns/sets the margins in page units
     */
    @VTID(35)
    double margins();

    /**
     * Returns/sets the margins in page units
     */
    @VTID(36)
    void margins(double pMargins);

    /**
     * Returns the line format object for the frame
     */
    @VTID(37)
    com.goldensoftware.surfer.ILineFormat frameLine();

    /**
     * Returns the fill format object for the frame
     */
    @VTID(38)
    com.goldensoftware.surfer.IFillFormat frameFill();

}
