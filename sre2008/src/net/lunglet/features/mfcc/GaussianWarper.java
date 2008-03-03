package net.lunglet.features.mfcc;

import com.dvsoft.sv.toolbox.matrix.GaussWarp;
import com.dvsoft.sv.toolbox.matrix.JMatrix;
import com.dvsoft.sv.toolbox.matrix.JVector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.lunglet.util.AssertUtils;

public final class GaussianWarper {
    // use a window size of 302 to work around a bug in GaussWarp
    private static final int WINDOW_SIZE = 302;

    public Features apply(final Features features) {
        float[][] values = features.getValues();
        ArrayList<float[]> validValues = new ArrayList<float[]>();
        for (float[] v : values) {
            if (v != null) {
                validValues.add(v);
            }
        }
        AssertUtils.assertTrue(validValues.size() > 0);
        JMatrix mat = new JMatrix(validValues.toArray(new float[0][]));
        mat = mat.transpose();
        // pad up to expected window size to make gaussianization work
        if (validValues.size() < WINDOW_SIZE) {
            JVector mean = mat.meanOfColumns();
            JVector stddev = mat.columnScatter(mean).diagonal();
            stddev.scal(1.0 / mat.noColumns());
            stddev.sqrt();
            float[] xp = mean.plus(stddev).transpose().toFloatArray()[0];
            float[] xm = mean.minus(stddev).transpose().toFloatArray()[0];
            int requiredElements = WINDOW_SIZE - validValues.size();
            if (requiredElements % 2 == 0) {
                for (int i = 0; i < requiredElements / 2; i++) {
                    validValues.add(xp);
                    validValues.add(xm);
                }
            } else {
                for (int i = 0; i < requiredElements / 2; i++) {
                    validValues.add(xp);
                    validValues.add(xm);
                }
                validValues.add(mean.transpose().toFloatArray()[0]);
            }
            mat = new JMatrix(validValues.toArray(new float[0][]));
            mat = mat.transpose();
        }
        AssertUtils.assertTrue(mat.noColumns() >= WINDOW_SIZE);
        GaussWarp.warp(mat);
        mat = mat.transpose();
        float[][] warpedValues = mat.toFloatArray();
        List<float[]> warpedList = new ArrayList<float[]>(Arrays.asList(warpedValues));
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) {
                values[i] = warpedList.remove(0);
            }
        }
        return features.replaceValues(values);
    }
}
