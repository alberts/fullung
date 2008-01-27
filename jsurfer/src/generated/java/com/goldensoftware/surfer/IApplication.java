package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.DefaultMethod;
import com4j.DefaultValue;
import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IApplication Interface
 */
@IID("{B2933401-9788-11D2-9780-00104B6D9C80}")
public interface IApplication extends Com4jObject {
    /**
     * Returns the application object
     */
    @VTID(7)
    com.goldensoftware.surfer.IApplication application();

    /**
     * Returns this object
     */
    @VTID(8)
    com.goldensoftware.surfer.IApplication parent();

    /**
     * Full path and name of the application
     */
    @VTID(9)
    java.lang.String fullName();

    /**
     * Returns the path of the application (no filename)
     */
    @VTID(10)
    java.lang.String path();

    /**
     * Get the default path for opening files
     */
    @VTID(11)
    java.lang.String defaultFilePath();

    /**
     * Get the default path for opening files
     */
    @VTID(12)
    void defaultFilePath(java.lang.String pVal);

    /**
     * Returns/sets the application window visibility
     */
    @VTID(13)
    boolean visible();

    /**
     * Returns/sets the application window visibility
     */
    @VTID(14)
    void visible(boolean pbVisible);

    /**
     * Returns the active document object
     */
    @VTID(15)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject activeDocument();

    /**
     * Returns the active window object
     */
    @VTID(16)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject activeWindow();

    /**
     * Returns the Documents collection
     */
    @VTID(17)
    com.goldensoftware.surfer.IDocuments documents();

    @VTID(17)
    @ReturnValue(type = NativeType.Dispatch, defaultPropertyThrough = {com.goldensoftware.surfer.IDocuments.class})
    com4j.Com4jObject documents(@MarshalAs(NativeType.VARIANT)
    java.lang.Object index);

    /**
     * Returns/sets the main window title
     */
    @VTID(18)
    java.lang.String caption();

    /**
     * Returns/sets the main window title
     */
    @VTID(19)
    void caption(java.lang.String pVal);

    /**
     * Returns the application version
     */
    @VTID(20)
    java.lang.String version();

    /**
     * Returns the Windows collection
     */
    @VTID(21)
    com.goldensoftware.surfer.IWindows windows();

    @VTID(21)
    @ReturnValue(type = NativeType.Dispatch, defaultPropertyThrough = {com.goldensoftware.surfer.IWindows.class})
    com4j.Com4jObject windows(@MarshalAs(NativeType.VARIANT)
    java.lang.Object index);

    /**
     * Returns/sets the coordinate for the left edge of the window
     */
    @VTID(22)
    int left();

    /**
     * Returns/sets the coordinate for the left edge of the window
     */
    @VTID(23)
    void left(int pVal);

    /**
     * Returns/sets the coordinate for the top edge of the window
     */
    @VTID(24)
    int top();

    /**
     * Returns/sets the coordinate for the top edge of the window
     */
    @VTID(25)
    void top(int pVal);

    /**
     * Returns/sets the width of the window
     */
    @VTID(26)
    int width();

    /**
     * Returns/sets the width of the window
     */
    @VTID(27)
    void width(int pVal);

    /**
     * Returns/sets the height of the window
     */
    @VTID(28)
    int height();

    /**
     * Returns/sets the height of the window
     */
    @VTID(29)
    void height(int pVal);

    /**
     * Returns the status bar visibility state
     */
    @VTID(30)
    boolean showStatusBar();

    /**
     * Returns the status bar visibility state
     */
    @VTID(31)
    void showStatusBar(boolean pbVisible);

    /**
     * Terminates the application
     */
    @VTID(32)
    void quit();

    /**
     * Test method
     */
    @VTID(33)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject test(@MarshalAs(NativeType.VARIANT)
    java.lang.Object vario);

    /**
     * Returns the name of the application (no path)
     */
    @VTID(34)
    @DefaultMethod
    java.lang.String name();

    /**
     * Create a variogram component object
     */
    @VTID(35)
    com.goldensoftware.surfer.IVarioComponent newVarioComponent(com.goldensoftware.surfer.SrfVarioType varioType,
            @DefaultValue("1")
            double param1, @DefaultValue("1")
            double param2, @DefaultValue("1")
            double power, @DefaultValue("1")
            double anisotropyRatio, @DefaultValue("0")
            double anisotropyAngle);

    /**
     * Creates a grid from irregularly spaced XYZ data
     */
    @VTID(36)
    boolean gridData(java.lang.String dataFile, @DefaultValue("0")
    int xCol, @DefaultValue("0")
    int yCol, @DefaultValue("0")
    int zCol, @MarshalAs(NativeType.VARIANT)
    java.lang.Object exclusionFilter, @MarshalAs(NativeType.VARIANT)
    java.lang.Object dupMethod, @MarshalAs(NativeType.VARIANT)
    java.lang.Object xDupTol, @MarshalAs(NativeType.VARIANT)
    java.lang.Object yDupTol, @MarshalAs(NativeType.VARIANT)
    java.lang.Object numCols, @MarshalAs(NativeType.VARIANT)
    java.lang.Object numRows, @MarshalAs(NativeType.VARIANT)
    java.lang.Object xMin, @MarshalAs(NativeType.VARIANT)
    java.lang.Object xMax, @MarshalAs(NativeType.VARIANT)
    java.lang.Object yMin, @MarshalAs(NativeType.VARIANT)
    java.lang.Object yMax, @MarshalAs(NativeType.VARIANT)
    java.lang.Object algorithm, @MarshalAs(NativeType.VARIANT)
    java.lang.Object showReport, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchEnable, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchNumSectors, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchRad1, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchRad2, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchAngle, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchMinData, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchDataPerSect, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchMaxEmpty, @MarshalAs(NativeType.VARIANT)
    java.lang.Object faultFileName, @MarshalAs(NativeType.VARIANT)
    java.lang.Object breakFileName, @MarshalAs(NativeType.VARIANT)
    java.lang.Object anisotropyRatio, @MarshalAs(NativeType.VARIANT)
    java.lang.Object anisotropyAngle, @MarshalAs(NativeType.VARIANT)
    java.lang.Object idPower, @MarshalAs(NativeType.VARIANT)
    java.lang.Object idSmoothing, @MarshalAs(NativeType.VARIANT)
    java.lang.Object krigType, @MarshalAs(NativeType.VARIANT)
    java.lang.Object krigDriftType, @MarshalAs(NativeType.VARIANT)
    java.lang.Object krigStdDevGrid, @MarshalAs(NativeType.VARIANT)
    java.lang.Object krigVariogram, @MarshalAs(NativeType.VARIANT)
    java.lang.Object mcMaxResidual, @MarshalAs(NativeType.VARIANT)
    java.lang.Object mcMaxIterations, @MarshalAs(NativeType.VARIANT)
    java.lang.Object mcInternalTension, @MarshalAs(NativeType.VARIANT)
    java.lang.Object mcBoundaryTension, @MarshalAs(NativeType.VARIANT)
    java.lang.Object mcRelaxationFactor, @MarshalAs(NativeType.VARIANT)
    java.lang.Object shepSmoothFactor, @MarshalAs(NativeType.VARIANT)
    java.lang.Object shepQuadraticNeighbors, @MarshalAs(NativeType.VARIANT)
    java.lang.Object shepWeightingNeighbors, @MarshalAs(NativeType.VARIANT)
    java.lang.Object shepRange1, @MarshalAs(NativeType.VARIANT)
    java.lang.Object shepRange2, @MarshalAs(NativeType.VARIANT)
    java.lang.Object regrMaxXOrder, @MarshalAs(NativeType.VARIANT)
    java.lang.Object regrMaxYOrder, @MarshalAs(NativeType.VARIANT)
    java.lang.Object regrMaxTotalOrder, @MarshalAs(NativeType.VARIANT)
    java.lang.Object rbBasisType, @MarshalAs(NativeType.VARIANT)
    java.lang.Object rbrSquared, @DefaultValue("")
    java.lang.String outGrid, @DefaultValue("3")
    com.goldensoftware.surfer.SrfGridFormat outFmt, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchMaxData, @MarshalAs(NativeType.VARIANT)
    java.lang.Object krigStdDevFormat, @MarshalAs(NativeType.VARIANT)
    java.lang.Object dataMetric, @MarshalAs(NativeType.VARIANT)
    java.lang.Object localPolyOrder, @MarshalAs(NativeType.VARIANT)
    java.lang.Object localPolyPower, @MarshalAs(NativeType.VARIANT)
    java.lang.Object triangleFileName);

    /**
     * Creates a new grid from the specified function
     */
    @VTID(37)
    boolean gridFunction(java.lang.String function, double xMin, double xMax, double xInc, double yMin, double yMax,
            double yInc, @DefaultValue("")
            java.lang.String outGrid, @DefaultValue("3")
            com.goldensoftware.surfer.SrfGridFormat outFmt);

    /**
     * Grid-to-grid and grid-to-constant math operations
     */
    @VTID(38)
    boolean gridMath(java.lang.String function, java.lang.String inGridA, @DefaultValue("")
    java.lang.String inGridB, @DefaultValue("")
    java.lang.String outGridC, @DefaultValue("3")
    com.goldensoftware.surfer.SrfGridFormat outFmt);

    /**
     * Smooth an existing grid using a smoothing matrix (deprecated - use
     * GridFilter instead)
     */
    @VTID(39)
    boolean gridMatrixSmooth(java.lang.String inGrid, @DefaultValue("1")
    com.goldensoftware.surfer.SrfMatSmoothMethod method, @DefaultValue("2")
    double centerWeight, @DefaultValue("2")
    double power, @DefaultValue("2")
    int nRow, @DefaultValue("2")
    int nCol, @DefaultValue("")
    java.lang.String outGrid, @DefaultValue("3")
    com.goldensoftware.surfer.SrfGridFormat outFmt);

    /**
     * Smooth an existing grid using cubic splines
     */
    @VTID(40)
    boolean gridSplineSmooth(java.lang.String inGrid, int nRow, int nCol, @DefaultValue("1")
    com.goldensoftware.surfer.SrfSplineMethod method, @DefaultValue("")
    java.lang.String outGrid, @DefaultValue("3")
    com.goldensoftware.surfer.SrfGridFormat outFmt);

    /**
     * Blank grid nodes inside or outside specified boundaries
     */
    @VTID(41)
    boolean gridBlank(java.lang.String inGrid, java.lang.String blankFile, @DefaultValue("")
    java.lang.String outGrid, @DefaultValue("3")
    com.goldensoftware.surfer.SrfGridFormat outFmt);

    /**
     * Convert between various grid formats
     */
    @VTID(42)
    boolean gridConvert(java.lang.String inGrid, @DefaultValue("")
    java.lang.String outGrid, @DefaultValue("3")
    com.goldensoftware.surfer.SrfGridFormat outFmt);

    /**
     * Compute cross section data through a grid surface
     */
    @VTID(44)
    boolean gridSlice(java.lang.String inGrid, java.lang.String blankFile, @DefaultValue("")
    java.lang.String outBlankFile, @DefaultValue("")
    java.lang.String outDataFile, @MarshalAs(NativeType.VARIANT)
    java.lang.Object outsideVal, @MarshalAs(NativeType.VARIANT)
    java.lang.Object blankVal);

    /**
     * Compute the difference between XYZ data and a grid surface
     */
    @VTID(45)
    com.goldensoftware.surfer.IWksDocument gridResiduals(java.lang.String inGrid, java.lang.String dataFile,
            @DefaultValue("0")
            int xCol, @DefaultValue("0")
            int yCol, @DefaultValue("0")
            int zCol, @DefaultValue("0")
            int residCol);

    /**
     * Scale, Offset, Mirror, or Rotate an existing grid file
     */
    @VTID(46)
    boolean gridTransform(java.lang.String inGrid, com.goldensoftware.surfer.SrfGridTransOp operation,
            @DefaultValue("0")
            double xOffset, @DefaultValue("0")
            double yOffset, @DefaultValue("1")
            double xScale, @DefaultValue("1")
            double yScale, @DefaultValue("0")
            double rotation, @DefaultValue("")
            java.lang.String outGrid, @DefaultValue("3")
            com.goldensoftware.surfer.SrfGridFormat outFmt);

    /**
     * Extract a subset from an existing grid file
     */
    @VTID(47)
    boolean gridExtract(java.lang.String inGrid, @DefaultValue("-1")
    int r1, @DefaultValue("-1")
    int r2, @DefaultValue("-1")
    int rFreq, @DefaultValue("-1")
    int c1, @DefaultValue("-1")
    int c2, @DefaultValue("-1")
    int cFreq, @DefaultValue("")
    java.lang.String outGrid, @DefaultValue("3")
    com.goldensoftware.surfer.SrfGridFormat outFmt);

    /**
     * Performs various calculus operations on an existing grid
     */
    @VTID(48)
    boolean gridCalculus(java.lang.String inGrid, com.goldensoftware.surfer.SrfCalculusOp operation, @DefaultValue("0")
    double param1, @DefaultValue("")
    java.lang.String outGrid, @DefaultValue("3")
    com.goldensoftware.surfer.SrfGridFormat outFmt);

    /**
     * Creates a new grid object
     */
    @VTID(49)
    com.goldensoftware.surfer.IGrid newGrid();

    /**
     * Returns/sets the global page unit type
     */
    @VTID(50)
    com.goldensoftware.surfer.SrfPageUnits pageUnits();

    /**
     * Returns/sets the global page unit type
     */
    @VTID(51)
    void pageUnits(com.goldensoftware.surfer.SrfPageUnits pUnits);

    /**
     * Returns/sets the global file backup state
     */
    @VTID(52)
    boolean backupFiles();

    /**
     * Returns/sets the global file backup state
     */
    @VTID(53)
    void backupFiles(boolean pBackup);

    /**
     * Returns/sets the visibility state of all the toolbars
     */
    @VTID(54)
    int showToolbars();

    /**
     * Returns/sets the visibility state of all the toolbars
     */
    @VTID(55)
    void showToolbars(int pVis);

    /**
     * Returns/sets the redraw flag for all view windows
     */
    @VTID(56)
    boolean screenUpdating();

    /**
     * Returns/sets the redraw flag for all view windows
     */
    @VTID(57)
    void screenUpdating(boolean pUpdate);

    /**
     * Returns/sets the state of the main application window
     */
    @VTID(58)
    void windowState(com.goldensoftware.surfer.SrfWindowState pState);

    /**
     * Returns/sets the state of the main application window
     */
    @VTID(59)
    com.goldensoftware.surfer.SrfWindowState windowState();

    /**
     * Apply a filter to an existing grid
     */
    @VTID(60)
    boolean gridFilter(java.lang.String inGrid, com.goldensoftware.surfer.SrfFilter filter, @DefaultValue("3")
    com.goldensoftware.surfer.SrfFilterEdge edgeOp, @DefaultValue("2")
    com.goldensoftware.surfer.SrfFilterBlank blankOp, @DefaultValue("1")
    int numPasses, @DefaultValue("0")
    double edgeFill, @DefaultValue("0")
    double blankFill, @DefaultValue("0")
    int numRow, @DefaultValue("0")
    int numCol, @MarshalAs(NativeType.VARIANT)
    java.lang.Object param1, @MarshalAs(NativeType.VARIANT)
    java.lang.Object param2, @MarshalAs(NativeType.VARIANT)
    java.lang.Object userFilter, @DefaultValue("")
    java.lang.String outGrid, @DefaultValue("3")
    com.goldensoftware.surfer.SrfGridFormat outFmt);

    /**
     * Creates a new grid by mosaicing one or more input grids
     */
    @VTID(61)
    boolean gridMosaic(java.lang.String[] inGrids, @DefaultValue("2")
    com.goldensoftware.surfer.SrfResampleMethod resampleMethod, @DefaultValue("1")
    com.goldensoftware.surfer.SrfOverlapMethod overlapMethod, @DefaultValue("0")
    double xMin, @DefaultValue("0")
    double xMax, @DefaultValue("0")
    double xSpacing, @DefaultValue("0")
    double xNumNodes, @DefaultValue("0")
    double yMin, @DefaultValue("0")
    double yMax, @DefaultValue("0")
    double ySpacing, @DefaultValue("0")
    double yNumNodes, @DefaultValue("")
    java.lang.String outGrid, @DefaultValue("3")
    com.goldensoftware.surfer.SrfGridFormat outFmt);

    /**
     * Performs cross validation
     */
    @VTID(62)
    boolean crossValidate(java.lang.String dataFile, @DefaultValue("0")
    int xCol, @DefaultValue("0")
    int yCol, @DefaultValue("0")
    int zCol, @MarshalAs(NativeType.VARIANT)
    java.lang.Object exclusionFilter, @MarshalAs(NativeType.VARIANT)
    java.lang.Object dupMethod, @MarshalAs(NativeType.VARIANT)
    java.lang.Object xDupTol, @MarshalAs(NativeType.VARIANT)
    java.lang.Object yDupTol, @MarshalAs(NativeType.VARIANT)
    java.lang.Object numCols, @MarshalAs(NativeType.VARIANT)
    java.lang.Object numRows, @MarshalAs(NativeType.VARIANT)
    java.lang.Object xMin, @MarshalAs(NativeType.VARIANT)
    java.lang.Object xMax, @MarshalAs(NativeType.VARIANT)
    java.lang.Object yMin, @MarshalAs(NativeType.VARIANT)
    java.lang.Object yMax, @MarshalAs(NativeType.VARIANT)
    java.lang.Object algorithm, @MarshalAs(NativeType.VARIANT)
    java.lang.Object showReport, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchEnable, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchNumSectors, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchRad1, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchRad2, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchAngle, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchMinData, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchDataPerSect, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchMaxEmpty, @MarshalAs(NativeType.VARIANT)
    java.lang.Object faultFileName, @MarshalAs(NativeType.VARIANT)
    java.lang.Object breakFileName, @MarshalAs(NativeType.VARIANT)
    java.lang.Object anisotropyRatio, @MarshalAs(NativeType.VARIANT)
    java.lang.Object anisotropyAngle, @MarshalAs(NativeType.VARIANT)
    java.lang.Object idPower, @MarshalAs(NativeType.VARIANT)
    java.lang.Object idSmoothing, @MarshalAs(NativeType.VARIANT)
    java.lang.Object krigType, @MarshalAs(NativeType.VARIANT)
    java.lang.Object krigDriftType, @MarshalAs(NativeType.VARIANT)
    java.lang.Object krigVariogram, @MarshalAs(NativeType.VARIANT)
    java.lang.Object mcMaxResidual, @MarshalAs(NativeType.VARIANT)
    java.lang.Object mcMaxIterations, @MarshalAs(NativeType.VARIANT)
    java.lang.Object mcInternalTension, @MarshalAs(NativeType.VARIANT)
    java.lang.Object mcBoundaryTension, @MarshalAs(NativeType.VARIANT)
    java.lang.Object mcRelaxationFactor, @MarshalAs(NativeType.VARIANT)
    java.lang.Object shepSmoothFactor, @MarshalAs(NativeType.VARIANT)
    java.lang.Object shepQuadraticNeighbors, @MarshalAs(NativeType.VARIANT)
    java.lang.Object shepWeightingNeighbors, @MarshalAs(NativeType.VARIANT)
    java.lang.Object shepRange1, @MarshalAs(NativeType.VARIANT)
    java.lang.Object shepRange2, @MarshalAs(NativeType.VARIANT)
    java.lang.Object regrMaxXOrder, @MarshalAs(NativeType.VARIANT)
    java.lang.Object regrMaxYOrder, @MarshalAs(NativeType.VARIANT)
    java.lang.Object regrMaxTotalOrder, @MarshalAs(NativeType.VARIANT)
    java.lang.Object rbBasisType, @MarshalAs(NativeType.VARIANT)
    java.lang.Object rbrSquared, @MarshalAs(NativeType.VARIANT)
    java.lang.Object searchMaxData, @MarshalAs(NativeType.VARIANT)
    java.lang.Object localPolyOrder, @MarshalAs(NativeType.VARIANT)
    java.lang.Object localPolyPower, @MarshalAs(NativeType.VARIANT)
    java.lang.Object numRandomPoints, @MarshalAs(NativeType.VARIANT)
    java.lang.Object xMinValidate, @MarshalAs(NativeType.VARIANT)
    java.lang.Object xMaxValidate, @MarshalAs(NativeType.VARIANT)
    java.lang.Object yMinValidate, @MarshalAs(NativeType.VARIANT)
    java.lang.Object yMaxValidate, @MarshalAs(NativeType.VARIANT)
    java.lang.Object zMinValidate, @MarshalAs(NativeType.VARIANT)
    java.lang.Object zMaxValidate, @MarshalAs(NativeType.VARIANT)
    java.lang.Object xTolValidate, @MarshalAs(NativeType.VARIANT)
    java.lang.Object yTolValidate, @MarshalAs(NativeType.VARIANT)
    java.lang.Object resultsFile, java.lang.Object pResults);

}
