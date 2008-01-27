package com.goldensoftware.surfer;

import com4j.DefaultValue;
import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.VTID;

/**
 * IWksDocument Interface
 */
@IID("{B2933405-9788-11D2-9780-00104B6D9C80}")
public interface IWksDocument extends com.goldensoftware.surfer.IDocument {
    /**
     * Returns a Range object which encompasses all the used cells in the
     * worksheet.
     */
    @VTID(21)
    com.goldensoftware.surfer.IWksRange usedRange();

    /**
     * Returns/sets the default column width for the worksheet.
     */
    @VTID(22)
    double defaultColumnWidth();

    /**
     * Returns/sets the default column width for the worksheet.
     */
    @VTID(23)
    void defaultColumnWidth(double pVal);

    /**
     * Returns/sets the default row height for the worksheet.
     */
    @VTID(24)
    double defaultRowHeight();

    /**
     * Returns/sets the default row height for the worksheet.
     */
    @VTID(25)
    void defaultRowHeight(double pVal);

    /**
     * Returns the default Format object for the worksheet.
     */
    @VTID(26)
    com.goldensoftware.surfer.IWksCellFormat defaultFormat();

    /**
     * Returns the PageSetup object for the worksheet.
     */
    @VTID(27)
    com.goldensoftware.surfer.IWksPageSetup pageSetup();

    /**
     * Prints the worksheet.
     */
    @VTID(28)
    void printOut(@DefaultValue("1")
    int fromPage, @DefaultValue("65535")
    int toPage, @DefaultValue("1")
    int copies, @DefaultValue("0")
    boolean collate, @DefaultValue("")
    java.lang.String selection, @DefaultValue("")
    java.lang.String printerName, @DefaultValue("")
    java.lang.String printToFileName);

    /**
     * Combines a worksheet file with the current document
     */
    @VTID(29)
    boolean merge(java.lang.String fileName, @DefaultValue("-1")
    int row, @MarshalAs(NativeType.VARIANT)
    @DefaultValue("-1")
    java.lang.Object col, @DefaultValue("")
    java.lang.String options, @DefaultValue("-3")
    com.goldensoftware.surfer.wksFileFormat fileFormat);

    /**
     * Applies a mathematical transform equation to columns.
     */
    @VTID(30)
    boolean transform(int firstRow, int lastRow, java.lang.String equation);

    /**
     * Returns a Range object containing one or more cells.
     */
    @VTID(31)
    com.goldensoftware.surfer.IWksRange cells(@MarshalAs(NativeType.VARIANT)
    java.lang.Object row, @MarshalAs(NativeType.VARIANT)
    java.lang.Object col, @MarshalAs(NativeType.VARIANT)
    java.lang.Object lastRow, @MarshalAs(NativeType.VARIANT)
    java.lang.Object lastCol);

    /**
     * Returns a Range object containing one or more cells.
     */
    @VTID(32)
    com.goldensoftware.surfer.IWksRange range(@MarshalAs(NativeType.VARIANT)
    java.lang.Object row, @MarshalAs(NativeType.VARIANT)
    java.lang.Object col, @MarshalAs(NativeType.VARIANT)
    java.lang.Object lastRow, @MarshalAs(NativeType.VARIANT)
    java.lang.Object lastCol);

    /**
     * Returns a Range object containing one or more entire rows of cells.
     */
    @VTID(33)
    com.goldensoftware.surfer.IWksRange rows(@MarshalAs(NativeType.VARIANT)
    java.lang.Object row1, @MarshalAs(NativeType.VARIANT)
    java.lang.Object row2);

    /**
     * Returns a Range object containing one or more entire columns of cells.
     */
    @VTID(34)
    com.goldensoftware.surfer.IWksRange columns(@MarshalAs(NativeType.VARIANT)
    java.lang.Object col1, @MarshalAs(NativeType.VARIANT)
    java.lang.Object col2);

}
