package net.lunglet.sre2008.v2;

import com.dvsoft.sv.toolbox.gmm.EigenMapTrain;
import com.dvsoft.sv.toolbox.gmm.FrameLvlBgr;
import com.dvsoft.sv.toolbox.gmm.JMapGMM;
import com.dvsoft.sv.toolbox.gmm.SuperVector;
import com.dvsoft.sv.toolbox.matrix.JVectorSequence;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFUtils;
import net.lunglet.io.HDFWriter;
import net.lunglet.sre2008.IterableJVectorSequence;
import net.lunglet.sre2008.io.IOUtils;
import net.lunglet.sre2008.util.Converters;
import net.lunglet.util.MainTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

// TODO support parameter that does initial runtime checks and then exits

// TODO investigate jopt simple

public final class ExtractChannelStats {
    @CommandLineInterface(application = "ExtractChannelStats")
    private static interface Arguments {
        @Option(shortName = "b", description = "basename for output")
        String getBasename();

        @Option(shortName = "f", description = "feature file list")
        File getFilelist();

        @Option(shortName = "o", description = "output")
        File getOutput();

        @Option(shortName = "u", description = "UBM file")
        File getUbm();

        boolean isBasename();
    }

    public static final class Main extends MainTemplate<Arguments> {
        public Main() {
            super(Arguments.class);
        }

        @Override
        protected int mainImpl(final Arguments args) throws Throwable {
            File outputFile = args.getOutput();
            checkFileNotExists("output", outputFile);
            File filelist = args.getFilelist();
            checkFileExists("feature file list", filelist);
            List<StatsJob> statsJobs = readChannelFilelist(filelist);
            File ubmFile = args.getUbm();
            checkFileExists("UBM", ubmFile);
            JMapGMM ubm = Converters.convert(IOUtils.readDiagCovGMM(ubmFile));
            ExecutorService executorService = createExecutorService();
            try {
                final String outputBasename;
                if (args.isBasename()) {
                    outputBasename = args.getBasename();
                } else {
                    outputBasename = null;
                }
                LOGGER.info("Extracting stats using top N={}={} scoring", JMapGMM.c, EigenMapTrain.c5);
                run(statsJobs, ubm, executorService, outputFile, outputBasename);
            } finally {
                LOGGER.debug("Shutting down executor service");
                executorService.shutdown();
                executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
            }
            return 0;
        }
    }

    private static class StatsJob {
        private final int channel;

        private final File featureFile;

        public StatsJob(final File featureFile, final int channel) {
            this.featureFile = featureFile;
            this.channel = channel;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractChannelStats.class);

    private static ExecutorService createExecutorService() {
        final int threads = 4;
        final int capacity = 10 * threads;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(capacity);
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        return new ThreadPoolExecutor(threads, threads, 0L, TimeUnit.MILLISECONDS, workQueue, handler);
    }

    public static void main(final String[] args) throws Throwable {
        new Main().main(args);
    }

    private static List<StatsJob> readChannelFilelist(final File featureListFile) throws IOException {
        List<StatsJob> statsJobs = new ArrayList<StatsJob>();
        LOGGER.info("Reading file list from {}", featureListFile);
        BufferedReader lineReader = new BufferedReader(new FileReader(featureListFile));
        try {
            String line = lineReader.readLine();
            while (line != null) {
                line = line.trim();
                File featureFile = new File(line.substring(0, line.length() - 2));
                if (!featureFile.isFile()) {
                    throw new RuntimeException("Invalid feature file " + featureFile);
                }
                int channel = line.charAt(line.length() - 1) - 'a';
                if (channel != 0 && channel != 1) {
                    throw new IOException();
                }
                statsJobs.add(new StatsJob(featureFile, channel));
                line = lineReader.readLine();
            }
            return statsJobs;
        } finally {
            lineReader.close();
        }
    }

    private static void run(final List<StatsJob> statsJobs, final JMapGMM ubm, final ExecutorService executorService,
            final File outputFile, final String outputBasename) {
        final HDFWriter writer = new HDFWriter(outputFile);
        final HDFReader reader = new HDFReader(16 * 1024 * 1024);
        List<Future<Void>> futures = new ArrayList<Future<Void>>();
        int jobCount = 0;
        for (final StatsJob statsJob : statsJobs) {
            final File featureFile = statsJob.featureFile;
            final int channel = statsJob.channel;
            final String hdfName = "/mfcc/" + channel;
            LOGGER.info("Reading {} from {}", hdfName, featureFile);
            H5File h5file = new H5File(featureFile);
            DataSet dataset = h5file.getRootGroup().openDataSet(hdfName);
            final int[] dims = dataset.getIntDims();
            dataset.close();
            final int localJobCount = jobCount++;
            Callable<Void> callable = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    H5File h5file2 = new H5File(featureFile);
                    FloatDenseMatrix data = DenseFactory.floatRowHeap(dims[0], dims[1]);
                    synchronized (reader) {
                        reader.read(h5file2, hdfName, data);
                    }
                    h5file2.close();
                    EigenMapTrain train = new EigenMapTrain(null, ubm);
                    JVectorSequence data2 = new IterableJVectorSequence(data.rowsIterator(), true);
                    LOGGER.debug("Evaluating frame level background");
                    FrameLvlBgr bgr = ubm.evalFrameLvlBgr(data2);
                    train.setData(data2, bgr);
                    train.calcPosteriors();
                    SuperVector x = train.dataExpectation2();
                    double[] n = train.getN();
                    final String outputName;
                    if (outputBasename != null) {
                        outputName = "/" + outputBasename + localJobCount;
                    } else {
                        outputName = "/" + featureFile.getName().split("\\.")[0] + "/" + channel;
                    }
                    synchronized (writer) {
                        HDFUtils.createGroup(writer.getH5File(), outputName).close();
                        // values in x are grouped together by feature
                        // dimension, not by GMM mixture component.
                        writer.write(outputName + "/x", DenseFactory.floatVector(x.data));
                        writer.write(outputName + "/n", DenseFactory.floatVector(n));
                        writer.flush();
                    }
                    LOGGER.info("Wrote to {}/[x,n]", outputName);
                    return null;
                }
            };
            // TODO provide a way to turn on debugging, which should bypass
            // ExecutorServices and execute jobs in the main thread. should also
            // allow overwriting of output files. could also introduce
            // convenient breaks into long loops.
            if (false) {
                try {
                    callable.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                Future<Void> future = executorService.submit(callable);
                futures.add(future);
            }
            h5file.close();
        }
        // TODO if it takes long to submit jobs, failures won't be seen until
        // much later. instead, check existing futures while submitting jobs.
        // probably a service that MainTemplate could provide.
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
        reader.close();
        writer.close();
    }
}
