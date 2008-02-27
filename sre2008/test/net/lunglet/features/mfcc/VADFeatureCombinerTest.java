package net.lunglet.features.mfcc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import cz.vutbr.fit.speech.phnrec.MasterLabelFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.junit.Test;

public final class VADFeatureCombinerTest {
    @Test
    public void test() throws UnsupportedAudioFileException, IOException {
        InputStream audioStream = getClass().getResourceAsStream("kajx.sph");
        assertNotNull(audioStream);
        HTKMFCCBuilder htkMfccBuilder = new HTKMFCCBuilder(audioStream);
        Features[] features = htkMfccBuilder.build();
        assertEquals(2, features.length);

        Features[] phnRecFeatures = new Features[features.length];
        for (int i = 0; i < features.length; i++) {
            InputStream mlfStream = getClass().getResourceAsStream("kajx.sph." + i + ".mlf");
            assertNotNull(mlfStream);
            MasterLabelFile mlf = new MasterLabelFile(new InputStreamReader(mlfStream));
            mlfStream.close();
            PhnRecVAD vad = new PhnRecVAD(features[i], mlf);
            phnRecFeatures[i] = vad.build();
        }

        CrossChannelSquelchVAD vad = new CrossChannelSquelchVAD(features);
        Features[] squelchFeatures = vad.build();

        FeatureCombiner featureCombiner = new VADFeatureCombiner();
        Features[] finalFeatures = new Features[features.length];
        for (int i = 0; i < features.length; i++) {
            finalFeatures[i] = featureCombiner.combine(phnRecFeatures[i], squelchFeatures[i]);
        }
    }
}
