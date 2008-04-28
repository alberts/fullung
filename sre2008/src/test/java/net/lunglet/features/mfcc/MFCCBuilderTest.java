package net.lunglet.features.mfcc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import cz.vutbr.fit.speech.phnrec.MasterLabelFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.lunglet.htk.HTKFlags;
import net.lunglet.htk.HTKOutputStream;
import org.junit.Test;

public final class MFCCBuilderTest {
    public static void writeFeatures(final String name, final FeatureSet features) throws IOException {
        HTKOutputStream out = new HTKOutputStream(name);
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
        MFCCBuilder mfccBuilder = new MFCCBuilder();
        MasterLabelFile[] mlfs = {getMLF(name + ".0.mlf")};
        FeatureSet[] features = mfccBuilder.apply(stream, mlfs);
        assertEquals(1, features.length);
        float[][] values = features[0].getValues();
        for (int i = 0; i < values.length; i++) {
            assertNotNull(values[i]);
        }
        if (true) {
            writeFeatures("xdac.sph.0.mfc", features[0]);
        }
    }

    @Test
    public void testShort() throws IOException, UnsupportedAudioFileException {
        String name = "kajx.sph";
        InputStream stream = getClass().getResourceAsStream(name);
        assertNotNull(stream);
        MFCCBuilder mfccBuilder = new MFCCBuilder();
        MasterLabelFile[] mlfs = {getMLF(name + ".0.mlf"), getMLF(name + ".1.mlf")};
        FeatureSet[] features = mfccBuilder.apply(stream, mlfs);
        assertEquals(2, features.length);
        if (true) {
            writeFeatures("kajx.sph.0.mfc", features[0]);
            writeFeatures("kajx.sph.1.mfc", features[1]);
        }
    }

    @Test
    public void testStereo() throws IOException, UnsupportedAudioFileException {
        String name = "jabo.sph";
        InputStream stream = getClass().getResourceAsStream(name);
        assertNotNull(stream);
        MFCCBuilder mfccBuilder = new MFCCBuilder();
        MasterLabelFile[] mlfs = {getMLF(name + ".0.mlf"), getMLF(name + ".1.mlf")};
        FeatureSet[] features = mfccBuilder.apply(stream, mlfs);
        assertEquals(2, features.length);
        float[][] values = features[0].getValues();
        for (int i = 0; i < values.length; i++) {
            assertNotNull(values[i]);
        }
        if (true) {
            writeFeatures("jabo.sph.0.mfc", features[0]);
            writeFeatures("jabo.sph.1.mfc", features[1]);
        }
    }
}
