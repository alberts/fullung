package net.lunglet.features.mfcc;

import java.util.ArrayList;

public final class ExcludedFrameRemover {
    public FeatureSet apply(final FeatureSet features) {
        float[][] values = features.getValues();
        ArrayList<float[]> nonNullFrames = new ArrayList<float[]>();
        for (float[] v : values) {
            if (v == null) {
                continue;
            }
            nonNullFrames.add(v);
        }
        return features.replaceValues(nonNullFrames.toArray(new float[0][]));
    }
}
