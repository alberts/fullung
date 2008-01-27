package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IMarkerFormat Interface
 */
@IID("{B2933413-9788-11D2-9780-00104B6D9C80}")
public interface IMarkerFormat extends Com4jObject {
    /**
     * Returns the application object
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
     * Returns/sets the symbol set name
     */
    @VTID(9)
    java.lang.String set();

    /**
     * Returns/sets the symbol set name
     */
    @VTID(10)
    void set(java.lang.String pSet);

    /**
     * Returns/sets the glyph index
     */
    @VTID(11)
    int index();

    /**
     * Returns/sets the glyph index
     */
    @VTID(12)
    void index(int pIndex);

    /**
     * Returns/sets the symbol height in page units
     */
    @VTID(13)
    double size();

    /**
     * Returns/sets the symbol height in page units
     */
    @VTID(14)
    void size(double pSize);

    /**
     * Returns/sets the color as an RGB value
     */
    @VTID(15)
    com.goldensoftware.surfer.srfColor color();

    /**
     * Returns/sets the color as an RGB value
     */
    @VTID(16)
    void color(com.goldensoftware.surfer.srfColor pColor);

}
