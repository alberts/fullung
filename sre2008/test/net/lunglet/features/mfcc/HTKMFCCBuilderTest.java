package net.lunglet.features.mfcc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.junit.Test;

public final class HTKMFCCBuilderTest {
    private static void checkFeatureVectors(final Features f) {
        assertTrue(f.hasEnergy());
        assertEquals(100000, f.getFramePeriodHTK());
        assertEquals(200000, f.getFrameLengthHTK());
        float[][] mfcc = f.getValues();
        for (int i = 0; i < mfcc.length; i++) {
            assertEquals(13, mfcc[i].length);
        }
    }

    @Test
    public void testMono() throws UnsupportedAudioFileException, IOException {
        InputStream stream = getClass().getResourceAsStream("xdac.sph");
        assertNotNull(stream);
        HTKMFCCBuilder builder = new HTKMFCCBuilder();
        Features[] features = builder.apply(stream);
        stream.close();
        assertEquals(1, features.length);
        checkFeatureVectors(features[0]);
        assertEquals(964, features[0].getValues().length);
    }

    @Test
    public void testStereo() throws UnsupportedAudioFileException, IOException {
        InputStream stream = getClass().getResourceAsStream("kajx.sph");
        assertNotNull(stream);
        HTKMFCCBuilder builder = new HTKMFCCBuilder();
        Features[] features = builder.apply(stream);
        assertEquals(2, features.length);
        checkFeatureVectors(features[0]);
        assertEquals(1287, features[0].getValues().length);
        checkFeatureVectors(features[1]);
        assertEquals(1287, features[1].getValues().length);
    }
}
