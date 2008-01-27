package com.goldensoftware.surfer;

import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IMapFrame Interface
 */
@IID("{B293341D-9788-11D2-9780-00104B6D9C80}")
public interface IMapFrame extends com.goldensoftware.surfer.IShape {
    /**
     * Returns the axes collection
     */
    @VTID(30)
    com.goldensoftware.surfer.IAxes axes();

    @VTID(30)
    @ReturnValue(defaultPropertyThrough = {com.goldensoftware.surfer.IAxes.class})
    com.goldensoftware.surfer.IAxis axes(@MarshalAs(NativeType.VARIANT)
    java.lang.Object index);

    /**
     * Returns the overlays collection
     */
    @VTID(31)
    com.goldensoftware.surfer.IOverlays overlays();

    @VTID(31)
    @ReturnValue(defaultPropertyThrough = {com.goldensoftware.surfer.IOverlays.class})
    com.goldensoftware.surfer.IShape overlays(@MarshalAs(NativeType.VARIANT)
    java.lang.Object index);

    /**
     * Returns the Scalebars collection
     */
    @VTID(32)
    com.goldensoftware.surfer.IScaleBars scaleBars();

    @VTID(32)
    @ReturnValue(defaultPropertyThrough = {com.goldensoftware.surfer.IScaleBars.class})
    com.goldensoftware.surfer.IScaleBar scaleBars(@MarshalAs(NativeType.VARIANT)
    java.lang.Object index);

    /**
     * Returns the minimum map limit in the X direction
     */
    @VTID(33)
    double xMin();

    /**
     * Returns the maximum map limit in the X direction
     */
    @VTID(34)
    double xMax();

    /**
     * Returns the minimum map limit in the Y direction
     */
    @VTID(35)
    double yMin();

    /**
     * Returns the maximum map limit in the Y direction
     */
    @VTID(36)
    double yMax();

    /**
     * Returns the background line format
     */
    @VTID(37)
    com.goldensoftware.surfer.ILineFormat backgroundLine();

    /**
     * Returns the background fill format
     */
    @VTID(38)
    com.goldensoftware.surfer.IFillFormat backgroundFill();

    /**
     * Returns/sets the 3D rotation in degrees
     */
    @VTID(39)
    double viewRotation();

    /**
     * Returns/sets the 3D rotation in degrees
     */
    @VTID(40)
    void viewRotation(double pRotation);

    /**
     * Returns/sets the 3D tilt in degrees
     */
    @VTID(41)
    double viewTilt();

    /**
     * Returns/sets the 3D tilt in degrees
     */
    @VTID(42)
    void viewTilt(double pTilt);

    /**
     * Returns/sets the 3D eye distance as a percent
     */
    @VTID(43)
    double viewDistance();

    /**
     * Returns/sets the 3D eye distance as a percent
     */
    @VTID(44)
    void viewDistance(double pDistance);

    /**
     * Returns/sets the 3D projection type
     */
    @VTID(45)
    com.goldensoftware.surfer.SrfViewProj viewProjection();

    /**
     * Returns/sets the 3D projection type
     */
    @VTID(46)
    void viewProjection(com.goldensoftware.surfer.SrfViewProj pProj);

    /**
     * Returns/sets the X scale length
     */
    @VTID(47)
    double xLength();

    /**
     * Returns/sets the X scale length
     */
    @VTID(48)
    void xLength(double pxLength);

    /**
     * Returns/sets the Y scale length
     */
    @VTID(49)
    double yLength();

    /**
     * Returns/sets the Y scale length
     */
    @VTID(50)
    void yLength(double pyLength);

    /**
     * Returns/sets the Z scale length
     */
    @VTID(51)
    double zLength();

    /**
     * Returns/sets the Z scale length
     */
    @VTID(52)
    void zLength(double pzLength);

    /**
     * Returns/sets the X scale factor (map units per page unit)
     */
    @VTID(53)
    double xMapPerPU();

    /**
     * Returns/sets the X scale factor (map units per page unit)
     */
    @VTID(54)
    void xMapPerPU(double pxMapPerPU);

    /**
     * Returns/sets the Y scale factor (map units per page unit)
     */
    @VTID(55)
    double yMapPerPU();

    /**
     * Returns/sets the Y scale factor (map units per page unit)
     */
    @VTID(56)
    void yMapPerPU(double pyMapPerPU);

    /**
     * Returns/sets the Z scale factor (map units per page unit)
     */
    @VTID(57)
    double zMapPerPU();

    /**
     * Returns/sets the Z scale factor (map units per page unit)
     */
    @VTID(58)
    void zMapPerPU(double pzMapPerPU);

    /**
     * Returns/sets the 3D field of view in degrees
     */
    @VTID(59)
    double viewFOV();

    /**
     * Returns/sets the 3D field of view in degrees
     */
    @VTID(60)
    void viewFOV(double pFOV);

    /**
     * Set the limits of the map in map units
     */
    @VTID(61)
    void setLimits(double xMin, double xMax, double yMin, double yMax);

    /**
     * Returns/sets the light model for OpenGL maps
     */
    @VTID(62)
    com.goldensoftware.surfer.SrfLightModel lightModel();

    /**
     * Returns/sets the light model for OpenGL maps
     */
    @VTID(63)
    void lightModel(com.goldensoftware.surfer.SrfLightModel pModel);

    /**
     * Returns/sets the light source azimuth angle for OpenGL maps
     */
    @VTID(64)
    double lightAzimuth();

    /**
     * Returns/sets the light source azimuth angle for OpenGL maps
     */
    @VTID(65)
    void lightAzimuth(double pAzimuth);

    /**
     * Returns/sets the light source zenith angle for OpenGL maps
     */
    @VTID(66)
    double lightZenith();

    /**
     * Returns/sets the light source zenith angle for OpenGL maps
     */
    @VTID(67)
    void lightZenith(double pZenith);

    /**
     * Returns/sets the ambient light color as an RGB value
     */
    @VTID(68)
    com.goldensoftware.surfer.srfColor ambientLightColor();

    /**
     * Returns/sets the ambient light color as an RGB value
     */
    @VTID(69)
    void ambientLightColor(com.goldensoftware.surfer.srfColor pColor);

    /**
     * Returns/sets the diffuse light color as an RGB value
     */
    @VTID(70)
    com.goldensoftware.surfer.srfColor diffuseLightColor();

    /**
     * Returns/sets the diffuse light color as an RGB value
     */
    @VTID(71)
    void diffuseLightColor(com.goldensoftware.surfer.srfColor pColor);

    /**
     * Returns/sets the specular light color as an RGB value
     */
    @VTID(72)
    com.goldensoftware.surfer.srfColor specularLightColor();

    /**
     * Returns/sets the specular light color as an RGB value
     */
    @VTID(73)
    void specularLightColor(com.goldensoftware.surfer.srfColor pColor);

    /**
     * Returns/sets the texture map resmapling method for OpenGL maps
     */
    @VTID(74)
    com.goldensoftware.surfer.SrfOverlayResample resampleMethod();

    /**
     * Returns/sets the texture map resmapling method for OpenGL maps
     */
    @VTID(75)
    void resampleMethod(com.goldensoftware.surfer.SrfOverlayResample pResample);

    /**
     * Returns/sets the texture map resolution for OpenGL maps
     */
    @VTID(76)
    int overlayResolution();

    /**
     * Returns/sets the texture map resolution for OpenGL maps
     */
    @VTID(77)
    void overlayResolution(int pRes);

    /**
     * Returns/sets the color modulation for OpenGL maps
     */
    @VTID(78)
    com.goldensoftware.surfer.SrfColorModulation colorModulation();

    /**
     * Returns/sets the color modulation for OpenGL maps
     */
    @VTID(79)
    void colorModulation(com.goldensoftware.surfer.SrfColorModulation pModulation);

}
