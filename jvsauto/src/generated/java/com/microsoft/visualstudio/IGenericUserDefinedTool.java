package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * IGenericUserDefinedTool Interface
 */
@IID("{4F0F5FBF-A5C3-87E4-B2AC-1D4782F0E835}")
public interface IGenericUserDefinedTool extends Com4jObject {
    @VTID(3)
    java.lang.String name();

    @VTID(4)
    java.lang.String identifier();

    @VTID(5)
    int numberOfProperties();

    @VTID(6)
    int numberOfPropertiesObjects();

    @VTID(7)
    void setPropertyContainer(
        com.microsoft.visualstudio.ISimplePropertyContainer pPropContainer);

    @VTID(8)
    com4j.Com4jObject getPropertiesObject(
        int index);

    @VTID(9)
    java.lang.String getPropertiesObjectName(
        int index);

    @VTID(10)
    void saveProperties(
        com.microsoft.visualstudio.IToolPropertyWriter pXML);

    @VTID(11)
    void saveUserProperties(
        com.microsoft.visualstudio.IToolPropertyWriter pXML);

}
