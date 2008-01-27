package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * IPlotWindow Interface
 */
@IID("{B2933408-9788-11D2-9780-00104B6D9C80}")
public interface IPlotWindow extends com.goldensoftware.surfer.IWindow {
    /**
     * Enable/Disable Automatic Redraw
     */
    @VTID(26)
    boolean autoRedraw();

    /**
     * Enable/Disable Automatic Redraw
     */
    @VTID(27)
    void autoRedraw(boolean pEnabled);

    /**
     * Show/hide rulers
     */
    @VTID(28)
    boolean showRulers();

    /**
     * Show/hide rulers
     */
    @VTID(29)
    void showRulers(boolean pShow);

    /**
     * Show/hide the drawing grid
     */
    @VTID(30)
    boolean showGrid();

    /**
     * Show/hide the drawing grid
     */
    @VTID(31)
    void showGrid(boolean pShow);

    /**
     * Redraw the contents of this window
     */
    @VTID(32)
    void redraw();

    /**
     * Zoom in or out
     */
    @VTID(33)
    void zoom(com.goldensoftware.surfer.SrfZoomTypes type);

    /**
     * Zoom in or out about a specified point
     */
    @VTID(34)
    void zoomPoint(double x, double y, double scale);

    /**
     * Zoom such that the specified rectangle occupies the entire window
     */
    @VTID(35)
    void zoomRectangle(double left, double top, double right, double bottom);

    /**
     * Returns the horizontal ruler object
     */
    @VTID(36)
    com.goldensoftware.surfer.IRuler horizontalRuler();

    /**
     * Returns the vertical ruler object
     */
    @VTID(37)
    com.goldensoftware.surfer.IRuler verticalRuler();

    /**
     * Show/hide the page representation rectangle
     */
    @VTID(38)
    boolean showPage();

    /**
     * Show/hide the page representation rectangle
     */
    @VTID(39)
    void showPage(boolean pShow);

    /**
     * Show/hide the margin representation rectangle
     */
    @VTID(40)
    boolean showMargins();

    /**
     * Show/hide the margin representation rectangle
     */
    @VTID(41)
    void showMargins(boolean pShow);

}
