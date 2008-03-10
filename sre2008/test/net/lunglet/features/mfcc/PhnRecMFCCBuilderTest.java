package net.lunglet.features.mfcc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import cz.vutbr.fit.speech.phnrec.MasterLabelFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.junit.Test;

public final class PhnRecMFCCBuilderTest {
    private MasterLabelFile getMLF(final String name) throws IOException {
        InputStream stream = getClass().getResourceAsStream(name);
        assertNotNull(stream);
        return new MasterLabelFile(new InputStreamReader(stream));
    }

    @Test
    public void testMono() throws IOException, UnsupportedAudioFileException {
        String name = "xdac.sph";
        InputStream stream = getClass().getResourceAsStream(name);
        assertNotNull(stream);
        YAMFCCBuilder mfccBuilder = new YAMFCCBuilder();
        MasterLabelFile[] mlfs = {getMLF(name + ".0.mlf")};
        FeatureSet[] features = mfccBuilder.apply(stream, mlfs);
        assertEquals(1, features.length);
        float[][] values = features[0].getValues();
        for (int i = 0; i < values.length; i++) {
            assertNotNull(values[i]);
        }
        if (true) {
            MFCCBuilderTest.writeFeatures("xdac.sph.0.mfc", features[0]);
        }
    }

    @Test
    public void testStereo() throws IOException, UnsupportedAudioFileException {
        String name = "jabo.sph";
        InputStream stream = getClass().getResourceAsStream(name);
        assertNotNull(stream);
        YAMFCCBuilder mfccBuilder = new YAMFCCBuilder();
        MasterLabelFile[] mlfs = {getMLF(name + ".0.mlf"), getMLF(name + ".1.mlf")};
        FeatureSet[] features = mfccBuilder.apply(stream, mlfs);
        assertEquals(2, features.length);
        float[][] values = features[0].getValues();
        for (int i = 0; i < values.length; i++) {
            assertNotNull(values[i]);
        }
        if (true) {
            MFCCBuilderTest.writeFeatures("jabo.sph.0.mfc", features[0]);
            MFCCBuilderTest.writeFeatures("jabo.sph.1.mfc", features[1]);
        }
    }
}
