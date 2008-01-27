package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IPageSetup Interface
 */
@IID("{B293340B-9788-11D2-9780-00104B6D9C80}")
public interface IPageSetup extends Com4jObject {
    /**
     * Returns the Application object
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
     * Returns/sets the paper orientation
     */
    @VTID(9)
    com.goldensoftware.surfer.SrfPaperOrientation orientation();

    /**
     * Returns/sets the paper orientation
     */
    @VTID(10)
    void orientation(com.goldensoftware.surfer.SrfPaperOrientation pOrient);

    /**
     * Returns/sets the paper width in page units
     */
    @VTID(11)
    double width();

    /**
     * Returns/sets the paper width in page units
     */
    @VTID(12)
    void width(double pWidth);

    /**
     * Returns/sets the paper height in page units
     */
    @VTID(13)
    double height();

    /**
     * Returns/sets the paper height in page units
     */
    @VTID(14)
    void height(double pHeight);

    /**
     * Returns/sets the left margin in page units
     */
    @VTID(15)
    double leftMargin();

    /**
     * Returns/sets the left margin in page units
     */
    @VTID(16)
    void leftMargin(double pLeft);

    /**
     * Returns/sets the top margin in page units
     */
    @VTID(17)
    double topMargin();

    /**
     * Returns/sets the top margin in page units
     */
    @VTID(18)
    void topMargin(double pTop);

    /**
     * Returns/sets the right margin in page units
     */
    @VTID(19)
    double rightMargin();

    /**
     * Returns/sets the right margin in page units
     */
    @VTID(20)
    void rightMargin(double pRight);

    /**
     * Returns/sets the bottom margin in page units
     */
    @VTID(21)
    double bottomMargin();

    /**
     * Returns/sets the bottom margin in page units
     */
    @VTID(22)
    void bottomMargin(double pBottom);

}
