package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * IVectorLegend Interface
 */
@IID("{B2933430-9788-11D2-9780-00104B6D9C80}")
public interface IVectorLegend extends com.goldensoftware.surfer.ILegend {
    /**
     * Returns/sets the reference vector magnitudes
     */
    @VTID(39)
    java.lang.String magnitudes();

    /**
     * Returns/sets the reference vector magnitudes
     */
    @VTID(40)
    void magnitudes(java.lang.String pMagnitudes);

    /**
     * Returns the vector legend layout style
     */
    @VTID(41)
    com.goldensoftware.surfer.SrfVecLayout layout();

    /**
     * Returns the vector legend layout style
     */
    @VTID(42)
    void layout(com.goldensoftware.surfer.SrfVecLayout pLayout);

    /**
     * Returns the label font format object
     */
    @VTID(43)
    com.goldensoftware.surfer.IFontFormat labelFont();

    /**
     * Returns the label format object
     */
    @VTID(44)
    com.goldensoftware.surfer.ILabelFormat labelFormat();

}
