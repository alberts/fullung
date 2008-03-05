package net.lunglet.features.mfcc;

import net.lunglet.util.AssertUtils;

public final class CrossChannelSquelchVAD {
    private float[][] apply(final FeatureSet[] features, final int channel) {
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

    private float[][] apply2(final FeatureSet[] features, final int channel) {
        float[][] mfcc = features[channel].getValues();
        double thresholdEnergydB = PhnRecVAD.getMaxEnergydB(mfcc) - 3.0;
        float[][] otherMFCC = features[channel == 0 ? 1 : 0].getValues();
        float[][] newmfcc = new float[mfcc.length][];
        for (int i = 0; i < mfcc.length; i++) {
            AssertUtils.assertEquals(mfcc[i].length, otherMFCC[i].length);
            double otherEnerygdB = PhnRecVAD.getEnergydB(otherMFCC[i]);
            // check if energy in opposite channel is bigger than maximum energy
            // less 3 dB in this channel
            if (otherEnerygdB > thresholdEnergydB) {
                continue;
            }
            newmfcc[i] = mfcc[i];
        }
        return newmfcc;
    }

    public FeatureSet[] apply(final FeatureSet[] features) {
        if (features.length != 2) {
            throw new IllegalArgumentException();
        }
        FeatureSet[] squelchedFeatures = new FeatureSet[features.length];
        for (int channel = 0; channel < features.length; channel++) {
            float[][] values = apply2(features, channel);
            squelchedFeatures[channel] = features[channel].replaceValues(values);
        }
        return squelchedFeatures;
    }
}
