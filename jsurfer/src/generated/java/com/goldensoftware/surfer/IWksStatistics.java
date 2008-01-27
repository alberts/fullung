package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.DefaultValue;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * Holds the results of a statistics calculation.
 */
@IID("{00222CC8-2B05-11D2-9F99-482637000000}")
public interface IWksStatistics extends Com4jObject {
    /**
     * Returns the Application object.
     */
    @VTID(7)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject application();

    /**
     * Returns the worksheet from which the Statistics were calculated.
     */
    @VTID(8)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject parent();

    /**
     * Returns the number of columns for which statistics are stored.
     */
    @VTID(9)
    int columnCount();

    /**
     * Returns the label for the column containing the data from which the
     * statistics were calculated.
     */
    @VTID(10)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object label(@DefaultValue("1")
    int col);

    /**
     * Returns the first row from which the data were retrieved
     */
    @VTID(11)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object firstRow(@DefaultValue("1")
    int col);

    /**
     * Returns the last row from which the data were retrieved
     */
    @VTID(12)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object lastRow(@DefaultValue("1")
    int col);

    /**
     * Returns the number of data values in the column from which the statistics
     * were calculated.
     */
    @VTID(13)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object count(@DefaultValue("1")
    int col);

    /**
     * Returns the number of missing data values in the column from which the
     * statistics were calculated.
     */
    @VTID(14)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object missing(@DefaultValue("1")
    int col);

    /**
     * Returns the sum of the data values in the column from which the
     * statistics were calculated.
     */
    @VTID(15)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object sum(@DefaultValue("1")
    int col);

    /**
     * Returns the mimimum of the data values in the column from which the
     * statistics were calculated.
     */
    @VTID(16)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object minimum(@DefaultValue("1")
    int col);

    /**
     * Returns the maximum of the data values in the column from which the
     * statistics were calculated.
     */
    @VTID(17)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object maximum(@DefaultValue("1")
    int col);

    /**
     * Returns the difference between the maximum and minimum data values in the
     * column from which the statistics were calculated.
     */
    @VTID(18)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object range(@DefaultValue("1")
    int col);

    /**
     * Returns the mean of the data values in the column from which the
     * statistics were calculated.
     */
    @VTID(19)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object mean(@DefaultValue("1")
    int col);

    /**
     * Returns the median of the data values in the column from which the
     * statistics were calculated.
     */
    @VTID(20)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object median(@DefaultValue("1")
    int col);

    /**
     * Returns the first quartile (25th percentile) of the data values in the
     * column from which the statistics were calculated.
     */
    @VTID(21)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object firstQuartile(@DefaultValue("1")
    int col);

    /**
     * Returns the third quartile (75th percentile) of the data values in the
     * column from which the statistics were calculated.
     */
    @VTID(22)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object thirdQuartile(@DefaultValue("1")
    int col);

    /**
     * Returns the standard error of the data values in the column from which
     * the statistics were calculated.
     */
    @VTID(23)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object standardError(@DefaultValue("1")
    int col);

    /**
     * Returns the 95% confidence interval for the mean of the data values in
     * the column from which the statistics were calculated.
     */
    @VTID(24)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object confidenceInterval95(@DefaultValue("1")
    int col);

    /**
     * Returns the 99% confidence interval for the mean of the data values in
     * the column from which the statistics were calculated.
     */
    @VTID(25)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object confidenceInterval99(@DefaultValue("1")
    int col);

    /**
     * Returns the variance of the data values in the column from which the
     * statistics were calculated.
     */
    @VTID(26)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object variance(@DefaultValue("1")
    int col);

    /**
     * Returns the average deviation of the data values in the column from which
     * the statistics were calculated.
     */
    @VTID(27)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object averageDeviation(@DefaultValue("1")
    int col);

    /**
     * Returns the standard deviation of the data values in the column from
     * which the statistics were calculated.
     */
    @VTID(28)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object standardDeviation(@DefaultValue("1")
    int col);

    /**
     * Returns the coefficient of variation of the data values in the column
     * from which the statistics were calculated.
     */
    @VTID(29)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object coefficientOfVariation(@DefaultValue("1")
    int col);

    /**
     * Returns the coefficient of skewness for the data values in the column
     * from which the statistics were calculated.
     */
    @VTID(30)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object skewness(@DefaultValue("1")
    int col);

    /**
     * Returns the coefficient of kurtosis for the data values in the column
     * from which the statistics were calculated.
     */
    @VTID(31)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object kurtosis(@DefaultValue("1")
    int col);

    /**
     * Returns the Kolmogorov-Smirnov goodness-of-fit statistic (versus a normal
     * probablility curve) for the data values in the column from which the
     * statistics were calculated.
     */
    @VTID(32)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object ksStatistic(@DefaultValue("1")
    int col);

    /**
     * Returns the 90% critical value of the Kolmogorov-Smirnov statistic for
     * the number of data values in the column from which the statistics were
     * calculated.
     */
    @VTID(33)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object ksCriticalValue90(@DefaultValue("1")
    int col);

    /**
     * Returns the 95% critical value of the Kolmogorov-Smirnov statistic for
     * the number of data values in the column from which the statistics were
     * calculated.
     */
    @VTID(34)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object ksCriticalValue95(@DefaultValue("1")
    int col);

    /**
     * Returns the 99% critical value of the Kolmogorov-Smirnov statistic for
     * the number of data values in the column from which the statistics were
     * calculated.
     */
    @VTID(35)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object ksCriticalValue99(@DefaultValue("1")
    int col);

    @VTID(36)
    void reserved0();

    @VTID(37)
    void reserved1();

    @VTID(38)
    void reserved2();

    @VTID(39)
    void reserved3();

    @VTID(40)
    void reserved4();

    @VTID(41)
    void reserved5();

    @VTID(42)
    void reserved6();

    @VTID(43)
    void reserved7();

    @VTID(44)
    void reserved8();

    @VTID(45)
    void reserved9();

}
