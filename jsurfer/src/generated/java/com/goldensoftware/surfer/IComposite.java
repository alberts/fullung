package com.goldensoftware.surfer;

import com4j.IID;
import com4j.VTID;

/**
 * IComposite Interface
 */
@IID("{B293341C-9788-11D2-9780-00104B6D9C80}")
public interface IComposite extends com.goldensoftware.surfer.IShape {
    /**
     * Break apart the composite object into component objects
     */
    @VTID(30)
    void breakApart();

}
