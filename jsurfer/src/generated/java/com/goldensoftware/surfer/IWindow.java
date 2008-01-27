package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.DefaultMethod;
import com4j.DefaultValue;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IWindow Interface
 */
@IID("{B2933407-9788-11D2-9780-00104B6D9C80}")
public interface IWindow extends Com4jObject {
    /**
     * Returns the Application object
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
     * Returns the caption of the window
     */
    @VTID(9)
    @DefaultMethod
    java.lang.String caption();

    /**
     * Returns the type of this window
     */
    @VTID(10)
    com.goldensoftware.surfer.SrfWinTypes type();

    /**
     * Returns/sets the coordinate for the left edge of the window
     */
    @VTID(11)
    int left();

    /**
     * Returns/sets the coordinate for the left edge of the window
     */
    @VTID(12)
    void left(int pVal);

    /**
     * Returns/sets the coordinate for the top edge of the window
     */
    @VTID(13)
    int top();

    /**
     * Returns/sets the coordinate for the top edge of the window
     */
    @VTID(14)
    void top(int pVal);

    /**
     * Returns/sets the width of the window
     */
    @VTID(15)
    int width();

    /**
     * Returns/sets the width of the window
     */
    @VTID(16)
    void width(int pVal);

    /**
     * Returns/sets the height of the window
     */
    @VTID(17)
    int height();

    /**
     * Returns/sets the height of the window
     */
    @VTID(18)
    void height(int pVal);

    /**
     * Returns/sets the active state of the window
     */
    @VTID(19)
    boolean active();

    /**
     * Returns/sets the state of the window
     */
    @VTID(20)
    void windowState(com.goldensoftware.surfer.SrfWindowState pState);

    /**
     * Returns/sets the state of the window
     */
    @VTID(21)
    com.goldensoftware.surfer.SrfWindowState windowState();

    /**
     * Returns the document associated with this window
     */
    @VTID(22)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject document();

    /**
     * Returns the 1-based index of this window in the Application windows
     * collection
     */
    @VTID(23)
    int index();

    /**
     * Activates the window
     */
    @VTID(24)
    void activate();

    /**
     * Closes this window
     */
    @VTID(25)
    boolean close(@DefaultValue("1")
    com.goldensoftware.surfer.SrfSaveTypes saveChanges, @DefaultValue("")
    java.lang.String fileName);

}
