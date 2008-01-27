package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * IPolygon Interface
 */
@IID("{B293341B-9788-11D2-9780-00104B6D9C80}")
public interface IPolygon extends com.goldensoftware.surfer.IShape {
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
     * Returns the polygon vertices
     */
    @VTID(32)
    double[] vertices();

    /**
     * Returns the number of vertices per sub-polygon
     */
    @VTID(33)
    int[] polyCounts();

    /**
     * Sets the polygon vertices
     */
    @VTID(34)
    void setVertices(double[] vertices, int[] polyCounts);

}
