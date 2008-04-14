package net.lunglet.sre2008;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.array4j.matrix.math.FloatMatrixMath;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Evaluation {
    private static final Logger LOGGER = LoggerFactory.getLogger(Evaluation.class);

    public static void main(final String[] args) throws IOException {
        int dim = Constants.GMM_DIMENSION + 1;
        String evalFile = Constants.EVAL_FILE;
        List<Model> models = Evaluation2.readModels(evalFile);
        H5File datah5 = new H5File(Constants.EVAL_GMM);
        H5File svmh5 = new H5File(Constants.EVAL_SVM);
        HDFReader svmReader = new HDFReader(svmh5);

        // TODO read tnorm models from file
        FloatDenseMatrix tnorm = null;

        BufferedWriter outputWriter = new BufferedWriter(new FileWriter("eval.txt"));
        for (Model model : models) {
            FloatDenseVector speakerModel = DenseFactory.floatRowDirect(dim);
            svmReader.read("/" + model.getId(), speakerModel);
            List<String> output = score(model, speakerModel, datah5, tnorm);
            for (String line : output) {
                outputWriter.write(line);
                outputWriter.write("\n");
            }
        }
        svmReader.close();
        outputWriter.close();
    }

    public static List<String> score(final Model model, final FloatVector speakerModel, final H5File datah5,
            final FloatDenseMatrix tnorm) {
        HDFReader reader = new HDFReader(datah5);
        // exclude offset when calculating length of supervector
        FloatDenseVector x = DenseFactory.floatRowDirect(speakerModel.length() - 1);
        List<String> output = new ArrayList<String>();
        for (Trial trial : model.getTest()) {
            reader.read(trial.getHDFName(), x);

            float[] arr = speakerModel.toArray();
            float rho = arr[arr.length - 1];
            float[] svpart = new float[arr.length - 1];
            System.arraycopy(arr, 0, svpart, 0, svpart.length);
            FloatVector sv = DenseFactory.floatVector(svpart);

            float score = FloatMatrixMath.dot(sv, x) - rho;

            if (tnorm != null) {
                // TODO score x against tnorm models using a gemv
                // estimate mean
                // estimate variance
                // subtract mean from score and divide by variance
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
