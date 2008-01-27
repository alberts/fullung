package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * IScaleBar Interface
 */
@IID("{B2933422-9788-11D2-9780-00104B6D9C80}")
public interface IScaleBar extends com.goldensoftware.surfer.IShape {
    /**
     * Returns/sets the number of cycles
     */
    @VTID(30)
    int numCycles();

    /**
     * Returns/sets the number of cycles
     */
    @VTID(31)
    void numCycles(int pNumCycles);

    /**
     * Returns/sets the spacing between cycles in map units
     */
    @VTID(32)
    double cycleSpacing();

    /**
     * Returns/sets the spacing between cycles in map units
     */
    @VTID(33)
    void cycleSpacing(double pSpacing);

    /**
     * Returns/sets the amount to increment each label by (arbitrary units)
     */
    @VTID(34)
    double labelIncrement();

    /**
     * Returns/sets the amount to increment each label by (arbitrary units)
     */
    @VTID(35)
    void labelIncrement(double pLabInc);

    /**
     * Returns/sets the axis that this scale is linked to
     */
    @VTID(36)
    com.goldensoftware.surfer.SrfTrackingAxis axis();

    /**
     * Returns/sets the axis that this scale is linked to
     */
    @VTID(37)
    void axis(com.goldensoftware.surfer.SrfTrackingAxis pAxis);

    /**
     * Returns the line format object
     */
    @VTID(38)
    com.goldensoftware.surfer.ILineFormat line();

    /**
     * Returns the label font format object
     */
    @VTID(39)
    com.goldensoftware.surfer.IFontFormat labelFont();

    /**
     * Returns the label format object
     */
    @VTID(40)
    com.goldensoftware.surfer.ILabelFormat labelFormat();

    /**
     * Returns/sets the label rotation angle in degrees
     */
    @VTID(41)
    double labelRotation();

    /**
     * Returns/sets the label rotation angle in degrees
     */
    @VTID(42)
    void labelRotation(double pRotation);

}
