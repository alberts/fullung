package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCToolFile
 */
@IID("{238B5187-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCToolFile extends Com4jObject {
    @VTID(7)
    java.lang.String name();

    @VTID(8)
    void name(
        java.lang.String name);

    @VTID(9)
    java.lang.String path();

    @VTID(10)
    com.microsoft.visualstudio.IVCCollection customBuildRules();

    @VTID(10)
    @ReturnValue(type=NativeType.Dispatch,defaultPropertyThrough={com.microsoft.visualstudio.IVCCollection.class})
    com4j.Com4jObject customBuildRules(
        @MarshalAs(NativeType.VARIANT) java.lang.Object index);

    @VTID(11)
    void save(
        java.lang.String path);

    @VTID(12)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addCustomBuildRule(
        java.lang.String name,
        java.lang.String commandLine,
        java.lang.String outputs,
        java.lang.String fileExtensions);

    @VTID(13)
    void removeCustomBuildRule(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject customBuildRule);

}
