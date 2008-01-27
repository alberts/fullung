package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * IBaseMap Interface
 */
@IID("{B2933423-9788-11D2-9780-00104B6D9C80}")
public interface IBaseMap extends com.goldensoftware.surfer.IShape {
    /**
     * Returns the line format object
     */
    @VTID(30)
    com.goldensoftware.surfer.ILineFormat line();

    /**
     * Returns the fill format object
     */
    @VTID(31)
    com.goldensoftware.surfer.IFillFormat fill();

    /**
     * Returns the font format object
     */
    @VTID(32)
    com.goldensoftware.surfer.IFontFormat font();

    /**
     * Returns the symbol format object
     */
    @VTID(33)
    com.goldensoftware.surfer.IMarkerFormat symbol();

    /**
     * Returns the minimum X coordinate in map units
     */
    @VTID(34)
    double xMin();

    /**
     * Returns the maximum X coordinate in map units
     */
    @VTID(35)
    double xMax();

    /**
     * Returns the minimum Y coordinate in map units
     */
    @VTID(36)
    double yMin();

    /**
     * Returns the maximum Y coordinate in map units
     */
    @VTID(37)
    double yMax();

    /**
     * Returns the name of the file used to create the basemap
     */
    @VTID(38)
    java.lang.String fileName();

    /**
     * Sets the coordinates for raster basemaps
     */
    @VTID(39)
    void setImageLimits(double xMin, double xMax, double yMin, double yMax);

}
