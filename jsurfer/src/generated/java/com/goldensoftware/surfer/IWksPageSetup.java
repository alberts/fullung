package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * Represents the print settings for a worksheet.
 */
@IID("{00222CC7-2B05-11D2-9F99-482637000000}")
public interface IWksPageSetup extends Com4jObject {
    /**
     * Returns the Application object.
     */
    @VTID(7)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject application();

    /**
     * Returns the worksheet associated with this PageSetup object.
     */
    @VTID(8)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject parent();

    /**
     * Returns/sets the page orientation (landscape or portrait).
     */
    @VTID(9)
    com.goldensoftware.surfer.wksOrientation orientation();

    /**
     * Returns/sets the page orientation (landscape or portrait).
     */
    @VTID(10)
    void orientation(com.goldensoftware.surfer.wksOrientation pVal);

    /**
     * Returns/sets the paper size.
     */
    @VTID(11)
    com.goldensoftware.surfer.wksPaper paperSize();

    /**
     * Returns/sets the paper size.
     */
    @VTID(12)
    void paperSize(com.goldensoftware.surfer.wksPaper pVal);

    /**
     * Returns/sets the printer paper bin.
     */
    @VTID(13)
    com.goldensoftware.surfer.wksBin paperSource();

    /**
     * Returns/sets the printer paper bin.
     */
    @VTID(14)
    void paperSource(com.goldensoftware.surfer.wksBin pVal);

    /**
     * Returns/sets the maximum number of pages across to print.
     */
    @VTID(15)
    int fitToPagesAcross();

    /**
     * Returns/sets the maximum number of pages across to print.
     */
    @VTID(16)
    void fitToPagesAcross(int pVal);

    /**
     * Returns/sets the maximum number of pages down to print.
     */
    @VTID(17)
    int fitToPagesDown();

    /**
     * Returns/sets the maximum number of pages down to print.
     */
    @VTID(18)
    void fitToPagesDown(int pVal);

    /**
     * Returns/sets the printing enlargement or reduction percentage. A value of
     * zero indicates that reduction should be done according to the
     * FitToPagesAcross and FitToPagesDown settings.
     */
    @VTID(19)
    int adjustToPercentage();

    /**
     * Returns/sets the printing enlargement or reduction percentage. A value of
     * zero indicates that reduction should be done according to the
     * FitToPagesAcross and FitToPagesDown settings.
     */
    @VTID(20)
    void adjustToPercentage(int pVal);

    /**
     * Returns/sets the top margin distance.
     */
    @VTID(21)
    double topMargin();

    /**
     * Returns/sets the top margin distance.
     */
    @VTID(22)
    void topMargin(double pVal);

    /**
     * Returns/sets the bottom margin distance.
     */
    @VTID(23)
    double bottomMargin();

    /**
     * Returns/sets the bottom margin distance.
     */
    @VTID(24)
    void bottomMargin(double pVal);

    /**
     * Returns/sets the left margin distance.
     */
    @VTID(25)
    double leftMargin();

    /**
     * Returns/sets the left margin distance.
     */
    @VTID(26)
    void leftMargin(double pVal);

    /**
     * Returns/sets the right margin distance.
     */
    @VTID(27)
    double rightMargin();

    /**
     * Returns/sets the right margin distance.
     */
    @VTID(28)
    void rightMargin(double pVal);

    /**
     * Returns/sets the header margin distance.
     */
    @VTID(29)
    double headerMargin();

    /**
     * Returns/sets the header margin distance.
     */
    @VTID(30)
    void headerMargin(double pVal);

    /**
     * Returns/sets the footer margin distance.
     */
    @VTID(31)
    double footerMargin();

    /**
     * Returns/sets the footer margin distance.
     */
    @VTID(32)
    void footerMargin(double pVal);

    /**
     * Returns/sets whether to center the output horizontally.
     */
    @VTID(33)
    boolean centerHorizontally();

    /**
     * Returns/sets whether to center the output horizontally.
     */
    @VTID(34)
    void centerHorizontally(boolean pVal);

    /**
     * Returns/sets whether to center the output vertically.
     */
    @VTID(35)
    boolean centerVertically();

    /**
     * Returns/sets whether to center the output vertically.
     */
    @VTID(36)
    void centerVertically(boolean pVal);

    /**
     * Returns/sets whether grid lines will be printed.
     */
    @VTID(37)
    boolean printGridlines();

    /**
     * Returns/sets whether grid lines will be printed.
     */
    @VTID(38)
    void printGridlines(boolean pVal);

    /**
     * Returns/sets whether row and column headers will be printed.
     */
    @VTID(39)
    boolean printRowAndColumnHeader();

    /**
     * Returns/sets whether row and column headers will be printed.
     */
    @VTID(40)
    void printRowAndColumnHeader(boolean pVal);

    /**
     * Returns/sets whether output will be forced to be in black and white.
     */
    @VTID(41)
    boolean printBlackAndWhite();

    /**
     * Returns/sets whether output will be forced to be in black and white.
     */
    @VTID(42)
    void printBlackAndWhite(boolean pVal);

    /**
     * Returns/sets whether multiple pages will be printed across rows first and
     * then down (vesus down columns first and then across).
     */
    @VTID(43)
    boolean printAcrossAndThenDown();

    /**
     * Returns/sets whether multiple pages will be printed across rows first and
     * then down (vesus down columns first and then across).
     */
    @VTID(44)
    void printAcrossAndThenDown(boolean pVal);

    /**
     * Returns/sets the page header text.
     */
    @VTID(45)
    java.lang.String header();

    /**
     * Returns/sets the page header text.
     */
    @VTID(46)
    void header(java.lang.String pVal);

    /**
     * Returns/sets the page footer text.
     */
    @VTID(47)
    java.lang.String footer();

    /**
     * Returns/sets the page footer text.
     */
    @VTID(48)
    void footer(java.lang.String pVal);

    @VTID(49)
    void reserved0();

    @VTID(50)
    void reserved1();

    @VTID(51)
    void reserved2();

    @VTID(52)
    void reserved3();

    @VTID(53)
    void reserved4();

    @VTID(54)
    void reserved5();

    @VTID(55)
    void reserved6();

    @VTID(56)
    void reserved7();

    @VTID(57)
    void reserved8();

    @VTID(58)
    void reserved9();

}
