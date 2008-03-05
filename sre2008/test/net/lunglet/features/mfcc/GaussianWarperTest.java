package net.lunglet.features.mfcc;

import static org.junit.Assert.assertEquals;
import com.dvsoft.sv.toolbox.matrix.GaussWarp;
import com.dvsoft.sv.toolbox.matrix.JMatrix;
import java.util.ArrayList;
import org.junit.Ignore;
import org.junit.Test;

public final class GaussianWarperTest {
    @Ignore
    public void testGaussWarp() {
        // using a matrix exactly the size of the window causes an error
        JMatrix features = new JMatrix(10, 301);
        GaussWarp.warp(features);
    }

    @Test
    public void testTooSmallOdd() {
        float[][] v1 = new float[][]{{0.0f, 1.0f, 2.0f, 3.0f}};
        FeatureSet f1 = new FeatureSet(v1, 0, 0, false);
        GaussianWarper warper = new GaussianWarper();
        FeatureSet f2 = warper.apply(f1);
    }

    @Test
    public void testTooSmallEven() {
        float[][] v1 = new float[][]{{0.0f, 1.0f, 2.0f, 3.0f}, {0.0f, 1.0f, 2.0f, 3.0f}};
        FeatureSet f1 = new FeatureSet(v1, 0, 0, false);
        GaussianWarper warper = new GaussianWarper();
        FeatureSet f2 = warper.apply(f1);
    }

    @Test
    public void testTooSmall() {
        GaussianWarper warper = new GaussianWarper();
        ArrayList<float[]> valuesList = new ArrayList<float[]>();
        for (int i = 1; i <= 320; i++) {
            valuesList.clear();
            for (int j = 0; j < i; j++) {
                valuesList.add(new float[]{j, j, j});
            }
            float[][] values1 = valuesList.toArray(new float[0][]);
            FeatureSet f1 = new FeatureSet(values1, 0, 0, false);
            FeatureSet f2 = warper.apply(f1);
            float[][] values2 = f2.getValues();
            assertEquals(values1.length, values2.length);
            for (int j = 0; j < values2.length; j++) {
                assertEquals(values1[j].length, values2[j].length);
            }
        }
    }
}
