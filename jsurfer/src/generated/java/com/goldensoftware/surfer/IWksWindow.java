package com.goldensoftware.surfer;

import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IWksWindow Interface
 */
@IID("{B2933409-9788-11D2-9780-00104B6D9C80}")
public interface IWksWindow extends com.goldensoftware.surfer.IWindow {
    /**
     * Returns/sets the window's selected Range. Returns null if nothing is
     * selected.
     */
    @VTID(26)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object selection();

    /**
     * Returns/sets the window's selected Range. Returns null if nothing is
     * selected.
     */
    @VTID(27)
    void selection(@MarshalAs(NativeType.VARIANT)
    java.lang.Object pVal);

    /**
     * Returns/sets the active cell location.
     */
    @VTID(28)
    com.goldensoftware.surfer.IWksRange activeCell();

    /**
     * Returns/sets the active cell location.
     */
    @VTID(29)
    void activeCell(@MarshalAs(NativeType.VARIANT)
    java.lang.Object ppRange);

    /**
     * Returns/sets the first visible column in the window.
     */
    @VTID(30)
    int leftColumn();

    /**
     * Returns/sets the first visible column in the window.
     */
    @VTID(31)
    void leftColumn(@MarshalAs(NativeType.VARIANT)
    java.lang.Object pVal);

    /**
     * Returns/sets the first visible row in the window.
     */
    @VTID(32)
    int topRow();

    /**
     * Returns/sets the first visible row in the window.
     */
    @VTID(33)
    void topRow(int pVal);

}
