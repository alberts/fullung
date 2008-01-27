package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCFile
 */
@IID("{238B5175-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCFile extends com.microsoft.visualstudio.VCProjectItem {
    @VTID(13)
    java.lang.String name();

    @VTID(14)
    void remove();

    @VTID(15)
    java.lang.String relativePath();

    @VTID(16)
    void relativePath(
        java.lang.String val);

    @VTID(17)
    java.lang.String fullPath();

    @VTID(18)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject fileConfigurations();

    @VTID(19)
    boolean deploymentContent();

    @VTID(20)
    void deploymentContent(
        boolean val);

    @VTID(21)
    java.lang.String extension();

    @VTID(22)
    void move(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject parent);

    @VTID(23)
    boolean canMove(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject parent);

    @VTID(24)
    com.microsoft.visualstudio.eFileType fileType();

    @VTID(25)
    void fileType(
        com.microsoft.visualstudio.eFileType pType);

    @VTID(26)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject items();

    @VTID(27)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addFile(
        java.lang.String bstrPath);

    @VTID(28)
    boolean canAddFile(
        java.lang.String bstrFile);

    @VTID(29)
    void removeFile(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject file);

    @VTID(30)
    java.lang.String customTool();

    @VTID(31)
    void customTool(
        java.lang.String val);

    @VTID(32)
    java.lang.String subType();

    @VTID(33)
    void subType(
        java.lang.String val);

    @VTID(34)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject object();

    @VTID(35)
    java.lang.String unexpandedRelativePath();

}
