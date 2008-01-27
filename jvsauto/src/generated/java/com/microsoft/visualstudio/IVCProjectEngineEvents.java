package com.microsoft.visualstudio  ;

import com4j.*;

@IID("{FBBF3C62-2428-11D7-8BF6-00B0D03DAA06}")
public interface IVCProjectEngineEvents extends Com4jObject {
    @VTID(7)
    void itemAdded(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject item,
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject itemParent);

    @VTID(8)
    void itemRemoved(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject item,
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject itemParent);

    @VTID(9)
    void itemRenamed(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject item,
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject itemParent,
        java.lang.String oldName);

    @VTID(10)
    void itemMoved(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject item,
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject newParent,
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject oldParent);

    @VTID(11)
    void itemPropertyChange(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject item,
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject tool,
        int dispid);

    @VTID(12)
    boolean sccEvent(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject item,
        com.microsoft.visualstudio.enumSccEvent event);

    @VTID(13)
    void reportError(
        java.lang.String errMsg,
        int hr,
        java.lang.String helpKeyword);

    @VTID(14)
    void projectBuildStarted(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject cfg);

    @VTID(15)
    void projectBuildFinished(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject cfg,
        int warnings,
        int errors,
        boolean cancelled);

    @VTID(16)
    void solutionLoaded();

}
