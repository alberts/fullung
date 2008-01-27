package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * IPostLegend Interface
 */
@IID("{B2933431-9788-11D2-9780-00104B6D9C80}")
public interface IPostLegend extends com.goldensoftware.surfer.ILegend {
    /**
     * Returns/sets the template used to format legend items
     */
    @VTID(39)
    java.lang.String template();

    /**
     * Returns/sets the template used to format legend items
     */
    @VTID(40)
    void template(java.lang.String pTemplate);

    /**
     * Returns the label font format object
     */
    @VTID(41)
    com.goldensoftware.surfer.IFontFormat labelFont();

    /**
     * Returns the label format object
     */
    @VTID(42)
    com.goldensoftware.surfer.ILabelFormat labelFormat();

    /**
     * Returns/sets the reverse legend item order state
     */
    @VTID(43)
    boolean reverseOrder();

    /**
     * Returns/sets the reverse legend item order state
     */
    @VTID(44)
    void reverseOrder(boolean pReverse);

    /**
     * Returns/sets the legend symbol size method
     */
    @VTID(45)
    com.goldensoftware.surfer.SrfPostLegSymSize symbolSizeMethod();

    /**
     * Returns/sets the legend symbol size method
     */
    @VTID(46)
    void symbolSizeMethod(com.goldensoftware.surfer.SrfPostLegSymSize pMethod);

    /**
     * Returns/sets the legend symbol size in page units
     */
    @VTID(47)
    double symbolSize();

    /**
     * Returns/sets the legend symbol size in page units
     */
    @VTID(48)
    void symbolSize(double pSize);

}
