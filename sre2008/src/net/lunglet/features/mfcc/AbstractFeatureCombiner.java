package net.lunglet.features.mfcc;

public abstract class AbstractFeatureCombiner implements FeatureCombiner {
    public final float[][][] combine(float[][][] f1, float[][][] f2) {
        if (f1.length != f2.length) {
            throw new IllegalArgumentException();
        }
        float[][][] result = new float[f1.length][][];
        for (int i = 0; i < f1.length; i++) {
            result[i] = combine(f1[i], f2[i]);
        }
        return result;
    }

    public final float[][] combine(float[][] f1, float[][] f2) {
        if (f1.length != f2.length) {
            throw new IllegalArgumentException();
        }
        return reallyCombine(f1, f2);
    }

    protected abstract float[][] reallyCombine(float[][] f1, float[][] f2);
}
