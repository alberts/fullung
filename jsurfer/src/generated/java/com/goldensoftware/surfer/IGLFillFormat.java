package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IGLFillFormat Interface
 */
@IID("{B2933437-9788-11D2-9780-00104B6D9C80}")
public interface IGLFillFormat extends Com4jObject {
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
     * Returns/sets the foreground color as an RGB value
     */
    @VTID(9)
    com.goldensoftware.surfer.srfColor color();

    /**
     * Returns/sets the foreground color as an RGB value
     */
    @VTID(10)
    void color(com.goldensoftware.surfer.srfColor pColor);

    /**
     * Returns/sets the fill style
     */
    @VTID(11)
    com.goldensoftware.surfer.SrfGLFillStyle style();

    /**
     * Returns/sets the fill style
     */
    @VTID(12)
    void style(com.goldensoftware.surfer.SrfGLFillStyle pStyle);

}
