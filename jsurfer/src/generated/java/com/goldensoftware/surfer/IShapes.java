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
 * IShapes Interface
 */
@IID("{B293340C-9788-11D2-9780-00104B6D9C80}")
public interface IShapes extends Com4jObject, Iterable<Com4jObject> {
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
     * Returns the number of Shapes
     */
    @VTID(9)
    int count();

    @VTID(10)
    java.util.Iterator<Com4jObject> iterator();

    /**
     * Returns an individual shape
     */
    @VTID(11)
    @DefaultMethod
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject item(@MarshalAs(NativeType.VARIANT)
    java.lang.Object index);

    /**
     * Adds a new rectangle or rounded rectangle shape
     */
    @VTID(12)
    com.goldensoftware.surfer.IRectangle addRectangle(double left, double top, double right, double bottom,
            @DefaultValue("0")
            double xRadius, @DefaultValue("0")
            double yRadius);

    /**
     * Adds a new ellipse shape
     */
    @VTID(13)
    com.goldensoftware.surfer.IEllipse addEllipse(double left, double top, double right, double bottom);

    /**
     * Adds a new symbol shape
     */
    @VTID(14)
    com.goldensoftware.surfer.ISymbol addSymbol(double x, double y);

    /**
     * Adds a new text shape
     */
    @VTID(15)
    com.goldensoftware.surfer.IText addText(double x, double y, java.lang.String text);

    /**
     * Adds a new line segment
     */
    @VTID(16)
    com.goldensoftware.surfer.IPolyline addLine(double xBeg, double yBeg, double xEnd, double yEnd);

    /**
     * Adds a new polyline shape
     */
    @VTID(17)
    com.goldensoftware.surfer.IPolyline addPolyLine(double[] vertices);

    /**
     * Adds a new simple polygon shape
     */
    @VTID(18)
    com.goldensoftware.surfer.IPolygon addPolygon(double[] vertices);

    /**
     * Adds a new complex (included/excluded regions) polygon shape
     */
    @VTID(19)
    com.goldensoftware.surfer.IPolygon addComplexPolygon(double[] vertices, int[] polyCounts);

    /**
     * Adds a new base map
     */
    @VTID(20)
    com.goldensoftware.surfer.IMapFrame addBaseMap(java.lang.String importFileName, @DefaultValue("")
    java.lang.String importOptions);

    /**
     * Adds a new contour map
     */
    @VTID(21)
    com.goldensoftware.surfer.IMapFrame addContourMap(java.lang.String gridFileName);

    /**
     * Adds a new post map
     */
    @VTID(22)
    com.goldensoftware.surfer.IMapFrame addPostMap(java.lang.String dataFileName, @DefaultValue("0")
    int xCol, @DefaultValue("0")
    int yCol, @DefaultValue("0")
    int labCol, @DefaultValue("0")
    int symCol, @DefaultValue("0")
    int angleCol);

    /**
     * Adds a new classed post map
     */
    @VTID(23)
    com.goldensoftware.surfer.IMapFrame addClassedPostMap(java.lang.String dataFileName, @DefaultValue("0")
    int xCol, @DefaultValue("0")
    int yCol, @DefaultValue("0")
    int zCol, @DefaultValue("0")
    int labCol);

    /**
     * Adds a new image map
     */
    @VTID(24)
    com.goldensoftware.surfer.IMapFrame addImageMap(java.lang.String gridFileName);

    /**
     * Adds a new shaded relief map
     */
    @VTID(25)
    com.goldensoftware.surfer.IMapFrame addReliefMap(java.lang.String gridFileName);

    /**
     * Adds a new vector map
     */
    @VTID(26)
    com.goldensoftware.surfer.IMapFrame addVectorMap(java.lang.String gridFileName1, @DefaultValue("")
    java.lang.String gridFileName2, @DefaultValue("1")
    com.goldensoftware.surfer.SrfVecCoordSys coordSys, @DefaultValue("1")
    com.goldensoftware.surfer.SrfVecAngleSys angleSys, @DefaultValue("1")
    com.goldensoftware.surfer.SrfVecAngleUnits angleUnits);

    /**
     * Adds a new wireframe plot
     */
    @VTID(27)
    com.goldensoftware.surfer.IMapFrame addWireframe(java.lang.String gridFileName);

    /**
     * Adds a new variogram plot
     */
    @VTID(28)
    com.goldensoftware.surfer.IVariogram addVariogram(java.lang.String dataFileName, @DefaultValue("0")
    int xCol, @DefaultValue("0")
    int yCol, @DefaultValue("0")
    int zCol, @DefaultValue("")
    java.lang.String exclusionFilter, @DefaultValue("1")
    com.goldensoftware.surfer.SrfDupMethod dupMethod, @DefaultValue("0")
    double xDupTol, @DefaultValue("0")
    double yDupTol, @DefaultValue("180")
    int numAngularDivisions, @DefaultValue("100")
    int numRadialDivisions, @DefaultValue("1")
    com.goldensoftware.surfer.SrfDetrendMethod detrendMethod, @DefaultValue("0")
    boolean showReport, @MarshalAs(NativeType.VARIANT)
    java.lang.Object maxLagDistance);

    /**
     * Selects all shapes in the collection
     */
    @VTID(29)
    void selectAll();

    /**
     * Selects all shapes within the specified rectangle
     */
    @VTID(30)
    void blockSelect(double left, double top, double right, double bottom);

    /**
     * Selects all deselected objects, deselects all selected objects
     */
    @VTID(31)
    void invertSelection();

    /**
     * Pastes the contents of the clipboard into the collection
     */
    @VTID(32)
    com.goldensoftware.surfer.ISelection paste(@DefaultValue("1")
    com.goldensoftware.surfer.SrfPasteFormat format);

    /**
     * Adds a new Surface plot
     */
    @VTID(33)
    com.goldensoftware.surfer.IMapFrame addSurface(java.lang.String gridFileName);

}
