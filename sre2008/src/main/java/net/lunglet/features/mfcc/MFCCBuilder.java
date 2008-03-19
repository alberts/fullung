package net.lunglet.features.mfcc;

import cz.vutbr.fit.speech.phnrec.MasterLabelFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.lunglet.htk.HTKFlags;
import net.lunglet.htk.HTKOutputStream;
import net.lunglet.util.AssertUtils;

public final class MFCCBuilder {
    private static final boolean DEBUG = true;

    private static final int MIN_BLOCK_SIZE = 20;

    private static void convertFile(final MFCCBuilder mfccBuilder, final String name)
            throws UnsupportedAudioFileException, IOException {
        File sphFile = new File(name);
        System.err.println("Reading " + sphFile);
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(sphFile);
        int channels = aff.getFormat().getChannels();
        ArrayList<MasterLabelFile> mlfs = new ArrayList<MasterLabelFile>();
        for (int i = 0; i < channels; i++) {
            File mlfFile = new File(sphFile.getAbsolutePath() + "." + i + ".mlf");
            System.err.println("Reading " + mlfFile);
            mlfs.add(new MasterLabelFile(mlfFile));
        }
        FeatureSet[] features = mfccBuilder.apply(sphFile, mlfs.toArray(new MasterLabelFile[0]));
        for (int i = 0; i < channels; i++) {
            File mfccFile = new File(sphFile.getAbsolutePath() + "." + i + ".mfc.gz");
            System.err.println("Writing " + mfccFile);
            writeMFCC(mfccFile, features[i]);
        }
    }

    public static void main(final String[] args) throws IOException  {
        MFCCBuilder mfccBuilder = new MFCCBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        while (line != null && line.trim().length() > 0) {
            try {
                String name = line.trim();
                convertFile(mfccBuilder, name);
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            line = reader.readLine();
        }
        reader.close();
    }

    public static void writeMFCC(final File file, final FeatureSet features) throws IOException {
        HTKOutputStream out = new HTKOutputStream(file);
        int flags = 0;
        if (features.hasEnergy()) {
            flags |= HTKFlags.HAS_ENERGY;
            flags |= HTKFlags.SUPPRESS_ABSOLUTE_ENERGY;
        }
        flags |= HTKFlags.HAS_DELTA;
        flags |= HTKFlags.HAS_ACCELERATION;
        int framePeriod = features.getFramePeriodHTK();
        float[][] values = features.getValues();
        out.writeMFCC(values, framePeriod, flags);
        out.close();
    }

    private final DeltaBuilder deltaBuilder;

    private final DeltaBuilder deltaDeltaBuilder;

    private final DimensionReducer dimensionReducer;

    private final ExcludedFrameRemover frameRemover;

    private final GaussianWarper gaussianWarper;

    private final HTKMFCCBuilder htkmfcc;

    private final PhnRecVAD phnrecVAD;

    private final CrossChannelSquelchVAD squelchVAD;

    private final VADFeatureCombiner vadFeatureCombiner;

    public MFCCBuilder() {
        this.htkmfcc = new HTKMFCCBuilder();
        this.phnrecVAD = new PhnRecVAD();
        this.squelchVAD = new CrossChannelSquelchVAD();
        this.vadFeatureCombiner = new VADFeatureCombiner();
        this.gaussianWarper = new GaussianWarper();
        this.deltaBuilder = new DeltaBuilder(MIN_BLOCK_SIZE, 0, 13);
        this.deltaDeltaBuilder = new DeltaBuilder(MIN_BLOCK_SIZE, 13, 26);
        this.dimensionReducer = new DimensionReducer();
        this.frameRemover = new ExcludedFrameRemover();
    }

    public FeatureSet[] apply(final File file, final MasterLabelFile[] mlfs) throws UnsupportedAudioFileException,
            IOException {
        return apply(new FileInputStream(file), mlfs);
    }

    public FeatureSet[] apply(final InputStream stream, final MasterLabelFile[] mlfs)
            throws UnsupportedAudioFileException, IOException {
        FeatureSet[] htkFeatures = htkmfcc.apply(stream);
        FeatureSet[] phnRecVADFeatures = new FeatureSet[htkFeatures.length];
        for (int i = 0; i < htkFeatures.length; i++) {
            phnRecVADFeatures[i] = phnrecVAD.apply(htkFeatures[i], mlfs[i]);
        }

        final FeatureSet[] features;
        if (true) {
            // use cross channel squelch on stereo data
            features = new FeatureSet[htkFeatures.length];
            if (htkFeatures.length > 1) {
                FeatureSet[] squelchFeatures = squelchVAD.apply(htkFeatures);
                for (int i = 0; i < htkFeatures.length; i++) {
                    features[i] = vadFeatureCombiner.combine(phnRecVADFeatures[i], squelchFeatures[i]);
                    phnRecVADFeatures[i] = null;
                    squelchFeatures[i] = null;
                }
            } else {
                features[0] = phnRecVADFeatures[0];
            }
        } else {
            features = phnRecVADFeatures;
        }

        phnRecVADFeatures = null;
        for (int i = 0; i < features.length; i++) {
            features[i] = gaussianWarper.apply(features[i]);
        }
        for (int i = 0; i < features.length; i++) {
            features[i] = deltaBuilder.apply(features[i]);
        }
        for (int i = 0; i < features.length; i++) {
            features[i] = deltaDeltaBuilder.apply(features[i]);
        }
        for (int i = 0; i < features.length; i++) {
            features[i] = dimensionReducer.apply(features[i]);
        }
        // XXX debugging code
        if (DEBUG) {
            IndexAppender indexAppender = new IndexAppender();
            for (int i = 0; i < features.length; i++) {
                features[i] = indexAppender.apply(features[i]);
            }
        }
        for (int i = 0; i < features.length; i++) {
            features[i] = frameRemover.apply(features[i]);
            // check that we got at least one block of features
            AssertUtils.assertTrue(features[i].getValues().length >= MIN_BLOCK_SIZE);
        }
        return features;
    }
}
