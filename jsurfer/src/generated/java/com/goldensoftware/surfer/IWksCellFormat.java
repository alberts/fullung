package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * Represents the formatting of one or more cells in a worksheet.
 */
@IID("{00222CC5-2B05-11D2-9F99-482637000000}")
public interface IWksCellFormat extends Com4jObject {
    /**
     * Returns the Application object.
     */
    @VTID(7)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject application();

    /**
     * Returns the parent Range of this CellFormat object.
     */
    @VTID(8)
    com.goldensoftware.surfer.IWksRange parent();

    /**
     * Returns/sets the horizontal alignment. Returns null if not all the cells
     * in a Range have the same horizontal alignment.
     */
    @VTID(9)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object alignment();

    /**
     * Returns/sets the horizontal alignment. Returns null if not all the cells
     * in a Range have the same horizontal alignment.
     */
    @VTID(10)
    void alignment(com.goldensoftware.surfer.wksAlign pVal);

    /**
     * Returns/sets the background color. Returns null if not all the cells in a
     * Range have the same background color.
     */
    @VTID(11)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object backColor();

    /**
     * Returns/sets the background color. Returns null if not all the cells in a
     * Range have the same background color.
     */
    @VTID(12)
    void backColor(com.goldensoftware.surfer.wksColor pVal);

    /**
     * Returns/sets the numeric format type. Returns null if not all the cells
     * in a Range have the same numeric format type.
     */
    @VTID(13)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object numericType();

    /**
     * Returns/sets the numeric format type. Returns null if not all the cells
     * in a Range have the same numeric format type.
     */
    @VTID(14)
    void numericType(com.goldensoftware.surfer.wksNumericType pVal);

    /**
     * Returns/sets the number of decimal digits. Returns null if not all the
     * cells in a Range have the same number of decimal digits.
     */
    @VTID(15)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object digits();

    /**
     * Returns/sets the number of decimal digits. Returns null if not all the
     * cells in a Range have the same number of decimal digits.
     */
    @VTID(16)
    void digits(short pVal);

    /**
     * Returns/sets whether numbers are formatted with thousands separator
     * characters. Returns null if not all the cells in a Range have the same
     * thousands separator formatting.
     */
    @VTID(17)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object thousands();

    /**
     * Returns/sets whether numbers are formatted with thousands separator
     * characters. Returns null if not all the cells in a Range have the same
     * thousands separator formatting.
     */
    @VTID(18)
    void thousands(boolean pVal);

    @VTID(19)
    void reserved0();

    @VTID(20)
    void reserved1();

    @VTID(21)
    void reserved2();

    @VTID(22)
    void reserved3();

    @VTID(23)
    void reserved4();

    @VTID(24)
    void reserved5();

    @VTID(25)
    void reserved6();

    @VTID(26)
    void reserved7();

    @VTID(27)
    void reserved8();

    @VTID(28)
    void reserved9();

}
