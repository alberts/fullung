package com.goldensoftware.surfer;

import com4j.DefaultValue;
import com4j.IID;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IContourMap Interface
 */
@IID("{B2933426-9788-11D2-9780-00104B6D9C80}")
public interface IContourMap extends com.goldensoftware.surfer.IShape {
    /**
     * Returns/sets the name of the grid file used to create the map
     */
    @VTID(30)
    java.lang.String gridFile();

    /**
     * Returns/sets the name of the grid file used to create the map
     */
    @VTID(31)
    void gridFile(java.lang.String pFile);

    /**
     * Returns/sets the fill contours state
     */
    @VTID(32)
    boolean fillContours();

    /**
     * Returns/sets the fill contours state
     */
    @VTID(33)
    void fillContours(boolean pFill);

    /**
     * Returns/sets the show color scale state
     */
    @VTID(34)
    boolean showColorScale();

    /**
     * Returns/sets the show color scale state
     */
    @VTID(35)
    void showColorScale(boolean pShow);

    /**
     * Returns the color scale object if enabled
     */
    @VTID(36)
    com.goldensoftware.surfer.IDiscreteColorScale colorScale();

    /**
     * Returns/sets the smooth contours method
     */
    @VTID(37)
    com.goldensoftware.surfer.SrfConSmoothType smoothContours();

    /**
     * Returns/sets the smooth contours method
     */
    @VTID(38)
    void smoothContours(com.goldensoftware.surfer.SrfConSmoothType pSmooth);

    /**
     * Returns the line format object for blanked regions
     */
    @VTID(39)
    com.goldensoftware.surfer.ILineFormat blankLine();

    /**
     * Returns the fill format object for blanked regions
     */
    @VTID(40)
    com.goldensoftware.surfer.IFillFormat blankFill();

    /**
     * Returns the ContourLevels object
     */
    @VTID(41)
    com.goldensoftware.surfer.ILevels levels();

    @VTID(41)
    @ReturnValue(defaultPropertyThrough = {com.goldensoftware.surfer.ILevels.class})
    com.goldensoftware.surfer.ILevel levels(int index);

    /**
     * Returns/sets the label curve tolerance (must be > 1)
     */
    @VTID(42)
    double labelTolerance();

    /**
     * Returns/sets the label curve tolerance (must be > 1)
     */
    @VTID(43)
    void labelTolerance(double pTol);

    /**
     * Returns/sets the minimum label to label distance in page units
     */
    @VTID(44)
    double labelLabelDist();

    /**
     * Returns/sets the minimum label to label distance in page units
     */
    @VTID(45)
    void labelLabelDist(double pDist);

    /**
     * Returns/sets the minimum label to map edge distance in page units
     */
    @VTID(46)
    double labelEdgeDist();

    /**
     * Returns/sets the minimum label to map edge distance in page units
     */
    @VTID(47)
    void labelEdgeDist(double pDist);

    /**
     * Returns/sets the orient labels uphill state
     */
    @VTID(48)
    boolean orientLabelsUphill();

    /**
     * Returns/sets the orient labels uphill state
     */
    @VTID(49)
    void orientLabelsUphill(boolean pUp);

    /**
     * Returns the label font format object
     */
    @VTID(50)
    com.goldensoftware.surfer.IFontFormat labelFont();

    /**
     * Returns the label format object
     */
    @VTID(51)
    com.goldensoftware.surfer.ILabelFormat labelFormat();

    /**
     * Returns/sets the hachure length in page units
     */
    @VTID(52)
    double hachLength();

    /**
     * Returns/sets the hachure length in page units
     */
    @VTID(53)
    void hachLength(double pLen);

    /**
     * Returns/sets the hachure direction
     */
    @VTID(54)
    com.goldensoftware.surfer.SrfConHachDir hachDirection();

    /**
     * Returns/sets the hachure direction
     */
    @VTID(55)
    void hachDirection(com.goldensoftware.surfer.SrfConHachDir pDir);

    /**
     * Returns/sets the hachure closed contours only state
     */
    @VTID(56)
    boolean hachClosedOnly();

    /**
     * Returns/sets the hachure closed contours only state
     */
    @VTID(57)
    void hachClosedOnly(boolean pClosedOnly);

    /**
     * Returns the line format object for fault traces
     */
    @VTID(58)
    com.goldensoftware.surfer.ILineFormat faultLine();

    /**
     * Returns the grid object used to create the map
     */
    @VTID(59)
    com.goldensoftware.surfer.IGrid grid();

    /**
     * Exports the contour line vertices
     */
    @VTID(60)
    void exportContours(java.lang.String fileName, @DefaultValue("1")
    com.goldensoftware.surfer.SrfConFormat format);

}
