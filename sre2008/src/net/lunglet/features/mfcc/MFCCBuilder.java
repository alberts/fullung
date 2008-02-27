package net.lunglet.features.mfcc;

import cz.vutbr.fit.speech.phnrec.MasterLabelFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

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
        this.deltaBuilder = new DeltaBuilder(5, 0, 14);
        this.deltaDeltaBuilder = new DeltaBuilder(5, 14, 28);
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
        Features[] features = new Features[htkFeatures.length];
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
        if (true) {
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
}
