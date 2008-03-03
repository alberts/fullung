package net.lunglet.features.mfcc;

import cz.vutbr.fit.speech.phnrec.MasterLabelFile;

/**
 * Voice activity detector that uses PhnRec master label files.
 */
public final class PhnRecVAD {
    public Features apply(final Features features, final MasterLabelFile mlf) {
        final double framePeriod = features.getFramePeriodHTK() / 1.0e7;
        final double frameLength = features.getFrameLengthHTK() / 1.0e7;
        float[][] mfcc = features.getValues();
        float[][] newmfcc = new float[mfcc.length][];
        // get maximum energy in dB
        double maxEnergydB = getMaxEnergydB(mfcc);
        for (int i = 0; i < mfcc.length; i++) {
            double start = framePeriod * i;
            double end = start + frameLength;
            // deal with slight mismatch in timestamps due to differences
            // with MFCC parameters used for phoneme recognizer
            if (!mlf.containsTimestamp(start) || !mlf.containsTimestamp(end)) {
                // discard remaining frames
                break;
            }
            // keep frame if its energy is more than the maximum energy minus 30
            // dB and if it contains only speech phonemes
            double energydB = getEnergydB(mfcc[i]);
            if (maxEnergydB - energydB > 30.0) {
                continue;
            }
            if (!mlf.isOnlySpeech(start, end)) {
                continue;
            }
            newmfcc[i] = mfcc[i];
        }
        return features.replaceValues(newmfcc);
    }

    static double getEnergydB(final float[] mfcc) {
        final int energyIndex = mfcc.length - 1;
        // convert from HTK normalised log energy
        double energy = Math.exp(mfcc[energyIndex] - 1.0);
        return 10.0 * Math.log10(energy);
    }

    static double getMaxEnergydB(final float[][] mfcc) {
        double maxEnergy = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < mfcc.length; i++) {
            double energy = getEnergydB(mfcc[i]);
            if (energy > maxEnergy) {
                maxEnergy = energy;
            }
        }
        return maxEnergy;
    }
}
