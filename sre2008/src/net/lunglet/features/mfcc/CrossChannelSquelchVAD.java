package net.lunglet.features.mfcc;

import net.lunglet.util.AssertUtils;

public final class CrossChannelSquelchVAD {
    private float[][] apply(final Features[] features, final int channel) {
        float[][] mfcc = features[channel].getValues();
        float[][] otherMFCC = features[channel == 0 ? 1 : 0].getValues();
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

    public Features[] apply(final Features[] features) {
        if (features.length != 2) {
            throw new IllegalArgumentException();
        }
        Features[] squelchedFeatures = new Features[features.length];
        for (int channel = 0; channel < features.length; channel++) {
            float[][] values = apply(features, channel);
            squelchedFeatures[channel] = features[channel].replaceValues(values);
        }
        return squelchedFeatures;
    }
}
