package net.lunglet.features.mfcc;

import cz.vutbr.fit.speech.phnrec.MasterLabelFile;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

// TODO operate on FeatureVectors[] instead of crazy mfccs array and use streams instead of files

/**
 * Voice activity detector that uses PhnRec master label files.
 */
public final class PhnRecVAD {
    /** MFCC frame length in HTK units. */
    private static final int MFCC_FRAME_LENGTH = 200000;

    /** MFCC frame period in HTK units. */
    private static final int MFCC_FRAME_PERIOD = 100000;

    private final File audioFile;

    private final float[][][] mfccs;

    public PhnRecVAD(final File audioFile, final float[][] mfcc) {
        this(audioFile, new float[][][]{mfcc});
    }

    public PhnRecVAD(final File audioFile, final float[][][] mfccs) {
        this.audioFile = audioFile;
        this.mfccs = mfccs;
    }

    public float[][][] build() throws IOException {
        float[][][] newmfccs = new float[mfccs.length][][];
        for (int i = 0; i < mfccs.length; i++) {
            newmfccs[i] = build(i);
        }
        return newmfccs;
    }

    private float[][] build(final int channel) throws IOException {
        File mlfFile = createMLFFile(channel);
        FileReader reader = new FileReader(mlfFile);
        final MasterLabelFile mlf;
        try {
            mlf = new MasterLabelFile(reader);
        } finally {
            reader.close();
        }
        final double framePeriod = MFCC_FRAME_PERIOD / 1.0e7;
        final double frameLength = MFCC_FRAME_LENGTH / 1.0e7;
        float[][] mfcc = mfccs[channel];
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
        return newmfcc;
    }

    private File createMLFFile(final int channel) {
        return new File(audioFile.getAbsolutePath() + "." + channel + ".mlf");
    }
}
