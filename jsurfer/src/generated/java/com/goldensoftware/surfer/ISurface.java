package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * ISurface Interface
 */
@IID("{B2933436-9788-11D2-9780-00104B6D9C80}")
public interface ISurface extends com.goldensoftware.surfer.IShape {
    /**
     * Returns the grid object used to create the surface
     */
    @VTID(30)
    com.goldensoftware.surfer.IGrid grid();

    /**
     * Returns the upper material color map object
     */
    @VTID(31)
    com.goldensoftware.surfer.IColorMap upperColorMap();

    /**
     * Returns/sets the lower material color as an RGB value
     */
    @VTID(32)
    com.goldensoftware.surfer.srfColor lowerColor();

    /**
     * Returns/sets the lower material color as an RGB value
     */
    @VTID(33)
    void lowerColor(com.goldensoftware.surfer.srfColor pColor);

    /**
     * Returns/sets the upper shininess as a percent
     */
    @VTID(34)
    double upperShininess();

    /**
     * Returns/sets the upper shininess as a percent
     */
    @VTID(35)
    void upperShininess(double pShininess);

    /**
     * Returns/sets the flag to blank missing nodes
     */
    @VTID(36)
    boolean blankMissing();

    /**
     * Returns/sets the flag to blank missing nodes
     */
    @VTID(37)
    void blankMissing(boolean pBlank);

    /**
     * Returns/sets the Z value to use for missing nodes if BlankMissing is
     * false
     */
    @VTID(38)
    double zMissing();

    /**
     * Returns/sets the Z value to use for missing nodes if BlankMissing is
     * false
     */
    @VTID(39)
    void zMissing(double pzMissing);

    /**
     * Returns/sets the show color scale state
     */
    @VTID(40)
    boolean showColorScale();

    /**
     * Returns/sets the show color scale state
     */
    @VTID(41)
    void showColorScale(boolean pShow);

    /**
     * Returns the color scale object if enabled
     */
    @VTID(42)
    com.goldensoftware.surfer.IContinuousColorScale colorScale();

    /**
     * Returns/sets the flag to show overlays on this surface
     */
    @VTID(43)
    boolean showOverlays();

    /**
     * Returns/sets the flag to show overlays on this surface
     */
    @VTID(44)
    void showOverlays(boolean pShow);

    /**
     * Returns/sets the show base state
     */
    @VTID(45)
    boolean showBase();

    /**
     * Returns/sets the show base state
     */
    @VTID(46)
    void showBase(boolean pShow);

    /**
     * Returns the line properties object for the base
     */
    @VTID(47)
    com.goldensoftware.surfer.ILineFormat baseLine();

    /**
     * Returns the fill properties object for the base
     */
    @VTID(48)
    com.goldensoftware.surfer.IGLFillFormat baseFill();

    /**
     * Returns/sets the X mesh line frequency
     */
    @VTID(49)
    int xMeshFreq();

    /**
     * Returns/sets the X mesh line frequency
     */
    @VTID(50)
    void xMeshFreq(int pFreq);

    /**
     * Returns the line properties object for X mesh lines
     */
    @VTID(51)
    com.goldensoftware.surfer.ILineFormat xMeshLine();

    /**
     * Returns/sets the Y mesh line frequency
     */
    @VTID(52)
    int yMeshFreq();

    /**
     * Returns/sets the Y mesh line frequency
     */
    @VTID(53)
    void yMeshFreq(int pFreq);

    /**
     * Returns the line properties object for Y mesh lines
     */
    @VTID(54)
    com.goldensoftware.surfer.ILineFormat yMeshLine();

    /**
     * Returns/sets the surface offset factor
     */
    @VTID(55)
    double meshOffset();

    /**
     * Returns/sets the surface offset factor
     */
    @VTID(56)
    void meshOffset(double pOffset);

}
