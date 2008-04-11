package net.lunglet.sre2008.ubm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.gmm.DiagCovGMM;
import net.lunglet.gmm.GMMMAPStats;
import net.lunglet.gmm.GMMUtils;
import net.lunglet.sre2008.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TrainUBM8 {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainUBM8.class);

    private static GMMMAPStats expectationStep(final DiagCovGMM gmm, final DataCache dataCache,
            final ExecutorService executorService) {
        List<Future<GMMMAPStats>> futures = new ArrayList<Future<GMMMAPStats>>();
        for (final FloatDenseMatrix data : dataCache) {
            Future<GMMMAPStats> future = executorService.submit(new Callable<GMMMAPStats>() {
                private FloatDenseMatrix data2 = data;

                @Override
                public GMMMAPStats call() throws Exception {
                    GMMMAPStats stats = new GMMMAPStats(gmm, 0.01);
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

    private static void train(final DataCache dataCache, final ExecutorService executorService) {
        DiagCovGMM gmm;
        if (new File("origubm.h5").exists()) {
            LOGGER.info("Loading GMM to continue training");
            gmm = IOUtils.readDiagCovGMM("origubm.h5");
        } else {
            gmm = GMMUtils.createDiagCovGMM(1, 79);
        }
        while (gmm.getMixtureCount() < 512) {
            int maxiter = gmm.getMixtureCount() > 1 ? 5 : 1;
            trainIterations(gmm, maxiter, dataCache, executorService);
            gmm = GMMUtils.splitAll(gmm);
        }
        trainIterations(gmm, 10, dataCache, executorService);
    }

    private static GMMMAPStats train(final DiagCovGMM gmm, final DataCache dataCache,
            final ExecutorService executorService) {
        GMMMAPStats stats = expectationStep(gmm, dataCache, executorService);
        LOGGER.info("Total log likelihood = {}", stats.getTotalLogLh());
        gmm.doEM(stats);
        return stats;
    }

    private static void trainIterations(final DiagCovGMM gmm, final int maxiter, final DataCache dataCache,
            final ExecutorService executorService) {
        int iter = 1;
        while (iter <= maxiter) {
            LOGGER.info("Training GMM with {} dimensions and {} components", gmm.getDimension(), gmm.getMixtureCount());
            LOGGER.info("Iteration {} of {}", iter, maxiter);
            if (iter == 1) {
                LOGGER.info("Flooring weights to be equal on first iteration");
                gmm.floorWeights(gmm.getMixtureCount());
            }
            LOGGER.info("Weights before: " + gmm.getWeights());
            GMMMAPStats stats = train(gmm, dataCache, executorService);
            IOUtils.writeGMM("ubm_orig_" + gmm.getMixtureCount() + "_" + iter + ".h5", gmm);
            final int weakCount;
            // XXX give it a few iterations after split before we start nuking stuff
            if (iter >= 3) {
                // XXX 200 started rejecting at 256 components
                
                // TODO base criterion on total number of n / mixtures / 
                
                float nthresh = 200.0f * (2.0f * gmm.getDimension());
                weakCount = GMMUtils.countWeak(gmm, stats, nthresh);
                LOGGER.info("Replacing {} weak components", weakCount);
                GMMUtils.replaceWeak(gmm, stats, nthresh);
                LOGGER.info("Weights after: " + gmm.getWeights());
                IOUtils.writeGMM("ubm_fixed_" + gmm.getMixtureCount() + "_" + iter + ".h5", gmm);
            } else {
                weakCount = 0;
            }
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
