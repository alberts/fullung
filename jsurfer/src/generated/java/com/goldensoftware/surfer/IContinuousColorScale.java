package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * IContinuousColorScale Interface
 */
@IID("{B293342E-9788-11D2-9780-00104B6D9C80}")
public interface IContinuousColorScale extends com.goldensoftware.surfer.IShape {
    /**
     * Returns the line format object
     */
    @VTID(30)
    com.goldensoftware.surfer.ILineFormat frameLine();

    /**
     * Returns the font format object for labels
     */
    @VTID(31)
    com.goldensoftware.surfer.IFontFormat labelFont();

    /**
     * Returns the label format object
     */
    @VTID(32)
    com.goldensoftware.surfer.ILabelFormat labelFormat();

    /**
     * Returns/sets the label angle in degrees
     */
    @VTID(33)
    double labelAngle();

    /**
     * Returns/sets the label angle in degrees
     */
    @VTID(34)
    void labelAngle(double pAngle);

    /**
     * Returns/sets the minimum label value (data units)
     */
    @VTID(35)
    double labelMinimum();

    /**
     * Returns/sets the minimum label value (data units)
     */
    @VTID(36)
    void labelMinimum(double pValue);

    /**
     * Returns/sets the maximum label value (data units)
     */
    @VTID(37)
    double labelMaximum();

    /**
     * Returns/sets the maximum label value (data units)
     */
    @VTID(38)
    void labelMaximum(double pValue);

    /**
     * Returns/sets the interval between labels (data units)
     */
    @VTID(39)
    double labelInterval();

    /**
     * Returns/sets the interval between labels (data units)
     */
    @VTID(40)
    void labelInterval(double pValue);

}
