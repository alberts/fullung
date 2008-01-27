package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * IText Interface
 */
@IID("{B2933419-9788-11D2-9780-00104B6D9C80}")
public interface IText extends com.goldensoftware.surfer.IShape {
    /**
     * Returns/sets the text string
     */
    @VTID(30)
    java.lang.String text();

    /**
     * Returns/sets the text string
     */
    @VTID(31)
    void text(java.lang.String pText);

    /**
     * Returns the font format object
     */
    @VTID(32)
    com.goldensoftware.surfer.IFontFormat font();

}
