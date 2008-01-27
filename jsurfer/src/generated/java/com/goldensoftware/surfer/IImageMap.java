package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * IImageMap Interface
 */
@IID("{B2933424-9788-11D2-9780-00104B6D9C80}")
public interface IImageMap extends com.goldensoftware.surfer.IShape {
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
     * Returns/sets the interpolate pixels state
     */
    @VTID(32)
    boolean interpolatePixels();

    /**
     * Returns/sets the interpolate pixels state
     */
    @VTID(33)
    void interpolatePixels(boolean pInterp);

    /**
     * Returns/sets the dither bitmap state
     */
    @VTID(34)
    boolean ditherBitmap();

    /**
     * Returns/sets the dither bitmap state
     */
    @VTID(35)
    void ditherBitmap(boolean pDither);

    /**
     * Returns/sets the missing data color as an RGB value
     */
    @VTID(36)
    com.goldensoftware.surfer.srfColor missingDataColor();

    /**
     * Returns/sets the missing data color as an RGB value
     */
    @VTID(37)
    void missingDataColor(com.goldensoftware.surfer.srfColor pColor);

    /**
     * Returns/sets the show color scale state
     */
    @VTID(38)
    boolean showColorScale();

    /**
     * Returns/sets the show color scale state
     */
    @VTID(39)
    void showColorScale(boolean pShow);

    /**
     * Returns the color scale object if enabled
     */
    @VTID(40)
    com.goldensoftware.surfer.IContinuousColorScale colorScale();

    /**
     * Returns the color map object
     */
    @VTID(41)
    com.goldensoftware.surfer.IColorMap colorMap();

    /**
     * Returns the grid object used to create the map
     */
    @VTID(42)
    com.goldensoftware.surfer.IGrid grid();

}
