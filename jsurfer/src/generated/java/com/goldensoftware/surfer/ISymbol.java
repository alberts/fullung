package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * ISymbol Interface
 */
@IID("{B2933418-9788-11D2-9780-00104B6D9C80}")
public interface ISymbol extends com.goldensoftware.surfer.IShape {
    /**
     * Returns the symbol format object
     */
    @VTID(30)
    com.goldensoftware.surfer.IMarkerFormat marker();

}
