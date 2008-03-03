package net.lunglet.features.mfcc;

import cz.vutbr.fit.speech.phnrec.MasterLabelFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.lunglet.htk.HTKFlags;
import net.lunglet.htk.HTKOutputStream;

public final class MFCCBuilder {
    private final DeltaBuilder deltaBuilder;

    private final DeltaBuilder deltaDeltaBuilder;

    private final GaussianWarper gaussianWarper;

    private final HTKMFCCBuilder htkmfcc;

    private final PhnRecVAD phnrecVAD;

    private final CrossChannelSquelchVAD squelchVAD;

    private final VADFeatureCombiner vadFeatureCombiner;

    private final DimensionReducer dimensionReducer;

    private final ExcludedFrameRemover frameRemover;

    public MFCCBuilder() {
        this.htkmfcc = new HTKMFCCBuilder();
        this.phnrecVAD = new PhnRecVAD();
        this.squelchVAD = new CrossChannelSquelchVAD();
        this.vadFeatureCombiner = new VADFeatureCombiner();
        this.gaussianWarper = new GaussianWarper();
        int minBlockSize = 20;
        this.deltaBuilder = new DeltaBuilder(minBlockSize, 0, 14);
        this.deltaDeltaBuilder = new DeltaBuilder(minBlockSize, 14, 28);
        this.dimensionReducer = new DimensionReducer();
        this.frameRemover = new ExcludedFrameRemover();
    }

    public Features[] apply(final File file, final MasterLabelFile[] mlfs) throws UnsupportedAudioFileException,
            IOException {
        return apply(new FileInputStream(file), mlfs);
    }

    public Features[] apply(final InputStream stream, final MasterLabelFile[] mlfs)
            throws UnsupportedAudioFileException, IOException {
        Features[] htkFeatures = htkmfcc.apply(stream);
        Features[] phnRecVADFeatures = new Features[htkFeatures.length];
        for (int i = 0; i < htkFeatures.length; i++) {
            phnRecVADFeatures[i] = phnrecVAD.apply(htkFeatures[i], mlfs[i]);
        }

        final Features[] features;
        if (true) {
            // use cross channel squelch on stereo data
            features = new Features[htkFeatures.length];
            if (htkFeatures.length > 1) {
                Features[] squelchFeatures = squelchVAD.apply(htkFeatures);
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
        if (false) {
            IndexAppender indexAppender = new IndexAppender();
            for (int i = 0; i < features.length; i++) {
                features[i] = indexAppender.apply(features[i]);
            }
        }
        for (int i = 0; i < features.length; i++) {
            features[i] = frameRemover.apply(features[i]);
        }
        return features;
    }

    private static void writeMFCC(final File file, final Features features) throws IOException {
        HTKOutputStream out = new HTKOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
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

    public static void main(final String[] args) throws IOException, UnsupportedAudioFileException {
        // TODO get rid of C0 completely

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        while (line != null) {
            File sphFile = new File(line.trim());
            System.err.println("Reading " + sphFile);
            AudioFileFormat aff = AudioSystem.getAudioFileFormat(sphFile);
            int channels = aff.getFormat().getChannels();
            ArrayList<MasterLabelFile> mlfs = new ArrayList<MasterLabelFile>();
            for (int i = 0; i < channels; i++) {
                File mlfFile = new File(sphFile.getAbsolutePath() + "." + i + ".mlf");
                System.err.println("Reading " + mlfFile);
                mlfs.add(new MasterLabelFile(mlfFile));
            }
            MFCCBuilder mfccBuilder = new MFCCBuilder();
            Features[] features = mfccBuilder.apply(sphFile, mlfs.toArray(new MasterLabelFile[0]));
            for (int i = 0; i < channels; i++) {
                File mfccFile = new File(sphFile.getAbsolutePath() + "." + i + ".mfc.gz");
                System.err.println("Writing " + mfccFile);
                writeMFCC(mfccFile, features[i]);
            }
            line = reader.readLine();
        }
        reader.close();
    }
}
