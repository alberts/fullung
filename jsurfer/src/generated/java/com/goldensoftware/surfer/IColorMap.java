package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IColorMap Interface
 */
@IID("{B2933415-9788-11D2-9780-00104B6D9C80}")
public interface IColorMap extends Com4jObject {
    /**
     * Returns the application object
     */
    @VTID(7)
    com.goldensoftware.surfer.IApplication application();

    /**
     * Returns the parent object
     */
    @VTID(8)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject parent();

    /**
     * Returns the node positions (0.0 to 1.0)
     */
    @VTID(9)
    double[] nodePositions();

    /**
     * Returns the node colors
     */
    @VTID(10)
    int[] nodeColors();

    /**
     * Sets the node positions and colors
     */
    @VTID(11)
    void setNodes(double[] positions, int[] colors);

    /**
     * Loads the specified color map file
     */
    @VTID(12)
    void loadFile(java.lang.String fileName);

    /**
     * Saves the color map to the specified file
     */
    @VTID(13)
    void saveFile(java.lang.String fileName);

    /**
     * Converts a position (0.0 to 1.0) to a data value
     */
    @VTID(14)
    double posToDat(double pos);

    /**
     * Converts a data value to a position (0.0 to 1.0)
     */
    @VTID(15)
    double datToPos(double dat);

    /**
     * Set the data limits for the minimum and maximum color
     */
    @VTID(16)
    void setDataLimits(double dataMin, double dataMax);

    /**
     * Returns the data value corresponding to the minimum color
     */
    @VTID(17)
    double dataMin();

    /**
     * Returns the data value corresponding to the maximum color
     */
    @VTID(18)
    double dataMax();

}
