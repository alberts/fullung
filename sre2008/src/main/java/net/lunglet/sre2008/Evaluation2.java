package net.lunglet.sre2008;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Evaluation2 {
    public static List<Model> readModels(final String filename) throws IOException {
        List<Model> models = new ArrayList<Model>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
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
        return models;
    }
}
