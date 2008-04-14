package net.lunglet.sre2008.hlda;

import com.dvsoft.sv.toolbox.gmm.FastBayes;
import com.dvsoft.sv.toolbox.gmm.FrameLvlBgr;
import com.dvsoft.sv.toolbox.gmm.JMapGMM;
import com.dvsoft.sv.toolbox.matrix.JMatrix;
import com.dvsoft.sv.toolbox.matrix.JVector;
import com.dvsoft.sv.toolbox.matrix.JVectorSequence;
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
import net.lunglet.sre2008.IterableJVectorSequence;
import net.lunglet.sre2008.io.IOUtils;
import net.lunglet.sre2008.ubm.DataCache;
import net.lunglet.sre2008.util.Converters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HLDA {
    private static final Logger LOGGER = LoggerFactory.getLogger(HLDA.class);

    private static void accStats(final JMapGMM gmm, JVector frame, JMapStats2 ws, int[] indices) {
        int k = indices.length;
        FastBayes fbayes = new FastBayes(k);
        double[] cl = new double[k];
        for (int i = 0; i < k; i++) {
            int m = indices[i];
            cl[i] = gmm.getConditionalLL(frame, m);
        }
        ws.totLL += fbayes.calculate(gmm.logWeights, indices, cl);
        ws.add(indices, fbayes.getPosteriorProbs(), frame);
    }

    private static List<JMatrix> calculateCovs(final JMapGMM gmm, final JMapStats2 stats) {
        JVector globalMean = new JVector(gmm.getDimension());
        // this corresponds to \overbar{T} in eq. 45 in Kumar/SC
        JMatrix globalCov = new JMatrix(gmm.getDimension(), gmm.getDimension());
        // this corresponds to \overbar{W} in eq. 44 in Kumar/SC
        JMatrix globalCov2 = new JMatrix(gmm.getDimension(), gmm.getDimension());
        List<JMatrix> covs = new ArrayList<JMatrix>(gmm.getNoMixtures());
        for (int m = 0; m < gmm.getNoMixtures(); m++) {
            globalMean.add(1.0, stats.ex[m]);
            globalCov.add(1.0, stats.exx[m]);

            // prepare class covariance matrix
            JMatrix cov = new JMatrix(stats.exx[m]);
            double s = 1.0 / stats.n[m];
            // this does the 1/Nj scaling for the first term required to
            // calculate class covariance matrix
            cov.scal(s);
            // calculate class mean
            stats.ex[m].scal(s);
            // subtract class mean product term contribution
            cov.add(-1.0, JMatrix.multiply(stats.ex[m], stats.ex[m].transpose()));
            covs.add(cov);

            globalCov2.add(stats.n[m] / stats.totN, cov);
        }
        globalMean.scal(1.0 / stats.totN);
        globalCov.scal(1.0 / stats.totN);
        globalCov.add(-1.0, JMatrix.multiply(globalMean, globalMean.transpose()));
        covs.add(0, globalCov);
        return covs;
    }

    private static JMapStats2 calculateStats(final JMapGMM gmm, final JVectorSequence data, final FrameLvlBgr bgr) {
        data.reset();
        int i = 0;
        JMapStats2 stats = new JMapStats2(gmm.getNoMixtures(), gmm.getDimension());
        for (;;) {
            JVector frame = data.getNextVector();
            if (frame == null) {
                break;
            }
            accStats(gmm, frame, stats, bgr.getMixtureVector(i));
            i++;
        }
        return stats;
    }

    public static void main(final String[] args) throws InterruptedException {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOGGER.error("Uncaught exception", e);
                System.exit(1);
            }
        });
        final int threads = 8;
        final int queueCapacity = 15 * threads;
        ExecutorService executorService = new ThreadPoolExecutor(threads, threads, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(queueCapacity), new ThreadPoolExecutor.CallerRunsPolicy());

        LOGGER.info("Loading original UBM");
        JMapGMM ubm = Converters.convert(IOUtils.readDiagCovGMM("ubm8_final_79_512.h5"));
        LOGGER.info("Creating data cache");
        DataCache dataCache = new DataCache("Z:/data/background_mfcc2.h5");
        try {
            JMapStats2 stats = train(ubm, dataCache, executorService);
            double[] gamma = stats.n;
            List<JMatrix> covs = calculateCovs(ubm, stats);
            JMatrix globalCov = covs.remove(0);
            IOUtils.writeHLDA("hlda.h5", gamma, globalCov, covs);
        } finally {
            dataCache.close();
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
    }

    private static JMapStats2 train(final JMapGMM ubm, final DataCache dataCache, final ExecutorService executorService) {
        List<Future<Void>> futures = new ArrayList<Future<Void>>();
        final JMapStats2 globalStats = new JMapStats2(ubm.getNoMixtures(), ubm.getDimension());
        for (final FloatDenseMatrix data : dataCache) {
            Future<Void> future = executorService.submit(new Callable<Void>() {
                private FloatDenseMatrix data2 = data;

                @Override
                public Void call() throws Exception {
                    JVectorSequence dataSeq = new IterableJVectorSequence(data2.rowsIterator(), false);
                    // prevent future from referring to this data
                    data2 = null;
                    // this frame level background will cause only the top few
                    // components to be used for each vector when estimating the
                    // within-class covariance matrices
                    FrameLvlBgr bgr = ubm.evalFrameLvlBgr(dataSeq);
                    JMapStats2 stats = calculateStats(ubm, dataSeq, bgr);
                    // update global stats here because keep all the stats until
                    // the end uses too much memory
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

    private final JMapGMM gmm;

    public JMapStats2 ws;

    public HLDA(final JMapGMM gmm) {
        this.gmm = gmm;
    }

    public List<JMatrix> calculateCovs(JVectorSequence data, FrameLvlBgr bgr) {
        ws = calculateStats(gmm, data, bgr);
        return calculateCovs(gmm, ws);
    }
}
