package net.lunglet.features.mfcc;

public interface FeatureCombiner {
    Features combine(Features f1, Features f2);
}
