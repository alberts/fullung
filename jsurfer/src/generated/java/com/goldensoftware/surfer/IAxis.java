package com.goldensoftware.surfer;

import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.VTID;

/**
 * IAxis Interface
 */
@IID("{B293341F-9788-11D2-9780-00104B6D9C80}")
public interface IAxis extends com.goldensoftware.surfer.IShape {
    /**
     * Returns the axis type
     */
    @VTID(30)
    com.goldensoftware.surfer.SrfAxisType axisType();

    /**
     * Returns/sets the axis title
     */
    @VTID(31)
    java.lang.String title();

    /**
     * Returns/sets the axis title
     */
    @VTID(32)
    void title(java.lang.String pTitle);

    /**
     * Returns/sets the title offset along the axis in page units
     */
    @VTID(33)
    double titleOffset1();

    /**
     * Returns/sets the title offset along the axis in page units
     */
    @VTID(34)
    void titleOffset1(double pOff1);

    /**
     * Returns/sets the title offset perpendicular to the axis in page units
     */
    @VTID(35)
    double titleOffset2();

    /**
     * Returns/sets the title offset perpendicular to the axis in page units
     */
    @VTID(36)
    void titleOffset2(double pOff2);

    /**
     * Returns/sets the title angle in degrees from the axis
     */
    @VTID(37)
    double titleAngle();

    /**
     * Returns/sets the title angle in degrees from the axis
     */
    @VTID(38)
    void titleAngle(double pAngle);

    /**
     * Returns the title font format object
     */
    @VTID(39)
    com.goldensoftware.surfer.IFontFormat titleFont();

    /**
     * Returns/sets the plane of the axis
     */
    @VTID(40)
    com.goldensoftware.surfer.SrfAxisPlane axisPlane();

    /**
     * Returns/sets the plane of the axis
     */
    @VTID(41)
    void axisPlane(com.goldensoftware.surfer.SrfAxisPlane pPlane);

    /**
     * Returns the axis line format object
     */
    @VTID(42)
    com.goldensoftware.surfer.ILineFormat axisLine();

    /**
     * Returns/sets the show labels state
     */
    @VTID(43)
    boolean showLabels();

    /**
     * Returns/sets the show labels state
     */
    @VTID(44)
    void showLabels(boolean pShow);

    /**
     * Returns/sets the label angle from the axis in degrees
     */
    @VTID(45)
    double labelAngle();

    /**
     * Returns/sets the label angle from the axis in degrees
     */
    @VTID(46)
    void labelAngle(double pAngle);

    /**
     * Returns/sets the label offset from the axis in page units
     */
    @VTID(47)
    double labelOffset();

    /**
     * Returns/sets the label offset from the axis in page units
     */
    @VTID(48)
    void labelOffset(double pOff);

    /**
     * Returns the label font object
     */
    @VTID(49)
    com.goldensoftware.surfer.IFontFormat labelFont();

    /**
     * Returns the label format object
     */
    @VTID(50)
    com.goldensoftware.surfer.ILabelFormat labelFormat();

    /**
     * Returns/sets the minor tick type
     */
    @VTID(51)
    com.goldensoftware.surfer.SrfTickType minorTickType();

    /**
     * Returns/sets the minor tick type
     */
    @VTID(52)
    void minorTickType(com.goldensoftware.surfer.SrfTickType pType);

    /**
     * Returns/sets the minor tick length in page units
     */
    @VTID(53)
    double minorTickLength();

    /**
     * Returns/sets the minor tick length in page units
     */
    @VTID(54)
    void minorTickLength(double pLength);

    /**
     * Returns/sets the major tick type
     */
    @VTID(55)
    com.goldensoftware.surfer.SrfTickType majorTickType();

    /**
     * Returns/sets the major tick type
     */
    @VTID(56)
    void majorTickType(com.goldensoftware.surfer.SrfTickType pType);

    /**
     * Returns/sets the major tick length in page units
     */
    @VTID(57)
    double majorTickLength();

    /**
     * Returns/sets the major tick length in page units
     */
    @VTID(58)
    void majorTickLength(double pLength);

    /**
     * Returns/sets the number of minor ticks per major tick
     */
    @VTID(59)
    int minorTicksPerMajor();

    /**
     * Returns/sets the number of minor ticks per major tick
     */
    @VTID(60)
    void minorTicksPerMajor(int pMinPerMaj);

    /**
     * Returns/sets the auto scale state
     */
    @VTID(61)
    boolean autoScale();

    /**
     * Returns/sets the auto scale state
     */
    @VTID(62)
    void autoScale(boolean pAutoScale);

    /**
     * Returns the axis minimum in map units
     */
    @VTID(63)
    double minimum();

    /**
     * Returns the axis maximum in map units
     */
    @VTID(64)
    double maximum();

    /**
     * Returns the interval between major ticks in map units
     */
    @VTID(65)
    double majorInterval();

    /**
     * Returns the value of the first major tick in map units
     */
    @VTID(66)
    double firstMajorTick();

    /**
     * Returns the value of the last major tick in map units
     */
    @VTID(67)
    double lastMajorTick();

    /**
     * Returns the axis crossing along the orthogonal axis (in map units)
     */
    @VTID(68)
    double cross1();

    /**
     * Returns the axis crossing along the orthogonal axis (in map units)
     */
    @VTID(69)
    double cross2();

    /**
     * Returns/sets the show major grid lines state
     */
    @VTID(70)
    boolean showMajorGridLines();

    /**
     * Returns/sets the show major grid lines state
     */
    @VTID(71)
    void showMajorGridLines(boolean pShow);

    /**
     * Returns the major grid line format object
     */
    @VTID(72)
    com.goldensoftware.surfer.ILineFormat majorGridLine();

    /**
     * Returns/sets the show minor grid lines state
     */
    @VTID(73)
    boolean showMinorGridLines();

    /**
     * Returns/sets the show minor grid lines state
     */
    @VTID(74)
    void showMinorGridLines(boolean pShow);

    /**
     * Returns the minor grid line format object
     */
    @VTID(75)
    com.goldensoftware.surfer.ILineFormat minorGridLine();

    /**
     * Sets the axis scaling
     */
    @VTID(76)
    void setScale(@MarshalAs(NativeType.VARIANT)
    java.lang.Object minimum, @MarshalAs(NativeType.VARIANT)
    java.lang.Object maximum, @MarshalAs(NativeType.VARIANT)
    java.lang.Object majorInterval, @MarshalAs(NativeType.VARIANT)
    java.lang.Object firstMajorTick, @MarshalAs(NativeType.VARIANT)
    java.lang.Object lastMajorTick, @MarshalAs(NativeType.VARIANT)
    java.lang.Object cross1, @MarshalAs(NativeType.VARIANT)
    java.lang.Object cross2);

}
