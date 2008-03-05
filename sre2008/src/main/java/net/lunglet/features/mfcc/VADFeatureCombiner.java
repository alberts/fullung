package net.lunglet.features.mfcc;

public final class VADFeatureCombiner implements FeatureCombiner {
    @Override
    public FeatureSet combine(final FeatureSet f1, final FeatureSet f2) {
        float[][] v1 = f1.getValues();
        float[][] v2 = f2.getValues();
        if (v1.length != v2.length) {
            throw new IllegalArgumentException();
        }
        float[][] v = new float[v1.length][];
        for (int i = 0; i < v.length; i++) {
            // skip frames rejected by either VAD
            if (v1[i] == null || v2[i] == null) {
                continue;
            }
            v[i] = v1[i];
        }
        return f1.replaceValues(v);
    }
}
