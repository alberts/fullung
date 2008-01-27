package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IGrid Interface
 */
@IID("{B2933434-9788-11D2-9780-00104B6D9C80}")
public interface IGrid extends Com4jObject {
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
     * Returns the number of rows in the grid
     */
    @VTID(9)
    int numRows();

    /**
     * Returns the number of columns in the grid
     */
    @VTID(10)
    int numCols();

    /**
     * Returns/sets the minimum X coordinate of the grid
     */
    @VTID(11)
    double xMin();

    /**
     * Returns/sets the minimum X coordinate of the grid
     */
    @VTID(12)
    void xMin(double pxMin);

    /**
     * Returns/sets the maximum X coordinate of the grid
     */
    @VTID(13)
    double xMax();

    /**
     * Returns/sets the maximum X coordinate of the grid
     */
    @VTID(14)
    void xMax(double pxMax);

    /**
     * Returns/sets the minimum Y coordinate of the grid
     */
    @VTID(15)
    double yMin();

    /**
     * Returns/sets the minimum Y coordinate of the grid
     */
    @VTID(16)
    void yMin(double pyMin);

    /**
     * Returns/sets the maximum Y coordinate of the grid
     */
    @VTID(17)
    double yMax();

    /**
     * Returns/sets the maximum Y coordinate of the grid
     */
    @VTID(18)
    void yMax(double pyMax);

    /**
     * Returns/sets the minimum Z coordinate of the grid
     */
    @VTID(19)
    double zMin();

    /**
     * Returns/sets the minimum Z coordinate of the grid
     */
    @VTID(20)
    void zMin(double pzMin);

    /**
     * Returns/sets the maximum Z coordinate of the grid
     */
    @VTID(21)
    double zMax();

    /**
     * Returns/sets the maximum Z coordinate of the grid
     */
    @VTID(22)
    void zMax(double pzMax);

    /**
     * Returns the size of a grid cell in the X direction (data units)
     */
    @VTID(23)
    double xSize();

    /**
     * Returns the size of a grid cell in the Y direction (data units)
     */
    @VTID(24)
    double ySize();

    /**
     * Returns the filename of the grid (if any)
     */
    @VTID(25)
    java.lang.String fileName();

    /**
     * Returns/sets the blank value
     */
    @VTID(26)
    double blankValue();

    /**
     * Returns/sets the blank value
     */
    @VTID(27)
    void blankValue(double pValue);

    /**
     * Get the value of a node at a specified position
     */
    @VTID(28)
    double getNode(int row, int col);

    /**
     * Set the value of a node at a specified position
     */
    @VTID(29)
    void setNode(int row, int col, double value);

    /**
     * Blank the node at a specified position
     */
    @VTID(30)
    void blankNode(int row, int col);

    /**
     * Returns true if the node at the specified position is blanked
     */
    @VTID(31)
    boolean isBlanked(int row, int col);

    /**
     * Interpolate a value within the grid at a specified position
     */
    @VTID(32)
    double interpolate(double x, double y);

    /**
     * Load the specified grid file
     */
    @VTID(33)
    void loadFile(java.lang.String fileName, boolean headerOnly);

    /**
     * Save the grid to a disk file
     */
    @VTID(34)
    void saveFile(java.lang.String fileName, com.goldensoftware.surfer.SrfGridFormat format);

    /**
     * Recomputes the zMin and zMax fields based on the current node values
     */
    @VTID(35)
    void updateZLimits();

    /**
     * Allocates the specified number of rows and columns
     */
    @VTID(36)
    void allocate(int numRows, int numCols);

}
