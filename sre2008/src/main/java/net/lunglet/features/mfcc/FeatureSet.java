package net.lunglet.features.mfcc;

import net.lunglet.util.ArrayUtils;

// TODO allow stuff like master label file to be attached to features as a property

public final class FeatureSet {
    private final int frameLength;

    private final int framePeriod;

    private final boolean hasEnergy;

    private final float[][] values;

    public FeatureSet(final float[][] values, final int framePeriod, final int frameLength, final boolean hasEnergy) {
        this.values = ArrayUtils.copyOf(values, values.length);
        this.framePeriod = framePeriod;
        this.frameLength = frameLength;
        this.hasEnergy = hasEnergy;
    }

    public int getFrameLengthHTK() {
        return frameLength;
    }

    public int getFramePeriodHTK() {
        return framePeriod;
    }

    public float[][] getValues() {
        return values;
    }

    public boolean hasEnergy() {
        return hasEnergy;
    }

    public FeatureSet replaceValues(final float[][] values) {
        return new FeatureSet(values, framePeriod, frameLength, hasEnergy);
    }
}
