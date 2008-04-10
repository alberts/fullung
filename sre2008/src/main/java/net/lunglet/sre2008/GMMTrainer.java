package net.lunglet.sre2008;

import java.util.List;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.array4j.matrix.util.FloatMatrixUtils;
import net.lunglet.gmm.DiagCovGMM;
import net.lunglet.gmm.GMMMAPStats;
import net.lunglet.gmm.GMMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GMMTrainer {
    private static final int C = 5;

    private static final Logger LOGGER = LoggerFactory.getLogger(GMMTrainer.class);

    private static final int MAP_ITERATIONS = 10;

    private static final float RELEVANCE = 16.0f;

    public static FloatVector train(final DiagCovGMM ubm, final Iterable<FloatDenseVector> data) {
        LOGGER.info("Calculating stats on UBM");
        GMMMAPStats ubmStats = new GMMMAPStats(ubm);
        List<int[]> indices = ubmStats.add(data, C);
        DiagCovGMM gmm = ubm.copy();
        for (int i = 1; i <= MAP_ITERATIONS; i++) {
            if (!GMMUtils.isGMMParametersFinite(gmm)) {
                LOGGER.error("GMM contains invalid parameters before iteration {}", i);
                throw new RuntimeException();
            }
            GMMMAPStats stats = new GMMMAPStats(gmm);
            stats.add(data, indices);
            LOGGER.info("MAP iteration {}, log likelihood = {}", i, stats.getTotalLogLh());
            gmm.doMAPonMeans(stats, RELEVANCE);
            if (!GMMUtils.isGMMParametersFinite(gmm)) {
                LOGGER.error("GMM contains invalid parameters afer iteration {}", i);
                throw new RuntimeException();
            }
        }
        // XXX adding sqrt of weight doesn't seem to work with SVM
        FloatVector sv = GMMUtils.createSupervector(gmm, ubm);
        if (!FloatMatrixUtils.isAllFinite(sv)) {
            LOGGER.error("GMM supervector contains invalid values");
            throw new RuntimeException();
        }

        // TODO do rank normalization of supervector
        // might have to do it after the fact for background vectors

        return sv;
    }
}
