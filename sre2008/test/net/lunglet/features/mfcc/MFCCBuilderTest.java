package net.lunglet.features.mfcc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import cz.vutbr.fit.speech.phnrec.MasterLabelFile;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.junit.Ignore;
import org.junit.Test;

public final class MFCCBuilderTest {
    private MasterLabelFile getMLF(final String name) throws IOException {
        InputStream stream = getClass().getResourceAsStream(name);
        assertNotNull(stream);
        return new MasterLabelFile(new InputStreamReader(stream));
    }

    private static void writeFeatures(final String name, final Features features) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(name));
        for (float[] v : features.getValues()) {
            if (v == null) {
                continue;
            }
            for (float f : v) {
                writer.write(f + " ");
            }
            writer.write("\n");
        }
        writer.close();
    }

    @Test
    public void test() throws IOException, UnsupportedAudioFileException {
        String name = "xdac.sph";
        InputStream stream = getClass().getResourceAsStream(name);
        assertNotNull(stream);
        MFCCBuilder mfccBuilder = new MFCCBuilder();
        MasterLabelFile[] mlfs = {getMLF(name + ".0.mlf")};
        Features[] features = mfccBuilder.apply(stream, mlfs);
        assertEquals(1, features.length);
        float[][] values = features[0].getValues();

        writeFeatures("xdac.sph.0.mfc.txt", features[0]);
    }

    @Ignore
    public void testShort() throws IOException, UnsupportedAudioFileException {
        String name = "kajx.sph";
        InputStream stream = getClass().getResourceAsStream(name);
        assertNotNull(stream);
        MFCCBuilder mfccBuilder = new MFCCBuilder();
        MasterLabelFile[] mlfs = {getMLF(name + ".0.mlf"), getMLF(name + ".1.mlf")};
        Features[] features = mfccBuilder.apply(stream, mlfs);
        assertEquals(2, features.length);
    }
}
