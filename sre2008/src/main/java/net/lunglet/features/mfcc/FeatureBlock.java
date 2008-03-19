package net.lunglet.features.mfcc;

import java.util.ArrayList;
import java.util.Arrays;

// TODO construct with feature spacing so that it can calculate timestamps

// TODO rename begin -> first and end -> last

public final class FeatureBlock {
    private static final float FEATURE_SPACING = 10.0e-3f;

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

    private final int fromIndex;

    private final int toIndex;

    private final double meanEnergydB;

    private final float[][] values;

    public FeatureBlock(final int beginIndex, final int endIndex, final float[][] values) {
        if (beginIndex < 0) {
            throw new IllegalArgumentException();
        }
        if (endIndex < beginIndex) {
            throw new IllegalArgumentException();
        }
        if (values.length != endIndex - beginIndex) {
            throw new IllegalArgumentException();
        }
        this.fromIndex = beginIndex;
        this.toIndex = endIndex;
        this.values = values;
        this.meanEnergydB = getMeanEnergydB(values);
    }

    public void appendIndexes() {
        for (int i = fromIndex, j = 0; i < toIndex; i++, j++) {
            float[] v = values[j];
            float[] vi = Arrays.copyOf(v, v.length + 1);
            vi[vi.length - 1] = i;
            values[j] = vi;
        }
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public double getStartTime() {
        return fromIndex * FEATURE_SPACING;
    }

    public double getDuration() {
        return getEndTime() - getStartTime();
    }

    public int getToIndex() {
        return toIndex;
    }

    public double getEndTime() {
        return toIndex * FEATURE_SPACING;
    }

    public int getLength() {
        return toIndex - fromIndex;
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
        for (int i = fromIndex; i < toIndex; i++) {
            blockValues.add(otherValues[i]);
        }
        return getMeanEnergydB(blockValues.toArray(new float[0][]));
    }

    public float[][] getValues() {
        // return without copying to allow in-place modification
        return values;
    }

    @Override
    public String toString() {
        return getMeanEnergydB() + " " + getLength() + " " + getStartTime() + " -> " + getEndTime() + " ["
                + getDuration() + "]";
    }
}
