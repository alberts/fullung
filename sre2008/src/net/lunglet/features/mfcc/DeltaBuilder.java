package net.lunglet.features.mfcc;

import java.util.ArrayList;
import java.util.Arrays;
import net.lunglet.util.AssertUtils;

public final class DeltaBuilder {
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
                // calculate index of future sample, replicating last vector if
                // needed
                int p = Math.min(i + j, deltas.length - 1);
                // calculate index of past sample, replicating first vector if
                // needed
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

    private final int beginIndex;

    private final int endIndex;

    private final int minBlockSize;
    
    public DeltaBuilder(final int beginIndex, final int endIndex) {
        this(2, beginIndex, endIndex);
    }

    public DeltaBuilder(final int minBlockSize, final int beginIndex, final int endIndex) {
        if (minBlockSize < 2) {
            throw new IllegalArgumentException();
        }
        this.minBlockSize = minBlockSize;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    public Features apply(final Features features) {
        float[][] values = features.getValues();
        float[][] deltas = new float[values.length][];
        int firstBlockIndex = -1;
        ArrayList<float[]> blockList = new ArrayList<float[]>();
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) {
                blockList.add(values[i]);
                if (firstBlockIndex < 0) {
                    firstBlockIndex = i;
                }
            }
            if (values[i] == null || i == values.length - 1) {
                if (blockList.size() >= minBlockSize) {
                    float[][] block = blockList.toArray(new float[0][]);
                    float[][] deltaBlock = delta(block, beginIndex, endIndex);
                    for (int j = 0; j < blockList.size(); j++) {
                        deltas[j + firstBlockIndex] = deltaBlock[j];
                    }
                }
                firstBlockIndex = -1;
                blockList.clear();
            }
        }
        float[][] newValues = new float[values.length][];
        for (int i = 0; i < newValues.length; i++) {
            if (deltas[i] == null) {
                continue;
            }
            newValues[i] = Arrays.copyOf(values[i], values[i].length + deltas[i].length);
            System.arraycopy(deltas[i], 0, newValues[i], values[i].length, deltas[i].length);
        }
        return features.replaceValues(newValues);
    }
}
