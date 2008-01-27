package com.goldensoftware.surfer;

import com4j.DefaultValue;
import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.VTID;

/**
 * IPostMap Interface
 */
@IID("{B2933429-9788-11D2-9780-00104B6D9C80}")
public interface IPostMap extends com.goldensoftware.surfer.IShape {
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
     * Returns/sets the column containing the labels (0 if none)
     */
    @VTID(36)
    int labCol();

    /**
     * Returns/sets the column containing the labels (0 if none)
     */
    @VTID(37)
    void labCol(int pCol);

    /**
     * Returns/sets the column containing the symbol type (0 if none)
     */
    @VTID(38)
    int symCol();

    /**
     * Returns/sets the column containing the symbol type (0 if none)
     */
    @VTID(39)
    void symCol(int pCol);

    /**
     * Returns/sets the column containing the symbol angle in degrees (0 if
     * none)
     */
    @VTID(40)
    int angleCol();

    /**
     * Returns/sets the column containing the symbol angle in degrees (0 if
     * none)
     */
    @VTID(41)
    void angleCol(int pCol);

    /**
     * Returns the symbol format object
     */
    @VTID(42)
    com.goldensoftware.surfer.IMarkerFormat symbol();

    /**
     * Returns/sets the symbol angle in degrees
     */
    @VTID(43)
    double symAngle();

    /**
     * Returns/sets the symbol angle in degrees
     */
    @VTID(44)
    void symAngle(double pAngle);

    /**
     * Returns/sets the symbol frequency
     */
    @VTID(45)
    int symFrequency();

    /**
     * Returns/sets the symbol frequency
     */
    @VTID(46)
    void symFrequency(int pFreq);

    /**
     * Returns the symbol scaling method
     */
    @VTID(47)
    com.goldensoftware.surfer.SrfPostSizeType symSizeMethod();

    /**
     * Returns/sets the label line length in page units
     */
    @VTID(48)
    double labelLineLength();

    /**
     * Returns/sets the label line length in page units
     */
    @VTID(49)
    void labelLineLength(double pLength);

    /**
     * Returns the line format object for the label lines
     */
    @VTID(50)
    com.goldensoftware.surfer.ILineFormat labelLine();

    /**
     * Returns/sets the label position method
     */
    @VTID(51)
    com.goldensoftware.surfer.SrfPostPosType labelPos();

    /**
     * Returns/sets the label position method
     */
    @VTID(52)
    void labelPos(com.goldensoftware.surfer.SrfPostPosType pType);

    /**
     * Returns/sets the label position offset in the X direction (page units)
     */
    @VTID(53)
    double labelXOffset();

    /**
     * Returns/sets the label position offset in the X direction (page units)
     */
    @VTID(54)
    void labelXOffset(double pOffset);

    /**
     * Returns/sets the label position offset in the Y direction (page units)
     */
    @VTID(55)
    double labelYOffset();

    /**
     * Returns/sets the label position offset in the Y direction (page units)
     */
    @VTID(56)
    void labelYOffset(double pOffset);

    /**
     * Returns/sets the label angle in degrees
     */
    @VTID(57)
    double labelAngle();

    /**
     * Returns/sets the label angle in degrees
     */
    @VTID(58)
    void labelAngle(double pAngle);

    /**
     * Returns/sets the label plane
     */
    @VTID(59)
    com.goldensoftware.surfer.SrfPostLabelPlane labelPlane();

    /**
     * Returns/sets the label plane
     */
    @VTID(60)
    void labelPlane(com.goldensoftware.surfer.SrfPostLabelPlane pPlane);

    /**
     * Returns the label font format object
     */
    @VTID(61)
    com.goldensoftware.surfer.IFontFormat labelFont();

    /**
     * Returns the label format object
     */
    @VTID(62)
    com.goldensoftware.surfer.ILabelFormat labelFormat();

    /**
     * Returns the worksheet column containing the heights for proportional
     * scaling
     */
    @VTID(63)
    int heightCol();

    /**
     * Returns the first proportional scaling value (height column units)
     */
    @VTID(64)
    double scaleValue1();

    /**
     * Returns the second proportional scaling value (height column units)
     */
    @VTID(65)
    double scaleValue2();

    /**
     * Returns the symbol height when the data equals ScaleValue1 (page units)
     */
    @VTID(66)
    double symbolHeight1();

    /**
     * Returns the symbol height when the data equals ScaleValue2 (page units)
     */
    @VTID(67)
    double symbolHeight2();

    /**
     * Sets the input data file and columns
     */
    @VTID(68)
    void setInputData(java.lang.String dataFileName, @DefaultValue("0")
    int xCol, @DefaultValue("0")
    int yCol, @DefaultValue("0")
    int labCol, @DefaultValue("0")
    int symCol, @DefaultValue("0")
    int angleCol);

    /**
     * Sets the symbol scaling parameters
     */
    @VTID(69)
    void setSymbolScaling(com.goldensoftware.surfer.SrfPostSizeType method, @MarshalAs(NativeType.VARIANT)
    java.lang.Object symbolHeight1, @MarshalAs(NativeType.VARIANT)
    java.lang.Object symbolHeight2, @MarshalAs(NativeType.VARIANT)
    java.lang.Object scaleValue1, @MarshalAs(NativeType.VARIANT)
    java.lang.Object scaleValue2, @DefaultValue("0")
    int heightCol);

}
