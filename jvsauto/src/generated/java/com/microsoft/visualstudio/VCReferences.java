package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCReferences
 */
@IID("{238B5177-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCReferences extends com.microsoft.visualstudio.VCProjectItem,Iterable<Com4jObject> {
    @VTID(13)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addAssemblyReference(
        java.lang.String bstrRef);

    @VTID(14)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addActiveXReference(
        java.lang.String typeLibGuid,
        int majorVersion,
        int minorVersion,
        int localeID,
        java.lang.String wrapper);

    @VTID(15)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addProjectReference(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject proj);

    @VTID(16)
    boolean canAddAssemblyReference(
        java.lang.String bstrRef);

    @VTID(17)
    boolean canAddActiveXReference(
        java.lang.String typeLibGuid,
        int majorVersion,
        int minorVersion,
        int localeID,
        java.lang.String wrapper);

    @VTID(18)
    boolean canAddProjectReference(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject proj);

    @VTID(19)
    void removeReference(
        @MarshalAs(NativeType.Dispatch) com4j.Com4jObject reference);

    @VTID(20)
    java.util.Iterator<Com4jObject> iterator();

    @VTID(21)
    @DefaultMethod
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject item(
        @MarshalAs(NativeType.VARIANT) java.lang.Object index);

    @VTID(22)
    int count();

    @VTID(23)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject references();

    @VTID(24)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addAssemblyReferenceWithStrongName(
        java.lang.String bstrRef,
        java.lang.String bstrAssemblyName);

    @VTID(25)
    boolean canAddAssemblyReferenceWithStrongName(
        java.lang.String bstrRef,
        java.lang.String bstrAssemblyName);

    @VTID(26)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addProjectReferenceByIdentifier(
        java.lang.String identifier);

    @VTID(27)
    boolean canAddProjectReferenceByIdentifier(
        java.lang.String identifier);

    @VTID(28)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject addReferenceToFile(
        java.lang.String path);

    @VTID(29)
    boolean canAddReferenceToFile(
        java.lang.String path);

}
