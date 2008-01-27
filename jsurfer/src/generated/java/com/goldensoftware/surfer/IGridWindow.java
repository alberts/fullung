package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * IGridWindow Interface
 */
@IID("{B293340A-9788-11D2-9780-00104B6D9C80}")
public interface IGridWindow extends com.goldensoftware.surfer.IWindow {
    /**
     * Show/hide the contour lines
     */
    @VTID(26)
    boolean showContours();

    /**
     * Show/hide the contour lines
     */
    @VTID(27)
    void showContours(boolean pShow);

    /**
     * Show/hide the grid node symbols
     */
    @VTID(28)
    boolean showNodes();

    /**
     * Show/hide the grid node symbols
     */
    @VTID(29)
    void showNodes(boolean pShow);

    /**
     * Redraw the contents of this window
     */
    @VTID(30)
    void redraw();

    /**
     * Zoom in or out
     */
    @VTID(31)
    void zoom(com.goldensoftware.surfer.SrfGridZoomTypes type);

}
