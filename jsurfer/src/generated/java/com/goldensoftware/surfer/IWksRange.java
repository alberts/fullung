package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.DefaultMethod;
import com4j.DefaultValue;
import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * Represents one or more cells within a worksheet
 */
@IID("{00222CC4-2B05-11D2-9F99-482637000000}")
public interface IWksRange extends Com4jObject, Iterable<Com4jObject> {
    @VTID(7)
    java.util.Iterator<Com4jObject> iterator();

    /**
     * Returns/sets the cell values in the Range.
     */
    @VTID(8)
    @DefaultMethod
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object value();

    /**
     * Returns/sets the cell values in the Range.
     */
    @VTID(9)
    @DefaultMethod
    void value(@MarshalAs(NativeType.VARIANT)
    java.lang.Object pVal);

    /**
     * Returns the Application object.
     */
    @VTID(10)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject application();

    /**
     * Returns the worksheet associated with this Range.
     */
    @VTID(11)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject parent();

    /**
     * Returns the text description of the Range
     */
    @VTID(12)
    java.lang.String name();

    /**
     * Retutrns the first row in the Range.
     */
    @VTID(13)
    int row();

    /**
     * Returns the first column in the Range.
     */
    @VTID(14)
    int column();

    /**
     * Returns the last row in the Range.
     */
    @VTID(15)
    int lastRow();

    /**
     * Returns the last column in the Range.
     */
    @VTID(16)
    int lastColumn();

    /**
     * Returns the number of cells in the Range.
     */
    @VTID(17)
    double count();

    /**
     * Returns the number of rows in the Range.
     */
    @VTID(18)
    int rowCount();

    /**
     * Returns the number of columns in the Range.
     */
    @VTID(19)
    int columnCount();

    /**
     * Returns True if the Range contains whole rows.
     */
    @VTID(20)
    boolean isEntireRow();

    /**
     * Returns True if the Range contains entire columns.
     */
    @VTID(21)
    boolean isEntireColumn();

    /**
     * Returns a new Range containing every cell in each row of the current
     * Range.
     */
    @VTID(22)
    com.goldensoftware.surfer.IWksRange entireRow();

    /**
     * Returns a new Range containing every cell in each column of the current
     * Range.
     */
    @VTID(23)
    com.goldensoftware.surfer.IWksRange entireColumn();

    /**
     * Returns/sets the column width of all the columns in the Range. Returns
     * null if not all the column widths are the same.
     */
    @VTID(24)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object columnWidth();

    /**
     * Returns/sets the column width of all the columns in the Range. Returns
     * null if not all the column widths are the same.
     */
    @VTID(25)
    void columnWidth(double pVal);

    /**
     * Returns/sets the row height of all the rows in the Range. Returns null if
     * not all the row heights are the same.
     */
    @VTID(26)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object rowHeight();

    /**
     * Returns/sets the row height of all the rows in the Range. Returns null if
     * not all the row heights are the same.
     */
    @VTID(27)
    void rowHeight(double pVal);

    /**
     * Returns the CellFormat object used by the cells in the Range.
     */
    @VTID(28)
    com.goldensoftware.surfer.IWksCellFormat format();

    /**
     * Deletes the contents and formatting of all the cells in the Range.
     */
    @VTID(29)
    void clear();

    /**
     * Copies all the cells in the Range into the clipboard.
     */
    @VTID(30)
    void copy();

    /**
     * Copies all the cells in the Range into the clipboard and clears the
     * contents and formatting of the cells.
     */
    @VTID(31)
    void cut();

    /**
     * Inserts the contents of the clipboard into the Range area. Will not paste
     * into cells that lie outside the Range.
     */
    @VTID(32)
    boolean paste(@DefaultValue("0")
    boolean clipToRange);

    /**
     * Inserts clipboard contents using the specified format into the Range
     * area. Will not paste into cells that lie outside the Range.
     */
    @VTID(33)
    boolean pasteSpecial(com.goldensoftware.surfer.wksClipboard format, @DefaultValue("0")
    boolean clipToRange);

    /**
     * Shifts the current cells down or to the right and inserts blank cells in
     * their place.
     */
    @VTID(34)
    void insert(@DefaultValue("0")
    com.goldensoftware.surfer.wksInsert direction);

    /**
     * Deletes the cells in the Range and shifts the remaining cells either up
     * or to the left.
     */
    @VTID(35)
    void delete(@DefaultValue("0")
    com.goldensoftware.surfer.wksDelete direction);

    /**
     * Sorts the cells in the Range from top to bottom.
     */
    @VTID(36)
    void sort(@DefaultValue("-1")
    int col1, @DefaultValue("0")
    com.goldensoftware.surfer.wksSort order1, @DefaultValue("-1")
    int col2, @DefaultValue("0")
    com.goldensoftware.surfer.wksSort order2, @DefaultValue("-1")
    int col3, @DefaultValue("0")
    com.goldensoftware.surfer.wksSort order3, @DefaultValue("0")
    boolean header, @DefaultValue("0")
    boolean matchCase);

    /**
     * Calculates statistics on all the columns in the Range.
     */
    @VTID(37)
    com.goldensoftware.surfer.IWksStatistics statistics(@DefaultValue("0")
    boolean sample, @DefaultValue("0")
    boolean header, @DefaultValue("0")
    int flags);

    /**
     * Returns a new Range. The specified coordinates are relative to the
     * top-left cell in the current Range.
     */
    @VTID(38)
    com.goldensoftware.surfer.IWksRange cells(@MarshalAs(NativeType.VARIANT)
    java.lang.Object row, @MarshalAs(NativeType.VARIANT)
    java.lang.Object col, @MarshalAs(NativeType.VARIANT)
    java.lang.Object lastRow, @MarshalAs(NativeType.VARIANT)
    java.lang.Object lastCol);

    /**
     * Returns a Range containing all or some of the rows in the current Range
     */
    @VTID(39)
    com.goldensoftware.surfer.IWksRange rows(@MarshalAs(NativeType.VARIANT)
    java.lang.Object row1, @MarshalAs(NativeType.VARIANT)
    java.lang.Object row2);

    /**
     * Returns a Range containing all or some of the columns of the current
     * Range.
     */
    @VTID(40)
    com.goldensoftware.surfer.IWksRange columns(@MarshalAs(NativeType.VARIANT)
    java.lang.Object col1, @MarshalAs(NativeType.VARIANT)
    java.lang.Object col2);

    @VTID(41)
    void reserved0();

    @VTID(42)
    void reserved1();

    @VTID(43)
    void reserved2();

    @VTID(44)
    void reserved3();

    @VTID(45)
    void reserved4();

    @VTID(46)
    void reserved5();

    @VTID(47)
    void reserved6();

    @VTID(48)
    void reserved7();

    @VTID(49)
    void reserved8();

    @VTID(50)
    void reserved9();

}
