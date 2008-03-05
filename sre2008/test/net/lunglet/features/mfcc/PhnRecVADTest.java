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
        HTKMFCCBuilder htkMfccBuilder = new HTKMFCCBuilder();
        FeatureSet[] featuresArr = htkMfccBuilder.apply(audioStream);
        audioStream.close();
        assertEquals(2, featuresArr.length);
        FeatureSet features = featuresArr[0];
        InputStream mlfStream = getClass().getResourceAsStream("kajx.sph.0.mlf");
        assertNotNull(mlfStream);
        MasterLabelFile mlf = new MasterLabelFile(new InputStreamReader(mlfStream));
        mlfStream.close();
        PhnRecVAD vad = new PhnRecVAD();
        FeatureSet vadFeatures = vad.apply(features, mlf);
        float[][] values = features.getValues();
        float[][] vadValues = vadFeatures.getValues();
        assertEquals(values.length, vadValues.length);
    }
}
