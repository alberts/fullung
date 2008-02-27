package net.lunglet.features.mfcc;

import net.lunglet.htk.HTKHeader;
import net.lunglet.util.AssertUtils;

public final class CrossChannelSquelchVAD {
    private final float[][][] mfccs;

    public CrossChannelSquelchVAD(final float[][][] mfccs, final HTKHeader header) {
        if (mfccs.length != 2) {
            throw new IllegalArgumentException();
        }
        if (!header.hasEnergy()) {
            throw new IllegalArgumentException();
        }
        this.mfccs = mfccs;
    }

    private float[][] build(final int channel) {
        float[][] mfcc = mfccs[channel];
        float[][] otherMFCC = mfccs[channel == 0 ? 1 : 0];
        float[][] newmfcc = new float[mfcc.length][];
        for (int i = 0; i < mfcc.length; i++) {
            AssertUtils.assertEquals(mfcc[i].length, otherMFCC[i].length);
            final int energyIndex = mfcc[i].length - 1;
            // convert from HTK normalised log energy 
            double e1 = Math.exp(mfcc[i][energyIndex] - 1.0);
            double e2 = Math.exp(otherMFCC[i][energyIndex] - 1.0);
            // keep frame if difference in energies is more than 3 dB
            if (10.0 * Math.log10(e1/e2) >= 3.0) {
                newmfcc[i] = mfcc[i];
            }
        }
        return newmfcc;
    }

    public float[][][] build() {
        // can't do cross channel squelch with a single channel
        if (mfccs.length == 1) {
            return mfccs;
        }
        float[][][] newmfccs = new float[mfccs.length][][];
        for (int i = 0; i < mfccs.length; i++) {
            newmfccs[i] = build(i);
        }
        return newmfccs;
    }
}
