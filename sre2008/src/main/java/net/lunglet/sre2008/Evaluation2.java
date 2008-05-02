package net.lunglet.sre2008;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.H5Exception;
import net.lunglet.hdf.H5File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Evaluation2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(Evaluation2.class);

    public static void checkData(final H5File h5file, final List<Model> models) {
        LOGGER.info("Checking data file {}", h5file.getFileName());
        for (Model model : models) {
            String name = null;
            try {
                for (Segment segment : model.getTrain()) {
                    name = segment.getHDFName();
                    DataSet dataset = h5file.getRootGroup().openDataSet(name);
                    dataset.close();
                }
                if (model.getTest() != null) {
                    for (Segment segment : model.getTest()) {
                        name = segment.getHDFName();
                        DataSet dataset = h5file.getRootGroup().openDataSet(name);
                        dataset.close();
                    }
                }
            } catch (H5Exception e) {
                LOGGER.error("Dataset for " + name + " doesn't exist", e);
                throw e;
            }
        }
    }

    public static List<Model> readModels(final String filename) throws IOException {
        List<Model> models = new ArrayList<Model>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
        while (line != null) {
            String[] parts = line.trim().split("\\s+");
            String id = parts[0].trim();
            final Gender gender = Gender.valueOf2(parts[1]);
            String[] train = parts[2].trim().split(",");
            List<Segment> trainList = new ArrayList<Segment>();
            for (String t : train) {
                String[] tparts = t.trim().split(":");
                Segment segment = new Segment(tparts[0], tparts[1]);
                trainList.add(segment);
            }
            String[] test = parts[3].trim().split(",");
            List<Trial> testList = new ArrayList<Trial>();
            for (String t : test) {
                String[] tparts = t.trim().split(":");
                Trial trial = new Trial(tparts[0], tparts[1], tparts[2]);
                testList.add(trial);
            }
            Model model = new Model(id, gender, "1conv4w", trainList, "1conv4w", testList);
            models.add(model);
            line = reader.readLine();
        }
        reader.close();
        return models;
    }
}
