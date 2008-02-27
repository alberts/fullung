package net.lunglet.features.mfcc;

import com.dvsoft.sv.toolbox.matrix.GaussWarp;
import com.dvsoft.sv.toolbox.matrix.JMatrix;

public final class GaussianWarper {
    private final Features[] features;

    public GaussianWarper(final Features[] features) {
        this.features = features;
    }

    public Features[] build() {
        Features[] warpedFeatures = new Features[features.length];
        int channels = features.length;
        for (int channel = 0; channel < channels; channel++) {
            float[][] values = features[channel].getValues();
            JMatrix mat = new JMatrix(values);
            mat = mat.transpose();
            GaussWarp.warp(mat);
            mat = mat.transpose();
            values = mat.toFloatArray();
            warpedFeatures[channel] = features[channel].replaceValues(values); 
        }
        return warpedFeatures;
    }
}
