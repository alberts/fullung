package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * IDiscreteColorScale Interface
 */
@IID("{B293342D-9788-11D2-9780-00104B6D9C80}")
public interface IDiscreteColorScale extends com.goldensoftware.surfer.IShape {
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
     * Returns/sets the index of the first label
     */
    @VTID(33)
    int firstLabel();

    /**
     * Returns/sets the index of the first label
     */
    @VTID(34)
    void firstLabel(int pFirst);

    /**
     * Returns/sets the label angle in degrees
     */
    @VTID(35)
    double labelAngle();

    /**
     * Returns/sets the label angle in degrees
     */
    @VTID(36)
    void labelAngle(double pAngle);

    /**
     * Returns/sets the label frequency
     */
    @VTID(37)
    int labelFrequency();

    /**
     * Returns/sets the label frequency
     */
    @VTID(38)
    void labelFrequency(int pFreq);

}
