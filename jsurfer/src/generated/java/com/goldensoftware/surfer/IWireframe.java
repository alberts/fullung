package com.goldensoftware.surfer;

import com4j.IID;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IWireframe Interface
 */
@IID("{B293342C-9788-11D2-9780-00104B6D9C80}")
public interface IWireframe extends com.goldensoftware.surfer.IShape {
    /**
     * Returns the name of the grid file used to create the plot
     */
    @VTID(30)
    java.lang.String gridFile();

    /**
     * Returns/sets the directions to plot lines (a combination of the
     * srfWireDir values)
     */
    @VTID(31)
    int lineDirection();

    /**
     * Returns/sets the directions to plot lines (a combination of the
     * srfWireDir values)
     */
    @VTID(32)
    void lineDirection(int pDir);

    /**
     * Returns/sets the show upper state
     */
    @VTID(33)
    boolean showUpper();

    /**
     * Returns/sets the show upper state
     */
    @VTID(34)
    void showUpper(boolean pShow);

    /**
     * Returns/sets the show lower state
     */
    @VTID(35)
    boolean showLower();

    /**
     * Returns/sets the show lower state
     */
    @VTID(36)
    void showLower(boolean pShow);

    /**
     * Returns/sets the show base state
     */
    @VTID(37)
    boolean showBase();

    /**
     * Returns/sets the show base state
     */
    @VTID(38)
    void showBase(boolean pShow);

    /**
     * Returns/sets the show vertical base lines state
     */
    @VTID(39)
    boolean showVerticalLines();

    /**
     * Returns/sets the show vertical base lines state
     */
    @VTID(40)
    void showVerticalLines(boolean pShow);

    /**
     * Returns/sets the base elevation
     */
    @VTID(41)
    double baseElevation();

    /**
     * Returns/sets the base elevation
     */
    @VTID(42)
    void baseElevation(double pElevation);

    /**
     * Returns/sets the remove hidden lines state
     */
    @VTID(43)
    boolean removeHiddenLines();

    /**
     * Returns/sets the remove hidden lines state
     */
    @VTID(44)
    void removeHiddenLines(boolean pRemove);

    /**
     * Returns/sets the surface plot border style
     */
    @VTID(45)
    com.goldensoftware.surfer.SrfWireBorder border();

    /**
     * Returns/sets the surface plot border style
     */
    @VTID(46)
    void border(com.goldensoftware.surfer.SrfWireBorder pBorder);

    /**
     * Returns the line format object for lines of constant X
     */
    @VTID(47)
    com.goldensoftware.surfer.ILineFormat xLine();

    /**
     * Returns the line format object for lines of constant Y
     */
    @VTID(48)
    com.goldensoftware.surfer.ILineFormat yLine();

    /**
     * Returns the line format object for lines of constant Z
     */
    @VTID(49)
    com.goldensoftware.surfer.ILineFormat zLine();

    /**
     * Returns the line format object for the base
     */
    @VTID(50)
    com.goldensoftware.surfer.ILineFormat baseLine();

    /**
     * Returns the levels object for lines of constant Z
     */
    @VTID(51)
    com.goldensoftware.surfer.ILevels zLevels();

    @VTID(51)
    @ReturnValue(defaultPropertyThrough = {com.goldensoftware.surfer.ILevels.class})
    com.goldensoftware.surfer.ILevel zLevels(int index);

    /**
     * Returns the levels object for color zones
     */
    @VTID(52)
    com.goldensoftware.surfer.ILevels zoneLevels();

    @VTID(52)
    @ReturnValue(defaultPropertyThrough = {com.goldensoftware.surfer.ILevels.class})
    com.goldensoftware.surfer.ILevel zoneLevels(int index);

    /**
     * Returns/sets the line directions to apply color zones to (a combination
     * of the srfWireDir values)
     */
    @VTID(53)
    int zoneDirection();

    /**
     * Returns/sets the line directions to apply color zones to (a combination
     * of the srfWireDir values)
     */
    @VTID(54)
    void zoneDirection(int pDir);

    /**
     * Returns/sets the show color scale state
     */
    @VTID(55)
    boolean showColorScale();

    /**
     * Returns/sets the show color scale state
     */
    @VTID(56)
    void showColorScale(boolean pShow);

    /**
     * Returns the color scale object if enabled
     */
    @VTID(57)
    com.goldensoftware.surfer.IDiscreteColorScale colorScale();

    /**
     * Returns the grid object used to create the plot
     */
    @VTID(58)
    com.goldensoftware.surfer.IGrid grid();

}
