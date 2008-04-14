package net.lunglet.sre2008.hlda;

import com.dvsoft.sv.toolbox.matrix.JMatrix;
import com.dvsoft.sv.toolbox.matrix.JVector;
import java.io.Serializable;

/**
 * Stats for estimating full covariance matrices.
 */
public final class JMapStats2 implements Serializable {
    private static final long serialVersionUID = 1L;

    public final JVector[] ex;

    public final JMatrix[] exx;

    public final double[] n;

    public double totLL;

    public double totN;

    public JMapStats2(int noMixtures, int dimension) {
        this.ex = new JVector[noMixtures];
        this.exx = new JMatrix[noMixtures];
        for (int i = 0; i < noMixtures; i++) {
            ex[i] = new JVector(dimension);
            exx[i] = new JMatrix(dimension, dimension);
        }
        this.n = new double[noMixtures];
    }

    public double add(int[] indices, double[] posteriors, JVector frame) {
        double sum = 0.0;
        JMatrix xx = JVector.multiply(frame, frame.transpose());
        for (int i = 0; i < posteriors.length; i++) {
            int m = indices[i];
            // post is L_j(t) from eq. 37 in Kumar/SC paper
            double post = posteriors[i];
            if (post == 0.0) {
                continue;
            }
            // this is Nj in eq. 38 in Kumar/SC paper
            n[m] += post;
            sum += post;
            // this is L_j(t) * o_t in eq. 42 in Kumar/SC paper
            ex[m].add(post, frame);
            exx[m].add(post, xx);
        }
        // this is N in eq. 39 in Kumar/SC paper
        totN += sum;
        return sum;
    }

    public void add(final JMapStats2 stats) {
        if (n.length != stats.n.length) {
            throw new RuntimeException("attempt to add incompatible JMapStats2");
        }
        this.totN += stats.totN;
        this.totLL += stats.totLL;
        for (int i = 0; i < n.length; i++) {
            this.n[i] += stats.n[i];
            this.ex[i].add(1.0f, stats.ex[i]);
            this.exx[i].add(1.0f, stats.exx[i]);
        }
    }
}
