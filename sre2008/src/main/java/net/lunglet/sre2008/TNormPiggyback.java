package net.lunglet.sre2008;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lunglet.array4j.Direction;
import net.lunglet.array4j.Storage;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.array4j.matrix.math.FloatMatrixMath;
import net.lunglet.hdf.DataSet;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TNormPiggyback {
    private static class Result {
        private final List<Float> labels;

        private final List<FloatVector> scores;

        public Result(final List<Float> labels, final List<FloatVector> scores) {
            if (labels.size() != scores.size()) {
                throw new IllegalArgumentException();
            }
            this.labels = labels;
            this.scores = scores;
        }
    }

    private static final class TNormStats {
        private final double mean;

        private final FloatDenseVector scores0;

        private final double stddev;

        private final double variance;

        public TNormStats(final float[] scores) {
            double[] params = Evaluation.getParams(scores);
            this.mean = params[0];
            this.stddev = params[1];
            this.variance = stddev * stddev;
            float[] scores0 = Arrays.copyOf(scores, scores.length);
            for (int i = 0; i < scores0.length; i++) {
                scores0[i] -= mean;
            }
            this.scores0 = DenseFactory.floatVector(scores0, Direction.ROW, Storage.DIRECT);
        }
    }

    private static final int DIMENSION = 512 * 79 + 1;

    private static final String EVAL_FILE = "C:/home/albert/SRE2008/scripts/sre05-1conv4w_1conv4w.txt";

    private static final String TNORM_PARAMS_FILE = "C:\\home\\albert\\SRE2008\\data\\tnormpiggy.h5";

    private static final String EVAL_GMM_FILE = "Z:\\data\\tnorm79\\sre05_eval_gmm.h5";

    private static final String EVAL_SVM_FILE = "Z:\\data\\tnorm79\\sre05_eval_svm.h5";

    private static final Logger LOGGER = LoggerFactory.getLogger(TNormPiggyback.class);

    //    private static final String TNORM_SVM_FILE = "Z:\\data\\tnorm79\\tnorm_svm.h5";
    private static final String TNORM_SVM_FILE = "C:\\home\\albert\\SRE2008\\data\\tnorm_svm.h5";

    public static Result extract(final Model model, final FloatVector speakerModel, final HDFReader gmmReader,
            final FloatDenseMatrix tnormModels, final FloatDenseMatrix utrans, final FloatDenseMatrix onesv,
            final Map<String, TNormStats> tnormCache) {
        int n = tnormModels.rows();
        FloatDenseVector x = DenseFactory.floatColumnDirect(speakerModel.length());
        FloatDenseVector buf = DenseFactory.floatColumnDirect(speakerModel.length() - 1);
        List<Float> labels = new ArrayList<Float>();
        List<FloatVector> piggybackScores = new ArrayList<FloatVector>();

        for (Trial trial : model.getTest()) {
            LOGGER.info("Using trial {} of model {} for piggyback", trial, model.getId());
            gmmReader.read(trial.getHDFName(), buf);
            FloatBuffer xdata = x.data();
            xdata.put(buf.data());
            xdata.put(-1.0f);

            // score the model against the trial
            float modelScore = FloatMatrixMath.dot(speakerModel, x);

            // normalize the score using tnorm
            final TNormStats tnormStats;
            if (tnormCache.containsKey(trial.getHDFName())) {
                LOGGER.debug("Found tnorm parameters for {} in cache", trial.getHDFName());
                tnormStats = tnormCache.get(trial.getHDFName());
            } else {
                float[] tnormScores = FloatMatrixMath.times(tnormModels, x).toArray();
                tnormStats = new TNormStats(tnormScores);
                tnormCache.put(trial.getHDFName(), tnormStats);
            }
            double mean = tnormStats.mean;
            double stddev = tnormStats.stddev;
            double variance = tnormStats.variance;
            double tnormScore = (modelScore - mean) / stddev;
            float alpha = (float) (-1.0 / (n * stddev));
            float beta = (float) (-tnormScore / (n * variance));

            // TODO calculate piggyback scores

            if (trial.isTarget()) {
                labels.add(1.0f);
            } else {
                labels.add(-1.0f);
            }
        }
        return new Result(labels, piggybackScores);
    }

    private static FloatDenseMatrix readParams(final HDFReader reader, final String name) {
        DataSet dataset = reader.getH5File().getRootGroup().openDataSet(name);
        int[] dims = dataset.getIntDims();
        dataset.close();
        if (dims.length == 1) {
            dims = new int[]{1, dims[0]};
        }
        FloatDenseMatrix matrix = DenseFactory.floatRowDirect(dims);
        reader.read(name, matrix);
        return matrix;
    }

    public static void main(final String[] args) throws IOException {
        LOGGER.info("Reading evaluation from {}", EVAL_FILE);
        List<Model> models = new ArrayList<Model>();
        for (Model model : Evaluation2.readModels(EVAL_FILE)) {
            models.add(pruneTrials(model));
        }

        LOGGER.info("Reading TNorm models from {}", TNORM_SVM_FILE);
        FloatDenseMatrix tnormModels = Evaluation.readTNorm(TNORM_SVM_FILE, DIMENSION);

        LOGGER.info("Reading TNorm parameters from {}", TNORM_PARAMS_FILE);
        HDFReader tnormParamsReader = new HDFReader(TNORM_PARAMS_FILE);
        FloatDenseMatrix utrans = readParams(tnormParamsReader, "/Utrans");
        FloatDenseMatrix v = readParams(tnormParamsReader, "/V");
        tnormParamsReader.close();

        System.exit(1);

        HDFReader gmmReader = new HDFReader(EVAL_GMM_FILE);
        HDFReader svmReader = new HDFReader(EVAL_SVM_FILE);
        HDFWriter writer = new HDFWriter("tnormpiggy.h5");
        writer.createGroup("/scores");
        List<Float> labels = new ArrayList<Float>();
        int trialCount = 0;
        Map<String, TNormStats> tnormCache = new HashMap<String, TNormStats>();
        FloatDenseVector speakerModel = DenseFactory.floatRowDirect(DIMENSION);
        for (Model model : models) {
            svmReader.read("/" + model.getId(), speakerModel);
            Result result = extract(model, speakerModel, gmmReader, tnormModels, utrans, v, tnormCache);
            labels.addAll(result.labels);
            for (FloatVector scores : result.scores) {
                writer.write("/scores/" + trialCount, DenseFactory.directCopyOf(scores));
                trialCount++;
            }
        }
        writer.write("/labels", DenseFactory.directCopyOf(DenseFactory.floatVector(labels)));
        svmReader.close();
        writer.close();
        LOGGER.info("Extracted {} trials for piggyback", trialCount);
        svmReader.close();
        gmmReader.close();
    }

    private static Model pruneTrials(final Model model) {
        return model;
    }
}
