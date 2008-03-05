package net.lunglet.features.mfcc;

import java.util.ArrayList;
import java.util.Arrays;

public final class FeatureBlock {
    private static double getMeanEnergydB(final float[][] values) {
        double meanEnergy = 0.0;
        int n = 0;
        for (float[] v : values) {
            n++;
            final int energyIndex = v.length - 1;
            // convert from HTK normalised log energy to absolute energy
            double energy = Math.exp(v[energyIndex] - 1.0);
            meanEnergy += (energy - meanEnergy) / n;
        }
        return 10.0 * Math.log10(meanEnergy);
    }

    private final int beginIndex;

    private final int endIndex;

    private final double meanEnergydB;

    private final float[][] values;

    public FeatureBlock(final int beginIndex, final int endIndex, final float[][] values) {
        if (values.length != endIndex - beginIndex) {
            throw new IllegalArgumentException();
        }
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.values = values;
        this.meanEnergydB = getMeanEnergydB(values);
    }

    /**
     * Returns the mean energy in this block in dB.
     */
    public double getMeanEnergydB() {
        return meanEnergydB;
    }

    public double getMeanEnergydB(final FeatureSet features) {
        if (!features.hasEnergy()) {
            throw new IllegalArgumentException();
        }
        ArrayList<float[]> blockValues = new ArrayList<float[]>();
        float[][] otherValues = features.getValues();
        for (int i = beginIndex; i < endIndex; i++) {
            blockValues.add(otherValues[i]);
        }
        return getMeanEnergydB(blockValues.toArray(new float[0][]));
    }

    public float[][] getValues() {
        // return without copying to allow in-place modification
        return values;
    }

    public int getLength() {
        return endIndex - beginIndex;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public double getBeginTime() {
        return beginIndex * 10.0e-3;
    }

    public double getEndTime() {
        return endIndex * 10.0e-3;
    }

    public void appendIndexes() {
        for (int i = beginIndex, j = 0; i < endIndex; i++, j++) {
            float[] v = values[j];
            float[] vi = Arrays.copyOf(v, v.length + 1);
            vi[vi.length - 1] = i;
            values[j] = vi;
        }
    }
}
