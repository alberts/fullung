package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IFontFormat Interface
 */
@IID("{B2933412-9788-11D2-9780-00104B6D9C80}")
public interface IFontFormat extends Com4jObject {
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
     * Returns/sets the color as an RGB value
     */
    @VTID(9)
    com.goldensoftware.surfer.srfColor color();

    /**
     * Returns/sets the color as an RGB value
     */
    @VTID(10)
    void color(com.goldensoftware.surfer.srfColor pColor);

    /**
     * Returns/sets the font face name
     */
    @VTID(11)
    java.lang.String face();

    /**
     * Returns/sets the font face name
     */
    @VTID(12)
    void face(java.lang.String pFace);

    /**
     * Returns/sets the font height in points
     */
    @VTID(13)
    double size();

    /**
     * Returns/sets the font height in points
     */
    @VTID(14)
    void size(double pSize);

    /**
     * Returns/sets the bold style
     */
    @VTID(15)
    boolean bold();

    /**
     * Returns/sets the bold style
     */
    @VTID(16)
    void bold(boolean pBold);

    /**
     * Returns/sets the italic style
     */
    @VTID(17)
    boolean italic();

    /**
     * Returns/sets the italic style
     */
    @VTID(18)
    void italic(boolean pItalic);

    /**
     * Returns/sets the strike-through style
     */
    @VTID(19)
    boolean strikeThrough();

    /**
     * Returns/sets the strike-through style
     */
    @VTID(20)
    void strikeThrough(boolean pStrike);

    /**
     * Returns/sets the underline style
     */
    @VTID(21)
    boolean underline();

    /**
     * Returns/sets the underline style
     */
    @VTID(22)
    void underline(boolean pUnderline);

    /**
     * Returns/sets the horizontal alignment
     */
    @VTID(23)
    com.goldensoftware.surfer.SrfHTextAlign hAlign();

    /**
     * Returns/sets the horizontal alignment
     */
    @VTID(24)
    void hAlign(com.goldensoftware.surfer.SrfHTextAlign pAlign);

    /**
     * Returns/sets the vertical alignment
     */
    @VTID(25)
    com.goldensoftware.surfer.SrfVTextAlign vAlign();

    /**
     * Returns/sets the vertical alignment
     */
    @VTID(26)
    void vAlign(com.goldensoftware.surfer.SrfVTextAlign pAlign);

}
