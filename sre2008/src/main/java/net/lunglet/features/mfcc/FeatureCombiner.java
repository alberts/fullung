package net.lunglet.features.mfcc;

public interface FeatureCombiner {
    FeatureSet combine(FeatureSet f1, FeatureSet f2);
}
