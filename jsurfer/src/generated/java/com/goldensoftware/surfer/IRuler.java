package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IRuler Interface
 */
@IID("{B2933435-9788-11D2-9780-00104B6D9C80}")
public interface IRuler extends Com4jObject {
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
     * Returns/sets the number of ruler divisions per page unit
     */
    @VTID(9)
    int rulerDivisions();

    /**
     * Returns/sets the number of ruler divisions per page unit
     */
    @VTID(10)
    void rulerDivisions(int pnDiv);

    /**
     * Returns/sets the number of grid divisions per page unit
     */
    @VTID(11)
    int gridDivisions();

    /**
     * Returns/sets the number of grid divisions per page unit
     */
    @VTID(12)
    void gridDivisions(int pnDiv);

    /**
     * Returns/sets the snap to ruler state
     */
    @VTID(13)
    boolean snapToRuler();

    /**
     * Returns/sets the snap to ruler state
     */
    @VTID(14)
    void snapToRuler(boolean pSnap);

    /**
     * Returns/sets the show position state
     */
    @VTID(15)
    boolean showPosition();

    /**
     * Returns/sets the show position state
     */
    @VTID(16)
    void showPosition(boolean pShow);

}
