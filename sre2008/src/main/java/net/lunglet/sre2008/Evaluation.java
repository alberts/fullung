package net.lunglet.sre2008;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.lunglet.array4j.Storage;
import net.lunglet.array4j.matrix.FloatMatrix;
import net.lunglet.array4j.matrix.FloatVector;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.array4j.matrix.math.FloatMatrixMath;
import net.lunglet.array4j.matrix.packed.FloatPackedMatrix;
import net.lunglet.array4j.matrix.packed.PackedFactory;
import net.lunglet.gmm.DiagCovGMM;
import net.lunglet.gmm.GMMUtils;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.H5Exception;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import net.lunglet.sre2008.svm.SpeakerKernelMatrix;
import net.lunglet.svm.Handle;
import net.lunglet.svm.SvmClassifier;
import net.lunglet.util.AssertUtils;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Evaluation {
    private static final String DATA_FILE = "Z:\\data\\sre05mfcc_1s1s.h5";

    private static final String EVAL_FILE = "sre05-1conv4w_1conv4w.txt";

    private static final Logger LOGGER = LoggerFactory.getLogger(Evaluation.class);

    private static final String SVM_DATA_FILE = "sre04gmm_1s1s.h5";

    private static final String SVM_KERNEL_FILE = "sre04gmm_1s1s_kernel.h5";

    private static final String UBM_FILE = "ubm_floored_512_3.h5";

    private static void checkData(final List<Model> models) {
        H5File h5file = new H5File(DATA_FILE);
        LOGGER.info("Checking if data file is complete");
        for (Model model : models) {
            String name = null;
            try {
                for (Segment segment : model.getTrain()) {
                    name = segment.getHDFName();
                    DataSet dataset = h5file.getRootGroup().openDataSet(name);
                    dataset.close();
                }
                for (Segment segment : model.getTest()) {
                    name = segment.getHDFName();
                    DataSet dataset = h5file.getRootGroup().openDataSet(name);
                    dataset.close();
                }
            } catch (H5Exception e) {
                LOGGER.error("Dataset for " + name + " doesn't exist", e);
                throw e;
            }
        }
        h5file.close();
    }

    private static final class HDFHandle implements Handle {
        private final H5File h5file;

        private final String name;

        private final int index;

        private final int label;

        public HDFHandle(final H5File h5file, final String name, final int index, final int label) {
            this.h5file = h5file;
            this.name = name;
            this.index = index;
            this.label = label;
        }

        @Override
        public FloatVector getData() {
            throw new NotImplementedException();
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public int getLabel() {
            return label;
        }
    }

    public static void score(final Model model, final FloatVector speakerModel, final H5File datah5) {
        HDFReader reader = new HDFReader(null);
        for (Trial trial : model.getTest()) {
            FloatDenseVector trialModel = null;
            reader.read(trial.getHDFName(), trialModel);
            float score = FloatMatrixMath.dot(speakerModel, trialModel);
            String decision = score > 0 ? "t" : "f";

            List<String> parts = new ArrayList<String>();
            parts.add(model.getTrainCondition());
            parts.add("n");
            parts.add(model.getTestCondition());
            parts.add(model.getGender().name().substring(0, 1).toLowerCase());
            parts.add(model.getId().toLowerCase());
            parts.add(trial.getName());
            parts.add(trial.getChannel());
            parts.add(decision);
            parts.add(Float.toString(score));
            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i < parts.size(); i++) {
                strBuilder.append(parts.get(i));
                if (i < parts.size() - 1) {
                    strBuilder.append(" ");
                }
            }
            System.out.println(strBuilder.toString());
        }
    }

    private static void doEvaluation(final Model model, final H5File datah5, final H5File svmh5,
            final FloatMatrix background) {
        LOGGER.info("Performing evaluation for model {}", model.getId());

        DiagCovGMM ubm = null;

        List<Handle> data = new ArrayList<Handle>();
        // background data
        int index = 0;
        for (DataSet dataset : svmh5.getRootGroup().getDataSets()) {
            String name = dataset.getName();
            dataset.close();
            data.add(new HDFHandle(svmh5, name, index++, -1));
        }

        // speaker data
        FloatMatrix speaker = null;
        int trainIndex = 0;
        for (Segment segment : model.getTrain()) {
            String name = segment.getHDFName();

            // TODO read data from datah5

            DiagCovGMM gmm = ubm.copy();
            // TODO adapt gmm from ubm
            FloatVector sv = GMMUtils.createSupervector(gmm, ubm);

            data.add(null);

            if (speaker == null) {
                speaker = DenseFactory.floatMatrix(sv.length(), model.getTrain().size());
            }
            speaker.setColumn(trainIndex++, sv);
        }

        // TODO znorm
        // TODO tnorm

        SpeakerKernelMatrix kernelMatrix = new SpeakerKernelMatrix(background, speaker);
        SvmClassifier svm = new SvmClassifier(null, kernelMatrix);
        svm.train(100.0);
        FloatVector svmModel = svm.compact().getModel();

        // TODO classify all training data to make sure we have no errors
        // print some stats like niko does in his code

        score(model, svmModel, datah5);
    }

    private static FloatPackedMatrix readKernelMatrix() {
        HDFReader kernelReader = new HDFReader(null);
        int kernelDim = 1790;
        FloatPackedMatrix kernelMatrix = PackedFactory.floatSymmetric(kernelDim, Storage.DIRECT);
        kernelReader.read("/kernel", kernelMatrix);
        kernelReader.close();

        // TODO do nap on kernel matrix
        // get all eigenvectors from 0..200
        // sweep over N to get best one

        return kernelMatrix;
    }

    public static void main(final String[] args) throws IOException {
        LOGGER.info("Checking if evaluation file exists");
        AssertUtils.assertTrue(new File(EVAL_FILE).exists());
        LOGGER.info("Checking if UBM file exists");
        AssertUtils.assertTrue(new File(UBM_FILE).exists());
        LOGGER.info("Checking if SVM data file exists");
        AssertUtils.assertTrue(new File(SVM_DATA_FILE).exists());
        LOGGER.info("Checking if SVM kernel file exists");
        AssertUtils.assertTrue(new File(SVM_KERNEL_FILE).exists());
        LOGGER.info("Checking if data file exists");
        AssertUtils.assertTrue(new File(DATA_FILE).exists());
        List<Model> models = new ArrayList<Model>();
        BufferedReader reader = new BufferedReader(new FileReader(EVAL_FILE));
        String line = reader.readLine();
        while (line != null) {
            String[] parts = line.split("\\s+");
            String id = parts[0];
            final Gender gender = Gender.easyValueOf(parts[1]);
            String[] train = parts[2].split(",");
            List<Segment> trainList = new ArrayList<Segment>();
            for (String t : train) {
                String[] tparts = t.split(":");
                Segment segment = new Segment(tparts[0], tparts[1]);
                trainList.add(segment);
            }
            String[] test = parts[3].split(",");
            List<Trial> testList = new ArrayList<Trial>();
            for (String t : test) {
                String[] tparts = t.split(":");
                Trial trial = new Trial(tparts[0], tparts[1], tparts[2]);
                testList.add(trial);
            }
            Model model = new Model(id, gender, "1conv4w", trainList, "1conv4w", testList);
            models.add(model);
            line = reader.readLine();
        }
        reader.close();
        LOGGER.info("Read " + models.size() + " models");
        checkData(models);

        Set<Trial> trials = new HashSet<Trial>();
        for (Model model : models) {
            trials.addAll(model.getTest());
        }

        // TODO train trial gmms in parallel
        // store in hdf file

        // TODO evaluate models in parallel
//        for (Model model : models) {
//            doEvaluation(model, null, null, null);
//        }
    }
}
