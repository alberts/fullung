package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCFileConfiguration
 */
@IID("{238B5184-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCFileConfiguration extends Com4jObject {
    @VTID(7)
    java.lang.String name();

    @VTID(8)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject tool();

    @VTID(9)
    void tool(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject val);

    @VTID(10)
    boolean excludedFromBuild();

    @VTID(11)
    void excludedFromBuild(
        boolean excludedFromBuild);

    @VTID(12)
    boolean matchName(
        java.lang.String bstrNameToMatch,
        boolean fullOnly);

    @VTID(13)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject parent();

    @VTID(14)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject file();

    @VTID(15)
    java.lang.String evaluate(
        java.lang.String bstrIn);

    @VTID(16)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcProjectEngine();

    @VTID(17)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject projectConfiguration();

    @VTID(18)
    void compile(
        boolean forceBuild,
        boolean waitOnBuild);

    @VTID(19)
    boolean outputUpToDate();

}
