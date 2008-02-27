package net.lunglet.features.mfcc;

public interface FeatureCombiner {
    float[][] combine(float[][] f1, float[][] f2);
}
