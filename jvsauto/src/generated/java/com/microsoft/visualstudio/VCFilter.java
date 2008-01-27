package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCFilter
 */
@IID("{238B5176-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCFilter extends com.microsoft.visualstudio.VCProjectItem {
    @VTID(13)
    java.lang.String name();

    @VTID(14)
    void name(
        java.lang.String val);

    @VTID(15)
    java.lang.String uniqueIdentifier();

    @VTID(16)
    void uniqueIdentifier(
        java.lang.String val);

    @VTID(17)
    java.lang.String canonicalName();

    @VTID(18)
    void remove();

    @VTID(19)
    java.lang.String filter();

    @VTID(20)
    void filter(
        java.lang.String val);

    @VTID(21)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject filters();

    @VTID(22)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject files();

    @VTID(23)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addFilter(
        java.lang.String bstrName);

    @VTID(24)
    boolean canAddFilter(
        java.lang.String filter);

    @VTID(25)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addFile(
        java.lang.String bstrPath);

    @VTID(26)
    boolean canAddFile(
        java.lang.String bstrFile);

    @VTID(27)
    void removeFile(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject file);

    @VTID(28)
    void removeFilter(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject filter);

    @VTID(29)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject items();

    @VTID(30)
    boolean parseFiles();

    @VTID(31)
    void parseFiles(
        boolean parse);

    @VTID(32)
    boolean sourceControlFiles();

    @VTID(33)
    void sourceControlFiles(
        boolean scc);

    @VTID(34)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addWebReference(
        java.lang.String bstrUrl,
        java.lang.String bstrName);

    @VTID(35)
    void move(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject parent);

    @VTID(36)
    boolean canMove(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject parent);

    @VTID(37)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject object();

    @VTID(38)
    java.lang.String webReference();

    @VTID(39)
    com.microsoft.visualstudio.eWebRefUrlBehavior urlBehavior();

}
