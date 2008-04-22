package net.lunglet.sre2008;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.array4j.matrix.math.FloatMatrixMath;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Evaluation3 {
    private static final Logger LOGGER = LoggerFactory.getLogger(Evaluation3.class);

    private static final int SVM_MODEL_DIM = Constants.GMM_DIMENSION + 1;

    public static double[] getTNormParams(final float[] tnormScores) {
        double n = 0.0;
        double mean = 0.0;
        double s = 0.0;
        for (int i = 0; i < tnormScores.length; i++) {
            n += 1.0;
            double x = tnormScores[i];
            double delta = x - mean;
            mean += delta / n;
            // s is updated using the new value of mean
            s += delta * (x - mean);
        }
        double stddev = Math.sqrt(s / n);
        return new double[]{mean, stddev};
    }

    public static void main(final String[] args) throws IOException {
        FloatDenseMatrix tnormModels = readTNorm("tnorm.h5", SVM_MODEL_DIM);
        H5File evalh5 = new H5File("eval05.h5");
        HDFReader svmReader = new HDFReader(evalh5);
        String evalFile = Constants.EVAL_FILE;
        List<Model> models = Evaluation2.readModels(evalFile);
        Map<String, double[]> tnormCache = new HashMap<String, double[]>();
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter("eval.txt"));
        for (Model model : models) {
            FloatDenseVector speakerModel = DenseFactory.floatRowDirect(SVM_MODEL_DIM);
            if (false) {
                svmReader.read("/" + model.getId() + "/svm", speakerModel);
            } else {
                String hdfName = model.getTrain().get(0).getHDFName();
                svmReader.read(hdfName + "/svm", speakerModel);
            }
            List<String> output = score(model, speakerModel, evalh5, tnormModels, tnormCache);
            for (String line : output) {
                outputWriter.write(line);
                outputWriter.write("\n");
            }
        }
        evalh5.close();
        outputWriter.close();
    }

    public static FloatDenseMatrix readTNorm(final String tnormSvmFile, final int dim) {
        H5File tnormh5 = new H5File(tnormSvmFile);
        Set<String> tnormNames = tnormh5.getRootGroup().getDataSetNames();
        int[] dims = {tnormNames.size(), dim};
        FloatDenseMatrix tnormModels = DenseFactory.floatRowDirect(dims);
        HDFReader reader = new HDFReader(tnormh5);
        int i = 0;
        for (String name : tnormNames) {
            reader.read(name, tnormModels.row(i++));
        }
        reader.close();
        return tnormModels;
    }

    public static List<String> score(final Model model, final FloatVector speakerModel, final H5File evalh5,
            final FloatDenseMatrix tnormModels, final Map<String, double[]> tnormCache) {
        // TODO don't construct a reader each time
        HDFReader reader = new HDFReader(evalh5);
        FloatDenseVector x = DenseFactory.floatColumnDirect(speakerModel.length());
        FloatDenseVector buf = DenseFactory.floatColumnDirect(speakerModel.length() - 1);
        List<String> output = new ArrayList<String>();
        for (Trial trial : model.getTest()) {
            // read trial data into buffer and prepare vector for scoring using
            // a single dot product or gemv
            reader.read(trial.getHDFName() + "/gmm", buf);
            FloatBuffer xdata = x.data();
            xdata.put(buf.data());
            xdata.put(-1.0f);

            // score the model against the trial
            float modelScore = FloatMatrixMath.dot(speakerModel, x);

            // normalize the score using tnorm
            final double score;
            if (tnormModels != null) {
                final double[] tnormParams;
                if (tnormCache.containsKey(trial.getHDFName())) {
                    LOGGER.debug("Found tnorm parameters for {} in cache", trial.getHDFName());
                    tnormParams = tnormCache.get(trial.getHDFName());
                } else {
                    float[] tnormScores = FloatMatrixMath.times(tnormModels, x).toArray();
                    tnormParams = getTNormParams(tnormScores);
                    tnormCache.put(trial.getHDFName(), tnormParams);
                }
                double mean = tnormParams[0];
                double stddev = tnormParams[1];
                score = (modelScore - mean) / stddev;
            } else {
                score = modelScore;
            }

            String decision = score >= 0 ? "t" : "f";
            List<String> parts = new ArrayList<String>();
            parts.add(model.getTrainCondition());
            parts.add("n");
            parts.add(model.getTestCondition());
            parts.add(model.getGender().name().substring(0, 1).toLowerCase());
            parts.add(model.getId().toLowerCase());
            parts.add(trial.getName());
            parts.add(trial.getChannel());
            parts.add(decision);
            parts.add(String.format("%.15E", score));
            parts.add(trial.isTarget() ? "targ" : "non");
            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i < parts.size(); i++) {
                strBuilder.append(parts.get(i));
                if (i < parts.size() - 1) {
                    strBuilder.append(" ");
                }
            }
            String str = strBuilder.toString();
            LOGGER.info(str);
            output.add(str);
        }
        return output;
    }
}
