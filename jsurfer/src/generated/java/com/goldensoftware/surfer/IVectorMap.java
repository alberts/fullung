package com.goldensoftware.surfer;

import com4j.DefaultValue;
import com4j.IID;
import com4j.VTID;

/**
 * IVectorMap Interface
 */
@IID("{B293342B-9788-11D2-9780-00104B6D9C80}")
public interface IVectorMap extends com.goldensoftware.surfer.IShape {
    /**
     * Returns the name of the grid file used to create a single grid map
     */
    @VTID(30)
    java.lang.String gridFile();

    /**
     * Returns the name of the grid file used for aspects in a 2-grid map
     */
    @VTID(31)
    java.lang.String aspectGridFile();

    /**
     * Returns the name of the grid file used for gradients in a 2-grid map
     */
    @VTID(32)
    java.lang.String gradientGridFile();

    /**
     * Returns the coordinate system for 2-grid vector maps
     */
    @VTID(33)
    com.goldensoftware.surfer.SrfVecCoordSys coordinateSystem();

    /**
     * Returns the angle system for 2-grid polar vector maps
     */
    @VTID(34)
    com.goldensoftware.surfer.SrfVecAngleSys angleSystem();

    /**
     * Returns the angle units for 2-grid polar vector maps
     */
    @VTID(35)
    com.goldensoftware.surfer.SrfVecAngleUnits angleUnits();

    /**
     * Returns/sets the arrow symbol index (1-based)
     */
    @VTID(36)
    int symbol();

    /**
     * Returns/sets the arrow symbol index (1-based)
     */
    @VTID(37)
    void symbol(int pSym);

    /**
     * Returns/sets the clip symbols state
     */
    @VTID(38)
    boolean clipSymbols();

    /**
     * Returns/sets the clip symbols state
     */
    @VTID(39)
    void clipSymbols(boolean pShow);

    /**
     * Returns the line format object for the symbols
     */
    @VTID(40)
    com.goldensoftware.surfer.ILineFormat symbolLine();

    /**
     * Returns the fill format object for the symbols
     */
    @VTID(41)
    com.goldensoftware.surfer.IFillFormat symbolFill();

    /**
     * Returns/sets the symbol frequency in the X direction
     */
    @VTID(42)
    int xFrequency();

    /**
     * Returns/sets the symbol frequency in the X direction
     */
    @VTID(43)
    void xFrequency(int pFreq);

    /**
     * Returns/sets the symbol frequency in the Y direction
     */
    @VTID(44)
    int yFrequency();

    /**
     * Returns/sets the symbol frequency in the Y direction
     */
    @VTID(45)
    void yFrequency(int pFreq);

    /**
     * Returns/sets the color scale method
     */
    @VTID(46)
    com.goldensoftware.surfer.SrfVecColorMethod colorScaleMethod();

    /**
     * Returns/sets the color scale method
     */
    @VTID(47)
    void colorScaleMethod(com.goldensoftware.surfer.SrfVecColorMethod pMethod);

    /**
     * Returns/sets the show color scale state
     */
    @VTID(48)
    boolean showColorScale();

    /**
     * Returns/sets the show color scale state
     */
    @VTID(49)
    void showColorScale(boolean pShow);

    /**
     * Returns the color scale object if enabled
     */
    @VTID(50)
    com.goldensoftware.surfer.IContinuousColorScale colorScale();

    /**
     * Returns the color map object
     */
    @VTID(51)
    com.goldensoftware.surfer.IColorMap colorMap();

    /**
     * Returns/sets the name of the grid file used for color scaling
     */
    @VTID(52)
    java.lang.String colorGridFile();

    /**
     * Returns/sets the name of the grid file used for color scaling
     */
    @VTID(53)
    void colorGridFile(java.lang.String pFile);

    /**
     * Returns/sets the symbol origin
     */
    @VTID(54)
    com.goldensoftware.surfer.SrfVecSymOrg symbolOrigin();

    /**
     * Returns/sets the symbol origin
     */
    @VTID(55)
    void symbolOrigin(com.goldensoftware.surfer.SrfVecSymOrg pOrg);

    /**
     * Returns/sets the symbol scaling method
     */
    @VTID(56)
    com.goldensoftware.surfer.SrfVecSymScale symbolScaleMethod();

    /**
     * Returns/sets the symbol scaling method
     */
    @VTID(57)
    void symbolScaleMethod(com.goldensoftware.surfer.SrfVecSymScale pMethod);

    /**
     * Returns the minimum magnitude used for symbol scaling
     */
    @VTID(58)
    double minMagnitude();

    /**
     * Returns the maximum magnitude used for symbol scaling
     */
    @VTID(59)
    double maxMagnitude();

    /**
     * Returns the minimum shaft length in page units
     */
    @VTID(60)
    double minShaftLength();

    /**
     * Returns the maximum shaft length in page units
     */
    @VTID(61)
    double maxShaftLength();

    /**
     * Returns the minimum head length in page units
     */
    @VTID(62)
    double minHeadLength();

    /**
     * Returns the maximum head length in page units
     */
    @VTID(63)
    double maxHeadLength();

    /**
     * Returns the minimum symbol width in page units
     */
    @VTID(64)
    double minSymbolWidth();

    /**
     * Returns the maximum symbol width in page units
     */
    @VTID(65)
    double maxSymbolWidth();

    /**
     * Returns the minimum magnitude of the input data
     */
    @VTID(66)
    double minDataMagnitude();

    /**
     * Returns the maximum magnitude of the input data
     */
    @VTID(67)
    double maxDataMagnitude();

    /**
     * Returns/sets the show legend state
     */
    @VTID(68)
    boolean showLegend();

    /**
     * Returns/sets the show legend state
     */
    @VTID(69)
    void showLegend(boolean pShow);

    /**
     * Returns the legend scale object if enabled
     */
    @VTID(70)
    com.goldensoftware.surfer.IVectorLegend legend();

    /**
     * Sets the input grids and coordinate system
     */
    @VTID(71)
    void setInputGrids(java.lang.String gridFileName1, @DefaultValue("")
    java.lang.String gridFileName2, @DefaultValue("1")
    com.goldensoftware.surfer.SrfVecCoordSys coordSys, @DefaultValue("1")
    com.goldensoftware.surfer.SrfVecAngleSys angleSys, @DefaultValue("1")
    com.goldensoftware.surfer.SrfVecAngleUnits angleUnits);

    /**
     * Sets the vector scaling parameters
     */
    @VTID(72)
    void setScaling(com.goldensoftware.surfer.SrfVecScaleParams type, double minimum, double maximum);

    /**
     * Returns the grid object used for aspects
     */
    @VTID(73)
    com.goldensoftware.surfer.IGrid aspectGrid();

    /**
     * Returns the grid object used for gradients
     */
    @VTID(74)
    com.goldensoftware.surfer.IGrid gradientGrid();

    /**
     * Returns the grid object used for color scaling
     */
    @VTID(75)
    com.goldensoftware.surfer.IGrid colorGrid();

    /**
     * Returns/sets the reverse vector orientation state
     */
    @VTID(76)
    boolean reverseVectors();

    /**
     * Returns/sets the reverse vector orientation state
     */
    @VTID(77)
    void reverseVectors(boolean pReverse);

}
