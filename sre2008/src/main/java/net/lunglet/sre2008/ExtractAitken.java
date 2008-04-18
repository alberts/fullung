package net.lunglet.sre2008;

import com.dvsoft.sv.toolbox.gmm.EigenMapTrain;
import com.dvsoft.sv.toolbox.gmm.FrameLvlBgr;
import com.dvsoft.sv.toolbox.gmm.JMapGMM;
import com.dvsoft.sv.toolbox.matrix.JMatrix;
import com.dvsoft.sv.toolbox.matrix.JVector;
import com.dvsoft.sv.toolbox.matrix.JVectorSequence;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.gmm.DiagCovGMM;
import net.lunglet.gmm.GMMUtils;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFWriter;
import net.lunglet.sre2008.io.IOUtils;
import net.lunglet.sre2008.util.Converters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO make number of threads and other stuff configurable using Spring

public class ExtractAitken {
    private static final String CHANNEL_FILE = "Z:/data/tnorm79/channel.h5";

    private static final String EVAL_FILE = "Z:/scripts/sre05-1conv4w_1conv4w.txt";

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractAitken.class);

    private static final String MFCC_FILE = "Z:/data/tnorm79/sre05_1conv4w_1conv4w_mfcc2_79.h5";

    private static final String UBM_FILE = "Z:/data/tnorm79/ubm8_final_79_512.h5";

    private static void checkMFCC(final H5File h5file, List<Model> models, final Set<Trial> trials) {
        Group root = h5file.getRootGroup();
        for (Model model : models) {
            for (Segment segment : model.getTrain()) {
                if (!root.existsDataSet(segment.getHDFName())) {
                    throw new RuntimeException();
                }
            }
        }
        for (Segment segment : trials) {
            if (!root.existsDataSet(segment.getHDFName())) {
                throw new RuntimeException();
            }
        }
    }

    private static Future<?> submitWork(final ExecutorService executorService, final JMapGMM ubm, final JMatrix u,
            final String inputName, final HDFReader reader, final String outputName, final HDFWriter writer) {
        // TODO do a benchmark where each thread has its own copy of ubm and u
        return executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                final JVectorSequence data;
                synchronized (reader) {
                    data = readData(reader, inputName);
                }
                double[][] result = train(ubm, u, data);
                FloatVector n = DenseFactory.floatVector(result[0]);
                FloatVector ex = DenseFactory.floatVector(result[1]);
                synchronized (writer) {
                    write(writer, outputName, n, ex);
                }
                return null;
            }
        });
    }

    public static void main(final String[] args) throws IOException, InterruptedException {
        LOGGER.info("Reading evaluation from {}", EVAL_FILE);
        List<Model> models = new ArrayList<Model>();
        for (Model model : Evaluation2.readModels(EVAL_FILE)) {
            models.add(pruneTrials(model));
        }
        Set<Trial> trials = new HashSet<Trial>();
        int targetCount = 0;
        int nontargetCount = 0;
        for (Model model : models) {
            trials.addAll(model.getTest());
            for (Trial trial : model.getTest()) {
                if (trial.isTarget()) {
                    targetCount++;
                } else {
                    nontargetCount++;
                }
            }
        }

        LOGGER.info("{} model supervectors to extract", models.size());
        LOGGER.info("{} trial supervectors to extract", trials.size());
        LOGGER.info("Extracted {} target trials and {} nontarget trials", targetCount, nontargetCount);

        LOGGER.info("Reading UBM");
        JMapGMM ubm = readUBM();
        LOGGER.info("Reading channel compensation matrix");
        JMatrix u = readUMatrix();
        LOGGER.info("Opening and checking feature data");
        HDFReader mfccReader = new HDFReader(MFCC_FILE);
        checkMFCC(mfccReader.getH5File(), models, trials);

        BufferedWriter evalWriter = new BufferedWriter(new FileWriter("aitken.txt"));
        for (Model model : models) {
            for (Trial trial : model.getTest()) {
                evalWriter.write(model.getId());
                evalWriter.write(" ");
                evalWriter.write(trial.getName());
                evalWriter.write(" ");
                evalWriter.write(trial.getChannel());
                evalWriter.write(" ");
                evalWriter.write(trial.isTarget() ? "target" : "nontarget");
                evalWriter.write("\n");
            }
        }
        evalWriter.close();

        HDFWriter writer = new HDFWriter("aitken.h5");
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<?>> futures = new ArrayList<Future<?>>();
        for (Model model : models) {
            Segment segment = model.getTrain().get(0);
            String inputName = segment.getHDFName();
            String outputName = "/" + model.getId();
            futures.add(submitWork(executorService, ubm, u, inputName, mfccReader, outputName, writer));
        }
        for (Trial trial : trials) {
            String inputName = trial.getHDFName();
            String outputName = "/" + trial.getName() + "/" + trial.getChannel();
            futures.add(submitWork(executorService, ubm, u, inputName, mfccReader, outputName, writer));
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                LOGGER.error("Execution failed", e);
            }
        }
        mfccReader.close();
        writer.close();

        LOGGER.info("Extraction complete. Shutting down.");
        executorService.shutdown();
        executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
    }

    private static Model pruneTrials(final Model model) {
        // XXX do everything for now and just filter the trial list
        return model;
    }

    private static JVectorSequence readData(final HDFReader reader, final String name) {
        DataSet dataset = reader.getH5File().getRootGroup().openDataSet(name);
        int[] dims = dataset.getIntDims();
        dataset.close();
        FloatDenseMatrix data = DenseFactory.floatRowHeap(dims[0], dims[1]);
        LOGGER.info("Reading data from {} {}", name, Arrays.toString(dims));
        reader.read(name, data);
        return new IterableJVectorSequence(data.rowsIterator(), true);
    }

    private static JMapGMM readUBM() {
        DiagCovGMM ubm = IOUtils.readDiagCovGMM(UBM_FILE);
        if (!GMMUtils.isGMMParametersFinite(ubm)) {
            LOGGER.error("GMM contains invalid parameters");
            throw new RuntimeException();
        }
        return Converters.convert(ubm);
    }

    private static JMatrix readUMatrix() {
        HDFReader reader = new HDFReader(CHANNEL_FILE);
        int dim = 512 * 79;
        int k = 40;
        FloatDenseMatrix channelSpace = DenseFactory.floatRowDirect(new int[]{dim, k});
        reader.read("/U", channelSpace);
        reader.close();
        return new JMatrix(channelSpace.toRowArrays());
    }

    private static double[][] train(final JMapGMM ubm, final JMatrix u, final JVectorSequence data) {
        LOGGER.info("Evaluating frame level background on UBM");
        FrameLvlBgr bgr = ubm.evalFrameLvlBgr(data);
        LOGGER.info("Channel adapting sufficient statistics");
        EigenMapTrain train = new EigenMapTrain(u, ubm);
        train.setData(data, bgr);
        JVector ex = train.sufficientStatsChannelAdapt();
        // get n after doing channel adaptation
        double[] n = train.getN();
        return new double[][]{n, ex.transpose().toDoubleArray()[0]};
    }

    private static void write(final HDFWriter writer, final String name, final FloatVector n, final FloatVector ex) {
        H5File h5file = writer.getH5File();
        String[] parts = name.split("/");
        for (int i = 1; i < parts.length; i++) {
            StringBuilder pathBuilder = new StringBuilder();
            pathBuilder.append("/");
            for (int j = 1; j <= i; j++) {
                pathBuilder.append(parts[j]);
                if (j < i) {
                    pathBuilder.append("/");
                }
            }
            String path = pathBuilder.toString();
            if (!h5file.getRootGroup().existsGroup(path)) {
                LOGGER.debug("Creating group {}", path);
                h5file.getRootGroup().createGroup(path);
            }
        }
        String countsName = name + "/n";
        LOGGER.info("Writing counts to {}", countsName);
        writer.write(countsName, DenseFactory.directCopyOf(n));
        String statsName = name + "/ex";
        LOGGER.info("Writing sufficient statistics to {}", statsName);
        writer.write(statsName, DenseFactory.directCopyOf(ex));
    }
}
