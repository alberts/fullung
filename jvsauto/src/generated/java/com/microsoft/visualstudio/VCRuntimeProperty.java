package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCRuntimeProperty Interface
 */
@IID("{17D1B7B3-5827-43BE-A70B-6A91BD4CB154}")
public interface VCRuntimeProperty extends Com4jObject {
    @VTID(7)
    java.lang.String name();

    @VTID(8)
    void name(
        java.lang.String pbstrName);

    @VTID(9)
    boolean isReadOnly();

    @VTID(10)
    void isReadOnly(
        boolean pbReadOnly);

    @VTID(11)
    java.lang.String displayName();

    @VTID(12)
    void displayName(
        java.lang.String pbstrDisplayName);

    @VTID(13)
    java.lang.String propertyPageName();

    @VTID(14)
    void propertyPageName(
        java.lang.String pbstrPropertyPageName);

    @VTID(15)
    java.lang.String category();

    @VTID(16)
    void category(
        java.lang.String pbstrCategory);

    @VTID(17)
    java.lang.String description();

    @VTID(18)
    void description(
        java.lang.String pbstrDescription);

    @VTID(19)
    java.lang.String helpURL();

    @VTID(20)
    void helpURL(
        java.lang.String pbstrHelpURL);

    @VTID(21)
    int helpContext();

    @VTID(22)
    void helpContext(
        int plHelpContext);

    @VTID(23)
    java.lang.String helpFile();

    @VTID(24)
    void helpFile(
        java.lang.String pbstrHelpFile);

    @VTID(25)
    java.lang.String helpF1Keyword();

    @VTID(26)
    void helpF1Keyword(
        java.lang.String pbstrKeyword);

}
