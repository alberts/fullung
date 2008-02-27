package net.lunglet.features.mfcc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import net.lunglet.htk.HTKHeader;
import net.lunglet.htk.HTKInputStream;
import org.junit.Test;

public final class DeltaBuilderTest {
    private static void checkArray(final float expected, final float[][] actual) {
        for (int i = 0; i < actual.length; i++) {
            for (int j = 0; j < actual[i].length; j++) {
                assertEquals(expected, actual[i][j], 0);
            }
        }
    }

    @Test
    public void testDelta() {
        float[][] features = null;
        float[][] deltas = null;
        features = new float[][]{{}, {}};
        deltas = DeltaBuilder.delta(features, 0, 0);
        assertEquals(features.length, deltas.length);
        features = new float[][]{{1.0f, 2.0f, 3.0f}, {4.0f, 5.0f, 6.0f}};
        deltas = DeltaBuilder.delta(features, 0, 3);
        assertEquals(features.length, deltas.length);
        checkArray(0.9f, deltas);
        deltas = DeltaBuilder.delta(features, 0, 2);
        assertEquals(features.length, deltas.length);
        checkArray(0.9f, deltas);
        deltas = DeltaBuilder.delta(features, 1, 3);
        assertEquals(features.length, deltas.length);
        checkArray(0.9f, deltas);
        features = new float[][]{{1.0f, 2.0f, 3.0f}, {4.0f, 5.0f, 6.0f}, {7.0f, 8.0f, 9.0f}};
        deltas = DeltaBuilder.delta(features, 0, 3);
        assertEquals(features.length, deltas.length);
    }

    /**
     * <CODE>
     * TARGETKIND = MFCC_Z_D_A_T
     * NUMCEPS = 12
     * DELTAWINDOW = 2
     * ACCWINDOW = 2
     * THIRDWINDOW = 2
     * </CODE>
     */
    @Test
    public void testDeltaHTK() throws IOException {
        InputStream stream = getClass().getResourceAsStream("xdac.mfc");
        assertNotNull(stream);
        HTKInputStream in = new HTKInputStream(stream);
        in.mark(HTKHeader.SIZE);
        HTKHeader header = in.readHeader();
        assertFalse(header.hasC0());
        assertFalse(header.hasEnergy());
        assertTrue(header.hasDeltaCoefficients());
        assertTrue(header.hasAccelerationCoefficients());
        in.reset();
        float[][] mfcc = in.readMFCC();
        assertEquals(964, mfcc.length);
        assertEquals(4 * 12, mfcc[0].length);
        float[][] delta1 = DeltaBuilder.delta(mfcc, 0, 12);
        float[][] delta2 = DeltaBuilder.delta(delta1, 0, 12);
        float[][] delta3 = DeltaBuilder.delta(delta2, 0, 12);
        assertEquals(mfcc.length, delta1.length);
        assertEquals(mfcc.length, delta2.length);
        assertEquals(mfcc.length, delta3.length);
        for (int i = 0; i < mfcc.length; i++) {
            for (int j = 0; j < 12; j++) {
                assertEquals(mfcc[i][12 + j], delta1[i][j], 1.0e-6f);
                assertEquals(mfcc[i][24 + j], delta2[i][j], 1.0e-6f);
                assertEquals(mfcc[i][36 + j], delta3[i][j], 1.0e-6f);
            }
        }
    }
}
