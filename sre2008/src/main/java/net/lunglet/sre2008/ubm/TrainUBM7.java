package net.lunglet.sre2008.ubm;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.lunglet.array4j.Order;
import net.lunglet.array4j.Storage;
import net.lunglet.array4j.matrix.FloatMatrix;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.array4j.matrix.math.MatrixMath;
import net.lunglet.gmm.DiagCovGMM;
import net.lunglet.gmm.GMMMAPStats;
import net.lunglet.gmm.GMMUtils;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.sre2008.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TrainUBM7 {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainUBM7.class);

    public static void checkMatrix(final FloatMatrix expected, final FloatMatrix actual, final float eps,
            final boolean relative) {
        assertEquals(expected.rows(), actual.rows());
        assertEquals(expected.columns(), actual.columns());
        for (int i = 0; i < actual.rows(); i++) {
            for (int j = 0; j < actual.columns(); j++) {
                float expectedij = expected.get(i, j);
                float epsij = relative ? expectedij * eps : eps;
                assertEquals(expectedij, actual.get(i, j), epsij);
            }
        }
    }

    private static GMMMAPStats expectationStep(final DiagCovGMM gmm, final DataCache dataCache,
            final ExecutorService executorService) {
        List<Future<GMMMAPStats>> futures = new ArrayList<Future<GMMMAPStats>>();
        for (final FloatDenseMatrix data : dataCache) {
            Future<GMMMAPStats> future = executorService.submit(new Callable<GMMMAPStats>() {
                private FloatDenseMatrix data2 = data;

                @Override
                public GMMMAPStats call() throws Exception {
                    GMMMAPStats stats = new GMMMAPStats(gmm, 0.01);
                    LOGGER.debug("Collecting stats for data");
                    stats.add(data2.rowsIterator());
                    // prevent future from referring to this data
                    data2 = null;
                    return stats;
                }
            });
            futures.add(future);
        }
        GMMMAPStats globalStats = new GMMMAPStats(gmm);
        for (Future<GMMMAPStats> future : futures) {
            final GMMMAPStats stats;
            try {
                stats = future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            globalStats.add(stats);
        }
        futures.clear();
        return globalStats;
    }

    public static void main(final String[] args) throws InterruptedException {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOGGER.error("Uncaught exception", e);
                System.exit(1);
            }
        });
        // XXX do we want more threads than cores?
        final int threads = 8;
        final int queueCapacity = 15 * threads;
        ExecutorService executorService = new ThreadPoolExecutor(threads, threads, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(queueCapacity), new ThreadPoolExecutor.CallerRunsPolicy());
        LOGGER.info("Opening data cache for {}", args[0]);
        DataCache dataCache = new DataCache(args[0]);
        try {
            train(dataCache, executorService);
        } finally {
            dataCache.close();
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
    }

    private static void testHDFReader(final H5File dataFile, final String name) {
        DataSet dataset = dataFile.getRootGroup().openDataSet(name);
        DataSpace space = dataset.getSpace();
        int[] dims = space.getIntDims();
        LOGGER.info("Reading {}, dims={}", name, Arrays.toString(dims));
        space.close();
        dataset.close();
        HDFReader reader = new HDFReader(dataFile);
        LOGGER.info("Reading using direct matrix");
        FloatDenseMatrix data1 = DenseFactory.floatMatrix(dims, Order.ROW, Storage.DIRECT);
        reader.read(name, data1);
        LOGGER.info("Reading using heap matrix");
        FloatDenseMatrix data2 = DenseFactory.floatMatrix(dims, Order.ROW, Storage.HEAP);
        reader.read(name, data2);
        LOGGER.info("Comparing matrices");
        checkMatrix(data1, data2, 1.0e-9f, false);
    }

    private static void train(final DataCache dataCache, final ExecutorService executorService) {
        LOGGER.info("Training one component GMM");
        DiagCovGMM gmmVarFloor = GMMUtils.createDiagCovGMM(1, 79);
        train(gmmVarFloor, dataCache, executorService);
        IOUtils.writeGMM("ubm_varfloor.h5", gmmVarFloor);

        LOGGER.info("Setting variance floor to 50% of global variance");
        final FloatVector varFloor = gmmVarFloor.getVariance(0);
        MatrixMath.timesEquals(varFloor, 0.5f);
        LOGGER.info("Variance floor: {}", varFloor);

        DiagCovGMM gmm = null;
        if (new File("origubm.h5").exists()) {
            LOGGER.info("Loading GMM to continue training");
            gmm = IOUtils.readDiagCovGMM("origubm.h5");
            // train here first so that we don't immediately split the original
            trainIterations(gmm, varFloor, 3, dataCache, executorService);
        } else {
            gmm = gmmVarFloor;
        }

        while (gmm.getMixtureCount() < 512) {
            gmm = GMMUtils.splitAll(gmm);
            trainIterations(gmm, varFloor, 3, dataCache, executorService);
        }
        for (int i = 0; i < 5; i++) {
            gmm = GMMUtils.keepHeaviest(gmm, 512 - 128);
            while (gmm.getMixtureCount() < 512) {
                gmm = GMMUtils.splitHeaviest(gmm);
            }
            trainIterations(gmm, varFloor, 3, dataCache, executorService);
        }
        trainIterations(gmm, varFloor, 5, dataCache, executorService);
    }

    private static GMMMAPStats train(final DiagCovGMM gmm, final DataCache dataCache,
            final ExecutorService executorService) {
        GMMMAPStats stats = expectationStep(gmm, dataCache, executorService);
        LOGGER.info("Total log likelihood = {}", stats.getTotalLogLh());
        gmm.doEM(stats);
        return stats;
    }

    private static void trainIterations(final DiagCovGMM gmm, final FloatVector varFloor, final int maxiter,
            final DataCache dataCache, final ExecutorService executorService) {
        int iter = 1;
        while (iter <= maxiter) {
            LOGGER.info("Training GMM with " + gmm.getMixtureCount() + " components");
            LOGGER.info("Iteration " + iter + " of " + maxiter);
            if (iter == 1) {
                LOGGER.info("Flooring weights to be equal on first iteration");
                gmm.floorWeights(gmm.getMixtureCount());
            }

            LOGGER.info("Weights before: " + gmm.getWeights());

            GMMMAPStats stats = train(gmm, dataCache, executorService);
            IOUtils.writeGMM("ubm_weak_" + gmm.getMixtureCount() + "_" + iter + ".h5", gmm);

            // require at least 50 feature vectors to estimate each parameter
            float nthresh = 50.0f * (2.0f * gmm.getDimension());
            int weakCount = GMMUtils.countWeak(gmm, stats, nthresh);
            LOGGER.info("Replacing {} weak components", weakCount);
            GMMUtils.replaceWeak(gmm, stats, nthresh);

            LOGGER.info("Weights after: " + gmm.getWeights());
            IOUtils.writeGMM("ubm_fixed_" + gmm.getMixtureCount() + "_" + iter + ".h5", gmm);

            LOGGER.info("Flooring variances");
            gmm.floorVariances(varFloor);
            IOUtils.writeGMM("ubm_floored_" + gmm.getMixtureCount() + "_" + iter + ".h5", gmm);

            if (weakCount > 0) {
                LOGGER.info("Found some weak components, restarting with equal weights");
                iter = 1;
            } else {
                iter++;
            }
        }
        IOUtils.writeGMM("ubm_final_" + gmm.getMixtureCount() + ".h5", gmm);
    }
}
