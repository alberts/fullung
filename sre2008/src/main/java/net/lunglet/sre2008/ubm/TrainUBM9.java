package net.lunglet.sre2008.ubm;

import java.io.File;
import java.io.IOException;
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

public final class TrainUBM9 {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainUBM9.class);

    private static GMMMAPStats expectationStep(final DiagCovGMM gmm, final DataCache2 dataCache,
            final ExecutorService executorService) {
        List<Future<Void>> futures = new ArrayList<Future<Void>>();
        final GMMMAPStats globalStats = new GMMMAPStats(gmm);
        for (final FloatDenseMatrix data : dataCache) {
            Future<Void> future = executorService.submit(new Callable<Void>() {
                private FloatDenseMatrix data2 = data;

                @Override
                public Void call() throws Exception {
                    GMMMAPStats stats = new GMMMAPStats(gmm, 0.01);
                    stats.add(data2.rowsIterator());
                    // prevent future from referring to this data
                    data2 = null;
                    synchronized (globalStats) {
                        globalStats.add(stats);
                    }
                    return null;
                }
            });
            futures.add(future);
        }
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        futures.clear();
        return globalStats;
    }

    public static void main(final String[] args) throws InterruptedException, IOException {
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
        DataCache2 dataCache = new DataCache2(args[0]);
        try {
            train(dataCache, executorService);
        } finally {
            dataCache.close();
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
    }

    private static void train(final DataCache2 dataCache, final ExecutorService executorService) {
        int maxMixtures = 512;
        int dim = 38;
        DiagCovGMM gmm;
        if (new File("origubm.h5").exists()) {
            LOGGER.info("Loading GMM to continue training");
            gmm = IOUtils.readDiagCovGMM("origubm.h5");
        } else {
            gmm = GMMUtils.createDiagCovGMM(1, dim);
        }
        while (true) {
            final int maxiter;
            if (gmm.getMixtureCount() == 1) {
                maxiter = 1;
            } else if (gmm.getMixtureCount() < 256) {
                maxiter = 5;
            } else if (gmm.getMixtureCount() < 512) {
                maxiter = 10;
            } else {
                maxiter = 3;
            }
            trainIterations(gmm, maxiter, dataCache, executorService);
            if (gmm.getMixtureCount() == maxMixtures) {
                break;
            }
            gmm = GMMUtils.splitAll(gmm);
        }
    }

    private static GMMMAPStats train(final DiagCovGMM gmm, final DataCache2 dataCache,
            final ExecutorService executorService) {
        GMMMAPStats stats = expectationStep(gmm, dataCache, executorService);
        LOGGER.info("Total log likelihood = {}", stats.getTotalLogLh());
        gmm.doEM(stats);
        return stats;
    }

    private static void trainIterations(final DiagCovGMM gmm, final int maxiter, final DataCache2 dataCache,
            final ExecutorService executorService) {
        int iter = 1;
        while (iter <= maxiter) {
            LOGGER.info("Training GMM with {} dimensions and {} components", gmm.getDimension(), gmm.getMixtureCount());
            LOGGER.info("Iteration {} of {}", iter, maxiter);
            if (iter == 1) {
                LOGGER.info("Flooring weights to be equal on first iteration");
                gmm.floorWeights(gmm.getMixtureCount());
            }
            LOGGER.info("Weights before training: " + gmm.getWeights());
            GMMMAPStats stats = train(gmm, dataCache, executorService);
            IOUtils.writeGMM("ubm_orig_" + gmm.getMixtureCount() + "_" + iter + ".h5", gmm);
            LOGGER.info("Weights after training: " + gmm.getWeights());

            // require 100 feature vector per mixture parameter
            float nthresh = 100.0f * (2.0f * gmm.getDimension() + 1);
            final int weakCount = GMMUtils.countWeak(gmm, stats, nthresh);

            if (false) {
                if (iter >= 3 && weakCount > 0) {
                    LOGGER.info("Replacing {} weak components", weakCount);
                    GMMUtils.replaceWeak(gmm, stats, nthresh);
                    IOUtils.writeGMM("ubm_fixed_" + gmm.getMixtureCount() + "_" + iter + ".h5", gmm);
                    LOGGER.info("Found some weak components, restarting with equal weights");
                    iter = 1;
                } else {
                    iter++;
                }
            } else {
                // ignore weak count for microphone ubm
                LOGGER.info("Weak count = {}", weakCount);
                iter++;
            }
        }
        IOUtils.writeGMM("ubm_final_" + gmm.getMixtureCount() + ".h5", gmm);
    }
}
