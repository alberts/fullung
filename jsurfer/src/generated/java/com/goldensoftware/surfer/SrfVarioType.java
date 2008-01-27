package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum SrfVarioType implements ComEnum {
    srfVarExponential(1),
    srfVarGaussian(2),
    srfVarLinear(3),
    srfVarLogarithmic(4),
    srfVarNugget(5),
    srfVarPower(6),
    srfVarQuadratic(7),
    srfVarRationalQuadratic(8),
    srfVarSpherical(9),
    srfVarWave(10),
    srfVarCubic(11),
    srfVarPentaspherical(12), ;

    private final int value;

    SrfVarioType(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
