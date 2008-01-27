package com.goldensoftware.surfer;

import com4j.DefaultValue;
import com4j.IID;
import com4j.VTID;

/**
 * IClassedPostMap Interface
 */
@IID("{B293342A-9788-11D2-9780-00104B6D9C80}")
public interface IClassedPostMap extends com.goldensoftware.surfer.IShape {
    /**
     * Returns/sets the name of the data file used to create the map
     */
    @VTID(30)
    java.lang.String dataFile();

    /**
     * Returns/sets the name of the data file used to create the map
     */
    @VTID(31)
    void dataFile(java.lang.String pFile);

    /**
     * Returns/sets the column containing the X coordinates (1-based)
     */
    @VTID(32)
    int xCol();

    /**
     * Returns/sets the column containing the X coordinates (1-based)
     */
    @VTID(33)
    void xCol(int pCol);

    /**
     * Returns/sets the column containing the Y coordinates (1-based)
     */
    @VTID(34)
    int yCol();

    /**
     * Returns/sets the column containing the Y coordinates (1-based)
     */
    @VTID(35)
    void yCol(int pCol);

    /**
     * Returns/sets the column containing the Z coordinates (1-based)
     */
    @VTID(36)
    int zCol();

    /**
     * Returns/sets the column containing the Z coordinates (1-based)
     */
    @VTID(37)
    void zCol(int pCol);

    /**
     * Returns/sets the column containing the labels (0 if none)
     */
    @VTID(38)
    int labCol();

    /**
     * Returns/sets the column containing the labels (0 if none)
     */
    @VTID(39)
    void labCol(int pCol);

    /**
     * Returns/sets the symbol angle in degrees
     */
    @VTID(40)
    double symAngle();

    /**
     * Returns/sets the symbol angle in degrees
     */
    @VTID(41)
    void symAngle(double pAngle);

    /**
     * Returns/sets the symbol frequency
     */
    @VTID(42)
    int symFrequency();

    /**
     * Returns/sets the symbol frequency
     */
    @VTID(43)
    void symFrequency(int pFreq);

    /**
     * Returns/sets the show legend state
     */
    @VTID(44)
    boolean showLegend();

    /**
     * Returns/sets the show legend state
     */
    @VTID(45)
    void showLegend(boolean pShow);

    /**
     * Returns the legend object if enabled
     */
    @VTID(46)
    com.goldensoftware.surfer.IPostLegend legend();

    /**
     * Returns/sets the label line length in page units
     */
    @VTID(47)
    double labelLineLength();

    /**
     * Returns/sets the label line length in page units
     */
    @VTID(48)
    void labelLineLength(double pLength);

    /**
     * Returns the line format object for the label lines
     */
    @VTID(49)
    com.goldensoftware.surfer.ILineFormat labelLine();

    /**
     * Returns/sets the label position method
     */
    @VTID(50)
    com.goldensoftware.surfer.SrfPostPosType labelPos();

    /**
     * Returns/sets the label position method
     */
    @VTID(51)
    void labelPos(com.goldensoftware.surfer.SrfPostPosType pType);

    /**
     * Returns/sets the label position offset in the X direction (page units)
     */
    @VTID(52)
    double labelXOffset();

    /**
     * Returns/sets the label position offset in the X direction (page units)
     */
    @VTID(53)
    void labelXOffset(double pOffset);

    /**
     * Returns/sets the label position offset in the Y direction (page units)
     */
    @VTID(54)
    double labelYOffset();

    /**
     * Returns/sets the label position offset in the Y direction (page units)
     */
    @VTID(55)
    void labelYOffset(double pOffset);

    /**
     * Returns/sets the label angle in degrees
     */
    @VTID(56)
    double labelAngle();

    /**
     * Returns/sets the label angle in degrees
     */
    @VTID(57)
    void labelAngle(double pAngle);

    /**
     * Returns/sets the label plane
     */
    @VTID(58)
    com.goldensoftware.surfer.SrfPostLabelPlane labelPlane();

    /**
     * Returns/sets the label plane
     */
    @VTID(59)
    void labelPlane(com.goldensoftware.surfer.SrfPostLabelPlane pPlane);

    /**
     * Returns the label font format object
     */
    @VTID(60)
    com.goldensoftware.surfer.IFontFormat labelFont();

    /**
     * Returns the label format object
     */
    @VTID(61)
    com.goldensoftware.surfer.ILabelFormat labelFormat();

    /**
     * Returns/Sets the number of classes
     */
    @VTID(62)
    int numClasses();

    /**
     * Returns/Sets the number of classes
     */
    @VTID(63)
    void numClasses(int pNumClasses);

    /**
     * Returns/Sets the binning method
     */
    @VTID(64)
    com.goldensoftware.surfer.SrfPostBinMethod binningMethod();

    /**
     * Returns/Sets the binning method
     */
    @VTID(65)
    void binningMethod(com.goldensoftware.surfer.SrfPostBinMethod pMethod);

    /**
     * Returns the lower limit of the specified bin
     */
    @VTID(66)
    double binLowerLimit(int index);

    /**
     * Returns the upper limit of the specified bin
     */
    @VTID(67)
    double binUpperLimit(int index);

    /**
     * Returns the symbol format object for the specified bin
     */
    @VTID(68)
    com.goldensoftware.surfer.IMarkerFormat binSymbol(int index);

    /**
     * Returns the percentage of data in this bin
     */
    @VTID(69)
    double binPercent(int index);

    /**
     * Returns the number of data values in this bin
     */
    @VTID(70)
    int binCount(int index);

    /**
     * Sets the input data file and columns
     */
    @VTID(71)
    void setInputData(java.lang.String dataFileName, @DefaultValue("0")
    int xCol, @DefaultValue("0")
    int yCol, @DefaultValue("0")
    int zCol, @DefaultValue("0")
    int labCol);

    /**
     * Sets the lower and upper limits of each bin (2 elements per bin)
     */
    @VTID(72)
    void setBinLimits(double[] limits);

}
