package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * IEllipse Interface
 */
@IID("{B2933417-9788-11D2-9780-00104B6D9C80}")
public interface IEllipse extends com.goldensoftware.surfer.IShape {
    /**
     * Returns the line format object
     */
    @VTID(30)
    com.goldensoftware.surfer.ILineFormat line();

    /**
     * Returns the fill format object
     */
    @VTID(31)
    com.goldensoftware.surfer.IFillFormat fill();

}
