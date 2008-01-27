package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * ILabelFormat Interface
 */
@IID("{B2933414-9788-11D2-9780-00104B6D9C80}")
public interface ILabelFormat extends Com4jObject {
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
     * Returns/sets the label format type
     */
    @VTID(9)
    com.goldensoftware.surfer.SrfLabelType type();

    /**
     * Returns/sets the label format type
     */
    @VTID(10)
    void type(com.goldensoftware.surfer.SrfLabelType pType);

    /**
     * Returns/sets the number of digits to the right of the decimal point
     */
    @VTID(11)
    int numDigits();

    /**
     * Returns/sets the number of digits to the right of the decimal point
     */
    @VTID(12)
    void numDigits(int pNumDigits);

    /**
     * Returns/sets the thousands style
     */
    @VTID(13)
    boolean thousands();

    /**
     * Returns/sets the thousands style
     */
    @VTID(14)
    void thousands(boolean pThousands);

    /**
     * Returns/sets the absolute value style
     */
    @VTID(15)
    boolean absoluteValue();

    /**
     * Returns/sets the absolute value style
     */
    @VTID(16)
    void absoluteValue(boolean pAbsValue);

    /**
     * Returns/sets the label prefix string
     */
    @VTID(17)
    java.lang.String prefix();

    /**
     * Returns/sets the label prefix string
     */
    @VTID(18)
    void prefix(java.lang.String pPrefix);

    /**
     * Returns/sets the label postfix string
     */
    @VTID(19)
    java.lang.String postfix();

    /**
     * Returns/sets the label postfix string
     */
    @VTID(20)
    void postfix(java.lang.String pPostfix);

}
