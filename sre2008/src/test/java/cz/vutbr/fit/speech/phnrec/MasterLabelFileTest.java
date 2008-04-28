package cz.vutbr.fit.speech.phnrec;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.junit.Test;

public final class MasterLabelFileTest {
    @Test
    public void testBasics() throws IOException {
        InputStream stream = getClass().getResourceAsStream("xdac.sph.0.mlf");
        assertNotNull(stream);
        Reader reader = new InputStreamReader(stream);
        MasterLabelFile mlf = new MasterLabelFile(reader);
        assertFalse(mlf.isOnlySpeech(0.0, 0.0));
        assertFalse(mlf.isOnlySpeech(0.0, 0.39));
        assertFalse(mlf.isOnlySpeech(0.1, 0.2));
        assertFalse(mlf.isOnlySpeech(0.39, 0.68));
        assertTrue(mlf.isOnlySpeech(0.68, 0.76));
        assertTrue(mlf.isOnlySpeech(0.68, 0.68));
        assertTrue(mlf.isOnlySpeech(0.69, 0.75));
        assertFalse(mlf.isOnlySpeech(0.76, 1.07));
        assertFalse(mlf.isOnlySpeech(0.68, 0.77));
        assertTrue(mlf.isOnlySpeech(8.97, 9.3));
        assertFalse(mlf.isOnlySpeech(9.63, 9.63));
        reader.close();
    }

    @Test
    public void testNoSpeech() throws IOException {
        InputStream stream = getClass().getResourceAsStream("jcvu.sph.0.mlf");
        assertNotNull(stream);
        Reader reader = new InputStreamReader(stream);
        MasterLabelFile mlf = new MasterLabelFile(reader);
        assertFalse(mlf.isOnlySpeech(0.0, 0.0));
        assertFalse(mlf.isOnlySpeech(0.0, 299.93));
        assertFalse(mlf.isOnlySpeech(299.63, 299.93));
        reader.close();
    }
}
