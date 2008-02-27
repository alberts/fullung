package net.lunglet.features.mfcc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import cz.vutbr.fit.speech.phnrec.MasterLabelFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.junit.Test;

public final class PhnRecVADTest {
    @Test
    public void test() throws IOException, UnsupportedAudioFileException {
        InputStream audioStream = getClass().getResourceAsStream("kajx.sph");
        assertNotNull(audioStream);
        HTKMFCCBuilder htkMfccBuilder = new HTKMFCCBuilder(audioStream);
        Features[] featuresArr = htkMfccBuilder.build();
        assertEquals(2, featuresArr.length);
        Features features = featuresArr[0];
        audioStream.close();
        InputStream mlfStream = getClass().getResourceAsStream("kajx.sph.0.mlf");
        assertNotNull(mlfStream);
        MasterLabelFile mlf = new MasterLabelFile(new InputStreamReader(mlfStream));
        mlfStream.close();
        PhnRecVAD vad = new PhnRecVAD(features, mlf);
        Features vadFeatures = vad.build();
        float[][] values = features.getValues();
        float[][] vadValues = vadFeatures.getValues();
        assertEquals(values.length, vadValues.length);
    }
}
