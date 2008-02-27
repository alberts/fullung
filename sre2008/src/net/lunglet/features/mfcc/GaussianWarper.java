package net.lunglet.features.mfcc;

import com.dvsoft.sv.toolbox.matrix.GaussWarp;
import com.dvsoft.sv.toolbox.matrix.JMatrix;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.lunglet.util.AssertUtils;

public final class GaussianWarper {
    public Features apply(final Features features) {
        float[][] values = features.getValues();
        ArrayList<float[]> validValues = new ArrayList<float[]>();
        for (float[] v : values) {
            if (v != null) {
                validValues.add(v);
            }
        }
        JMatrix mat = new JMatrix(validValues.toArray(new float[0][]));
        mat = mat.transpose();
        GaussWarp.warp(mat);
        mat = mat.transpose();
        float[][] warpedValues = mat.toFloatArray();
        List<float[]> warpedList = new ArrayList<float[]>(Arrays.asList(warpedValues));
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) {
                values[i] = warpedList.remove(0);
            }
        }
        AssertUtils.assertTrue(warpedList.isEmpty());
        return features.replaceValues(values);
    }
}
