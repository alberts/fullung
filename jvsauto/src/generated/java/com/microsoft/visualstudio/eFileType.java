package com.microsoft.visualstudio  ;

import com4j.*;

public enum eFileType implements ComEnum {
    eFileTypeDefault(-1),
    eFileTypeCppCode(0),
    eFileTypeCppClass(1),
    eFileTypeCppHeader(2),
    eFileTypeCppForm(3),
    eFileTypeCppControl(4),
    eFileTypeText(5),
    eFileTypeDEF(6),
    eFileTypeIDL(7),
    eFileTypeMakefile(8),
    eFileTypeRGS(9),
    eFileTypeRC(10),
    eFileTypeRES(11),
    eFileTypeXSD(12),
    eFileTypeXML(13),
    eFileTypeHTML(14),
    eFileTypeCSS(15),
    eFileTypeBMP(16),
    eFileTypeICO(17),
    eFileTypeResx(18),
    eFileTypeScript(19),
    eFileTypeBSC(20),
    eFileTypeXSX(21),
    eFileTypeCppWebService(22),
    eFileTypeAsax(23),
    eFileTypeAspPage(24),
    eFileTypeDocument(25),
    eFileTypeDiscomap(26),
    eFileTypeCSharpFile(28),
    eFileTypeClassDiagram(29),
    eFileTypeMHT(30),
    eFileTypePropertySheet(31),
    eFileTypeCUR(32),
    eFileTypeManifest(33),
    ;

    private final int value;
    eFileType(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
