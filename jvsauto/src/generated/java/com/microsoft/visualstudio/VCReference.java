package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCReference
 */
@IID("{238B5178-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCReference extends com.microsoft.visualstudio.VCProjectItem {
    @VTID(13)
    java.lang.String name();

    @VTID(14)
    java.lang.String identity();

    @VTID(15)
    java.lang.String description();

    @VTID(16)
    java.lang.String label();

    @VTID(17)
    boolean copyLocal();

    @VTID(18)
    void copyLocal(
        boolean copyLocal);

    @VTID(19)
    java.lang.String fullPath();

    @VTID(20)
    java.lang.String culture();

    @VTID(21)
    int majorVersion();

    @VTID(22)
    int minorVersion();

    @VTID(23)
    java.lang.String version();

    @VTID(24)
    boolean strongName();

    @VTID(25)
    java.lang.String publicKeyToken();

    @VTID(26)
    int buildNumber();

    @VTID(27)
    int revisionNumber();

    @VTID(28)
    void remove();

    @VTID(29)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject reference();

    @VTID(30)
    boolean useInBuild();

    @VTID(31)
    void useInBuild(
        boolean useInBuild);

    @VTID(32)
    @ReturnValue(type=NativeType.Dispatch)
    com4j.Com4jObject vcReferences();

    @VTID(33)
    java.lang.String assemblyName();

    @VTID(34)
    java.lang.String subType();

    @VTID(35)
    void subType(
        java.lang.String val);

    @VTID(36)
    boolean useDependenciesInBuild();

    @VTID(37)
    void useDependenciesInBuild(
        boolean useDependenciesInBuild);

    @VTID(38)
    boolean copyLocalDependencies();

    @VTID(39)
    void copyLocalDependencies(
        boolean copyLocalDependencies);

    @VTID(40)
    boolean copyLocalSatelliteAssemblies();

    @VTID(41)
    void copyLocalSatelliteAssemblies(
        boolean copyLocalSatellites);

}
