package net.lunglet.features.mfcc;

import cz.vutbr.fit.speech.phnrec.MasterLabelFile;

/**
 * Voice activity detector that uses PhnRec master label files.
 */
public final class PhnRecVAD {
    private final Features features;

    private final MasterLabelFile mlf;

    public PhnRecVAD(final Features features, final MasterLabelFile mlf) {
        this.features = features;
        this.mlf = mlf;
    }

    public Features build() {
        final double framePeriod = features.getFramePeriodHTK() / 1.0e7;
        final double frameLength = features.getFrameLengthHTK() / 1.0e7;
        float[][] mfcc = features.getValues();
        float[][] newmfcc = new float[mfcc.length][];
        for (int i = 0; i < mfcc.length; i++) {
            double start = framePeriod * i;
            double end = start + frameLength;
            // deal with slight mismatch in timestamps due to differences
            // with MFCC parameters used for phoneme recognizer
            if (!mlf.containsTimestamp(start) || !mlf.containsTimestamp(end)) {
                // discard remaining frames
                break;
            }
            // keep frame if it contains only speech phonemes
            if (mlf.isOnlySpeech(start, end)) {
                newmfcc[i] = mfcc[i];
            }
        }
        return features.replaceValues(newmfcc);
    }
}
