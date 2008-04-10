package net.lunglet.sre2008.util;

import com.dvsoft.sv.toolbox.gmm.JMapGMM;
import com.dvsoft.sv.toolbox.matrix.JVector;
import java.util.ArrayList;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.gmm.DiagCovGMM;

public final class Converters {
    public static JMapGMM convert(final DiagCovGMM src) {
        float[] srcWeights = src.getWeights().toArray();
        double[] destWeights = new double[srcWeights.length];
        for (int i = 0; i < srcWeights.length; i++) {
            destWeights[i] = srcWeights[i];
        }
        ArrayList<JVector> means = new ArrayList<JVector>();
        ArrayList<JVector> vars = new ArrayList<JVector>();
        for (int i = 0; i < src.getMixtureCount(); i++) {
            means.add(new JVector(src.getMean(i).toArray()));
            vars.add(new JVector(src.getVariance(i).toArray()));
        }
        return new JMapGMM(destWeights, means.toArray(new JVector[0]), vars.toArray(new JVector[0]));
    }

    public static DiagCovGMM convert(final JMapGMM src) {
        FloatVector weights = DenseFactory.floatVector(src.getWeights());
        ArrayList<FloatVector> means = new ArrayList<FloatVector>();
        ArrayList<FloatVector> vars = new ArrayList<FloatVector>();
        for (int i = 0; i < src.getNoMixtures(); i++) {
            means.add(DenseFactory.floatVector(src.getMeans()[i].data));
            vars.add(DenseFactory.floatVector(src.getVariances()[i].data));
        }
        return new DiagCovGMM(weights, means, vars);
    }

    private Converters() {
    }
}
