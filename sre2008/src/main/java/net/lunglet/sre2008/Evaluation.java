package net.lunglet.sre2008;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.H5Exception;
import net.lunglet.hdf.H5File;
import net.lunglet.util.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Evaluation {
    private static final String DATA_FILE = "Z:\\data\\sre05mfcc_1s1s.h5";

    private static final String EVAL_FILE = "sre05-1conv4w_1conv4w.txt";

    private static final Logger LOGGER = LoggerFactory.getLogger(Evaluation.class);

    private static final String SVM_KERNEL_FILE = null;

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

    public static void main(final String[] args) throws IOException {
        LOGGER.info("Checking if evaluation file exists");
        AssertUtils.assertTrue(new File(EVAL_FILE).exists());
        LOGGER.info("Checking if UBM file exists");
        AssertUtils.assertTrue(new File(UBM_FILE).exists());
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
            Model model = new Model(id, gender, trainList, testList);
            models.add(model);
            line = reader.readLine();
        }
        reader.close();
        LOGGER.info("Read " + models.size() + " models");
        checkData(models);
    }
}
