package net.lunglet.sre2008;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.array4j.matrix.math.FloatMatrixMath;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.io.HDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExtractPiggyback {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractPiggyback.class);

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

    public static void main(final String[] args) throws IOException {
        int dim = 512 * 38 + 1;
//        String evalFile = "C:/home/albert/SRE2008/scripts/sre05-1conv4w_1conv4w.txt";
        String evalFile = "C:/home/albert/SRE2008/scripts/sre06-1conv4w_1conv4w.txt";
        List<Model> models = Evaluation2.readModels(evalFile);
//        String dataFile = "Z:\\data\\lptfc512.niko\\sre05_1s1s_gmmfc.h5";
        String dataFile = "Z:\\data\\lptfc512.niko\\sre06_1s1s_gmmfc.h5";
        H5File datah5 = new H5File(dataFile);
        String svmFile = "C:/home/albert/SRE2008/data/svm.h5";
        H5File svmh5 = new H5File(svmFile);
        HDFReader svmReader = new HDFReader(svmh5);
        HDFWriter writer = new HDFWriter("Z:/data/piggy06.h5");
        writer.createGroup("/scores");
        List<Float> labels = new ArrayList<Float>();
        int trialCount = 0;
        for (Model model : models) {
            FloatDenseVector speakerModel = DenseFactory.floatRowDirect(dim);
            svmReader.read("/" + model.getId(), speakerModel);
            Result result = extract(model, speakerModel, datah5);
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
    }

    public static Result extract(final Model model, final FloatVector speakerModel, final H5File datah5) {
        HDFReader reader = new HDFReader(datah5);
        // exclude offset when calculating length of supervector
        FloatDenseVector x = DenseFactory.floatRowDirect(speakerModel.length() - 1);
        int nontargetCount = 0;
        List<Float> labels = new ArrayList<Float>();
        List<FloatVector> scores = new ArrayList<FloatVector>();

        int nonTargetLimit = 0;
        for (Trial trial : model.getTest()) {
            if (trial.isTarget()) {
                nonTargetLimit++;
            }
        }
        // XXX limiting to 5 gives about 5k trials on SRE05
        // XXX limiting to 10 gives about 5600 trials on SRE05
//        nonTargetLimit = Math.min(nonTargetLimit, 10);
        nonTargetLimit = Integer.MAX_VALUE;

        for (Trial trial : model.getTest()) {
            // only use as many non-target trials as target trials
            if (!trial.isTarget() && nontargetCount > nonTargetLimit) {
                continue;
            }

            LOGGER.info("Using trial {} of model {} for piggyback", trial, model.getId());

            reader.read(trial.getHDFName(), x);

            float[] arr = speakerModel.toArray();
            float rho = arr[arr.length - 1];
            FloatVector m = DenseFactory.floatVector(Arrays.copyOf(arr, arr.length - 1));
            float score = FloatMatrixMath.dot(m, x) - rho;

            List<Float> scoreParts = new ArrayList<Float>();

            // include system score
            scoreParts.add(score);

            // piggyback scores
            for (int i = 0; i < m.length(); i++) {
                scoreParts.add(m.get(i) * x.get(i));
            }

            // include SVM offset as another piggyback score
            scoreParts.add(-rho);

            scores.add(DenseFactory.floatVector(scoreParts));
            if (trial.isTarget()) {
                labels.add(1.0f);
            } else {
                labels.add(-1.0f);
                nontargetCount++;
            }
        }
        return new Result(labels, scores);
    }
}
