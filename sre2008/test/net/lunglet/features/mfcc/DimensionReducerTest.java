package net.lunglet.features.mfcc;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public final class DimensionReducerTest {
    @Test
    public void test() {
        DimensionReducer dimReducer = new DimensionReducer();
        float[][] values = new float[1][13 + 2 * 13];
        for (int i = 0; i < values[0].length; i++) {
            values[0][i] = i;
        }
        FeatureSet f1 = new FeatureSet(values, 0, 0, true);
        FeatureSet f2 = dimReducer.apply(f1);
        assertEquals(12 + 2 * 13, f2.getValues()[0].length);
        for (int i = 0; i < 12; i++) {
            assertEquals(i, f2.getValues()[0][i], 0);
        }
        for (int i = 12; i < 38; i++) {
            assertEquals(i + 1, f2.getValues()[0][i], 0);
        }
    }
}
