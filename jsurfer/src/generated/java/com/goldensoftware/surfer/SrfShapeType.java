package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfShapeType implements ComEnum {
    srfShapeRectangle(1),
    srfShapeRoundRect(2),
    srfShapeEllipse(3),
    srfShapeComposite(4),
    srfShapePolyline(5),
    srfShapePolygon(6),
    srfShapeText(7),
    srfShapeMetafile(8),
    srfShapeBitmap(9),
    srfShapeSymbol(10),
    srfShapeMapFrame(11),
    srfShapeBaseMap(12),
    srfShapeAxis(13),
    srfShapePostmap(14),
    srfShapeWireframe(15),
    srfShapeContourMap(16),
    srfShapeScale(17),
    srfShapeColorScale(18),
    srfShapeImageMap(19),
    srfShapeReliefMap(20),
    srfShapeLegend(21),
    srfShapeSurface(22),
    srfShapeVectorMap(23),
    srfShapeVariogram(24), ;

    private final int value;

    SrfShapeType(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
