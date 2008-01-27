package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * IReliefMap Interface
 */
@IID("{B2933425-9788-11D2-9780-00104B6D9C80}")
public interface IReliefMap extends com.goldensoftware.surfer.IShape {
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
     * Returns/sets the horizontal light position angle in degrees
     */
    @VTID(32)
    double hLightAngle();

    /**
     * Returns/sets the horizontal light position angle in degrees
     */
    @VTID(33)
    void hLightAngle(double pAngle);

    /**
     * Returns/sets the vertical light position angle in degrees
     */
    @VTID(34)
    double vLightAngle();

    /**
     * Returns/sets the vertical light position angle in degrees
     */
    @VTID(35)
    void vLightAngle(double pAngle);

    /**
     * Returns/sets the gradient method
     */
    @VTID(36)
    com.goldensoftware.surfer.SrfSRGradient gradientMethod();

    /**
     * Returns/sets the gradient method
     */
    @VTID(37)
    void gradientMethod(com.goldensoftware.surfer.SrfSRGradient pMethod);

    /**
     * Returns/sets the shading method
     */
    @VTID(38)
    com.goldensoftware.surfer.SrfSRShading shadingMethod();

    /**
     * Returns/sets the shading method
     */
    @VTID(39)
    void shadingMethod(com.goldensoftware.surfer.SrfSRShading pMethod);

    /**
     * Returns/sets the dither bitmap state
     */
    @VTID(40)
    boolean ditherBitmap();

    /**
     * Returns/sets the dither bitmap state
     */
    @VTID(41)
    void ditherBitmap(boolean pDither);

    /**
     * Returns/sets the missing data color as an RGB value
     */
    @VTID(42)
    com.goldensoftware.surfer.srfColor missingDataColor();

    /**
     * Returns/sets the missing data color as an RGB value
     */
    @VTID(43)
    void missingDataColor(com.goldensoftware.surfer.srfColor pColor);

    /**
     * Returns the color map object
     */
    @VTID(44)
    com.goldensoftware.surfer.IColorMap colorMap();

    /**
     * Returns/sets the Z scale factor
     */
    @VTID(45)
    double zScale();

    /**
     * Returns/sets the Z scale factor
     */
    @VTID(46)
    void zScale(double pzScale);

    /**
     * Returns the grid object used to create the map
     */
    @VTID(47)
    com.goldensoftware.surfer.IGrid grid();

}
