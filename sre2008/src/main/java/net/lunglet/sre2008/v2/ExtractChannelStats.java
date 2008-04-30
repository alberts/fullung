package net.lunglet.sre2008.v2;

import com.dvsoft.sv.toolbox.gmm.EigenMapTrain;
import com.dvsoft.sv.toolbox.gmm.FrameLvlBgr;
import com.dvsoft.sv.toolbox.gmm.JMapGMM;
import com.dvsoft.sv.toolbox.gmm.SuperVector;
import com.dvsoft.sv.toolbox.matrix.JVectorSequence;
import java.io.File;
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
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFWriter;
import net.lunglet.sre2008.IterableJVectorSequence;
import net.lunglet.sre2008.io.IOUtils;
import net.lunglet.sre2008.util.Converters;
import net.lunglet.util.MainTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

public final class ExtractChannelStats {
    @CommandLineInterface(application = "ExtractChannelStats")
    private static interface Arguments {
        @Option(shortName = "b", description = "basename for output")
        String getBasename();

        @Option(shortName = "f", description = "feature file list")
        File getFileList();

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
            File filelistFile = args.getFileList();
            checkFileExists("feature file list", filelistFile);
            List<File> featureFiles = readFilelist(filelistFile);
            File ubmFile = args.getUbm();
            checkFileExists("UBM", ubmFile);
            File outputFile = args.getOutput();
            checkFileNotExists("output", outputFile);
            JMapGMM ubm = Converters.convert(IOUtils.readDiagCovGMM(ubmFile));
            ExecutorService executorService = createExecutorService();
            try {
                final String outputBasename;
                if (args.isBasename()) {
                    outputBasename = args.getBasename();
                } else {
                    outputBasename = null;
                }
                run(featureFiles, ubm, executorService, outputFile, outputBasename);
            } finally {
                LOGGER.debug("Shutting down executor service");
                executorService.shutdown();
                executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
            }
            return 0;
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

    private static void run(final List<File> featureFiles, final JMapGMM ubm, final ExecutorService executorService,
            final File outputFile, final String outputBasename) {
        final HDFWriter writer = new HDFWriter(outputFile);
        List<Future<Void>> futures = new ArrayList<Future<Void>>();
        final HDFReader reader = new HDFReader(16 * 1024 * 1024);
        int fileCount = 0;
        for (final File featureFile : featureFiles) {
            H5File h5file = new H5File(featureFile);
            Group mfccGroup = h5file.getRootGroup().openGroup("/mfcc");
            int channelCount = 0;
            for (DataSet dataset : mfccGroup.getDataSets()) {
                final int localFileCount = fileCount++;
                final int localChannelCount = channelCount++;
                final String hdfName = dataset.getName();
                final int[] dims = dataset.getIntDims();
                dataset.close();
                Future<Void> future = executorService.submit(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        LOGGER.info("Reading {} from {}", hdfName, featureFile);
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
                        synchronized (writer) {
                            final String outputHdfName;
                            Group root = writer.getH5File().getRootGroup();
                            if (outputBasename != null) {
                                outputHdfName = "/" + outputBasename + localFileCount;
                            } else {
                                String name = featureFile.getName().split("\\.")[0];
                                if (!root.existsGroup("/" + name)) {
                                    root.createGroup("/" + name);
                                }
                                outputHdfName = "/" + name + "/" + localChannelCount;
                            }
                            root.createGroup(outputHdfName);
                            writer.write(outputHdfName + "/x", DenseFactory.floatVector(x.data));
                            writer.write(outputHdfName + "/n", DenseFactory.floatVector(n));
                            writer.flush();
                            LOGGER.info("Wrote to {}/[x,n]", outputHdfName);
                        }
                        return null;
                    }
                });
                futures.add(future);
            }
            h5file.close();
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
        reader.close();
        writer.close();
    }
}
