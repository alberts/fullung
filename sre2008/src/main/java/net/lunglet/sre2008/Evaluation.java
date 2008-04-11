package net.lunglet.sre2008;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.array4j.matrix.math.FloatMatrixMath;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO rename to Evaluate

public final class Evaluation {
    private static final Logger LOGGER = LoggerFactory.getLogger(Evaluation.class);

    public static void main(final String[] args) throws IOException {
        int dim = 512 * 38 + 1;
//        String evalFile = "C:/home/albert/SRE2008/scripts/sre05-1conv4w_1conv4w.txt";
        String evalFile = "C:/home/albert/SRE2008/scripts/sre06-1conv4w_1conv4w.txt";
        List<Model> models = Evaluation2.readModels(evalFile);
//        String dataFile = "Z:/data/sre05_1conv4w_1conv4w_hlda_gmm2.h5";
//        String gmmFile = "Z:\\data\\lptfc512.niko\\sre05_1s1s_gmmfc.h5";
        String gmmFile = "Z:\\data\\lptfc512.niko\\sre06_1s1s_gmmfc.h5";
        H5File datah5 = new H5File(gmmFile);
        String svmFile = "C:/home/albert/SRE2008/data/svm.h5";
        H5File svmh5 = new H5File(svmFile);
        HDFReader svmReader = new HDFReader(svmh5);
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter("eval.txt"));
        for (Model model : models) {
            FloatDenseVector speakerModel = DenseFactory.floatRowDirect(dim);
            svmReader.read("/" + model.getId(), speakerModel);
            List<String> output = score(model, speakerModel, datah5);
            for (String line : output) {
                outputWriter.write(line);
                outputWriter.write("\n");
            }
        }
        svmReader.close();
        outputWriter.close();
    }

    public static List<String> score(final Model model, final FloatVector speakerModel, final H5File datah5) {
        HDFReader reader = new HDFReader(datah5);
        // exclude offset when calculating length of supervector
        FloatDenseVector trialModel = DenseFactory.floatRowDirect(speakerModel.length() - 1);
        List<String> output = new ArrayList<String>();
        for (Trial trial : model.getTest()) {
            reader.read(trial.getHDFName(), trialModel);

            float[] arr = speakerModel.toArray();
            float rho = arr[arr.length - 1];
            float[] svpart = new float[arr.length - 1];
            System.arraycopy(arr, 0, svpart, 0, svpart.length);
            FloatVector sv = DenseFactory.floatVector(svpart);

            float score = FloatMatrixMath.dot(sv, trialModel) - rho;
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
