package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.DefaultValue;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IVarioComponent Interface
 */
@IID("{B293340F-9788-11D2-9780-00104B6D9C80}")
public interface IVarioComponent extends Com4jObject {
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
     * Returns the component type
     */
    @VTID(9)
    com.goldensoftware.surfer.SrfVarioType type();

    /**
     * Returns the scale (error variance for nugget)
     */
    @VTID(10)
    double param1();

    /**
     * Returns the length (micro variance for nugget)
     */
    @VTID(11)
    double param2();

    /**
     * Returns the power used (power component only)
     */
    @VTID(12)
    double power();

    /**
     * Returns the anisotropy ratio (not used for nugget)
     */
    @VTID(13)
    double anisotropyRatio();

    /**
     * Returns the anisotropy angle in degrees (not used for nugget)
     */
    @VTID(14)
    double anisotropyAngle();

    /**
     * Sets the parameters for this variogram component
     */
    @VTID(15)
    void set(com.goldensoftware.surfer.SrfVarioType varioType, double param1, double param2, @DefaultValue("1")
    double power, @DefaultValue("1")
    double anisotropyRatio, @DefaultValue("0")
    double anisotropyAngle);

    /**
     * Returns the lower autofit limit for the specified parameter
     */
    @VTID(16)
    double lowerFitLimit(com.goldensoftware.surfer.SrfVarioParam param);

    /**
     * Returns the upper autofit limit for the specified parameter
     */
    @VTID(17)
    double upperFitLimit(com.goldensoftware.surfer.SrfVarioParam param);

    /**
     * Sets the autofit parameter limits
     */
    @VTID(18)
    void setFitLimits(com.goldensoftware.surfer.SrfVarioParam param, double lowerLimit, double upperLimit);

}
