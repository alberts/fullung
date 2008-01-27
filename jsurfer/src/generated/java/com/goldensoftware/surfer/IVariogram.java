package com.goldensoftware.surfer;

import com4j.DefaultValue;
import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IVariogram Interface
 */
@IID("{B2933432-9788-11D2-9780-00104B6D9C80}")
public interface IVariogram extends com.goldensoftware.surfer.IShape {
    /**
     * Returns/sets the lag direction angle (in degrees)
     */
    @VTID(30)
    double lagDirection();

    /**
     * Returns/sets the lag direction angle (in degrees)
     */
    @VTID(31)
    void lagDirection(double pAngle);

    /**
     * Returns/sets the lag direction tolerance (in degrees)
     */
    @VTID(32)
    double lagTolerance();

    /**
     * Returns/sets the lag direction tolerance (in degrees)
     */
    @VTID(33)
    void lagTolerance(double pAngle);

    /**
     * Returns/sets the maximum lag distance (in XY data units)
     */
    @VTID(34)
    double maxLagDistance();

    /**
     * Returns/sets the maximum lag distance (in XY data units)
     */
    @VTID(35)
    void maxLagDistance(double pDist);

    /**
     * Returns/sets the number of lags
     */
    @VTID(36)
    int numLags();

    /**
     * Returns/sets the number of lags
     */
    @VTID(37)
    void numLags(int pNumLags);

    /**
     * Returns/sets the lag width (in XY data units)
     */
    @VTID(38)
    double lagWidth();

    /**
     * Returns/sets the lag width (in XY data units)
     */
    @VTID(39)
    void lagWidth(double pWidth);

    /**
     * Returns/sets the variogram estimation method
     */
    @VTID(40)
    com.goldensoftware.surfer.SrfVarioEstimator estimatorType();

    /**
     * Returns/sets the variogram estimation method
     */
    @VTID(41)
    void estimatorType(com.goldensoftware.surfer.SrfVarioEstimator pType);

    /**
     * Returns/sets the vertical scale
     */
    @VTID(42)
    double verticalScale();

    /**
     * Returns/sets the vertical scale
     */
    @VTID(43)
    void verticalScale(double pScale);

    /**
     * Returns/sets the plot title
     */
    @VTID(44)
    java.lang.String title();

    /**
     * Returns/sets the plot title
     */
    @VTID(45)
    void title(java.lang.String pTitle);

    /**
     * Returns the plot title font properties
     */
    @VTID(46)
    com.goldensoftware.surfer.IFontFormat titleFont();

    /**
     * Returns/sets the show plot symbols state
     */
    @VTID(47)
    boolean showSymbols();

    /**
     * Returns/sets the show plot symbols state
     */
    @VTID(48)
    void showSymbols(boolean pShow);

    /**
     * Returns/sets the show experimental line state
     */
    @VTID(49)
    boolean showExperimental();

    /**
     * Returns/sets the show experimental line state
     */
    @VTID(50)
    void showExperimental(boolean pShow);

    /**
     * Returns/sets the show model line state
     */
    @VTID(51)
    boolean showModel();

    /**
     * Returns/sets the show model line state
     */
    @VTID(52)
    void showModel(boolean pShow);

    /**
     * Returns/sets the show variance line state
     */
    @VTID(53)
    boolean showVariance();

    /**
     * Returns/sets the show variance line state
     */
    @VTID(54)
    void showVariance(boolean pShow);

    /**
     * Returns/sets the show subtitle state
     */
    @VTID(55)
    boolean showSubTitle();

    /**
     * Returns/sets the show subtitle state
     */
    @VTID(56)
    void showSubTitle(boolean pShow);

    /**
     * Returns the plot subtitle font properties
     */
    @VTID(57)
    com.goldensoftware.surfer.IFontFormat subTitleFont();

    /**
     * Returns/sets the show pairs state
     */
    @VTID(58)
    boolean showPairs();

    /**
     * Returns/sets the show pairs state
     */
    @VTID(59)
    void showPairs(boolean pShow);

    /**
     * Returns the pair annotation properties
     */
    @VTID(60)
    com.goldensoftware.surfer.IFontFormat pairsFont();

    /**
     * Returns the symbol properties
     */
    @VTID(61)
    com.goldensoftware.surfer.IMarkerFormat symbol();

    /**
     * Returns the model line properties
     */
    @VTID(62)
    com.goldensoftware.surfer.ILineFormat modelLine();

    /**
     * Returns the experimental line properties
     */
    @VTID(63)
    com.goldensoftware.surfer.ILineFormat experimentalLine();

    /**
     * Returns the variance line properties
     */
    @VTID(64)
    com.goldensoftware.surfer.ILineFormat varianceLine();

    /**
     * Returns/sets the array of variogram component objects
     */
    @VTID(65)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object model();

    /**
     * Returns/sets the array of variogram component objects
     */
    @VTID(66)
    void model(@MarshalAs(NativeType.VARIANT)
    java.lang.Object pModel);

    /**
     * Returns the axes collection
     */
    @VTID(67)
    com.goldensoftware.surfer.IAxes axes();

    @VTID(67)
    @ReturnValue(defaultPropertyThrough = {com.goldensoftware.surfer.IAxes.class})
    com.goldensoftware.surfer.IAxis axes(@MarshalAs(NativeType.VARIANT)
    java.lang.Object index);

    /**
     * Returns various data statistics
     */
    @VTID(68)
    double statistics(com.goldensoftware.surfer.SrfDataStats statistic);

    /**
     * Automatically fits variogram parameters to the current model
     */
    @VTID(69)
    void autoFit(@DefaultValue("3")
    com.goldensoftware.surfer.SrfVarioFitMethod fitMethod, @DefaultValue("9.99999974737875E-05")
    double precision, @DefaultValue("9.99999968028569E+37")
    double maxDistance, @DefaultValue("50")
    int iterations);

    /**
     * Exports the experimental variogram to a data file
     */
    @VTID(70)
    boolean export(java.lang.String fileName);

}
