package net.lunglet.features.mfcc;

import net.lunglet.util.AssertUtils;

public class DeltaBuilder {
    static float[][] delta(final float[][] features, final int beginIndex, final int endIndex) {
        AssertUtils.assertTrue(features.length >= 2);
        for (int i = 1; i < features.length; i++) {
            AssertUtils.assertTrue(features[0].length == features[i].length);
        }
        AssertUtils.assertTrue(beginIndex <= endIndex);
        AssertUtils.assertTrue(beginIndex >= 0);
        AssertUtils.assertTrue(endIndex <= features[0].length);
        float[][] deltas = new float[features.length][];
        for (int i = 0; i < deltas.length; i++) {
            deltas[i] = new float[endIndex - beginIndex];
        }
        for (int i = 0; i < deltas.length; i++) {
            for (int j = 1; j <= 2; j++) {
                // calculate index of future sample, replicating last vector if needed
                int p = Math.min(i + j, deltas.length - 1);
                // calculate index of past sample, replicating first vector if needed
                int m = Math.max(i - j, 0);
                for (int index = beginIndex; index < endIndex; index++) {
                    float cp = features[p][index];
                    float cm = features[m][index];
                    deltas[i][index - beginIndex] += j * (cp - cm);
                }
            }
            for (int index = beginIndex; index < endIndex; index++) {
                deltas[i][index - beginIndex] /= 2 * (1 * 1 + 2 * 2);
            }
        }
        return deltas;
    }
}
