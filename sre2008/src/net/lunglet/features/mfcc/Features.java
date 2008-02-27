package net.lunglet.features.mfcc;

import net.lunglet.util.ArrayUtils;

public final class Features {
    private final int frameLength;

    private final int framePeriod;

    private final boolean hasEnergy;

    private final float[][] values;

    public Features(final float[][] values, final int framePeriod, final int frameLength, final boolean hasEnergy) {
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

    public Features replaceValues(final float[][] values) {
        return new Features(values, framePeriod, frameLength, hasEnergy);
    }
}
