package net.lunglet.features.mfcc;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MFCCBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(MFCCBuilder.class);

    public MFCCBuilder(final File audioFile) {
    }

    public void build() {
        // TODO write mfcc file for each channel
        // combine phnrecvad and xchansquelchvad output (both have to agree that frame is speech) to get final mfccs
        // chop this output into blocks maybe
        // pass each block to deltabuilder
        // merge blocks to pass to gaussianizer
        // combine delta and delta-delta
        // combine gaussianized stuff with delta stuff
    }
}
