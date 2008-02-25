package cz.vutbr.fit.speech.phnrec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public final class PhonemeUtil {
    public static List<MasterLabel> readMasterLabels(final Reader reader) throws IOException {
        List<MasterLabel> labels = new ArrayList<MasterLabel>();
        BufferedReader bufReader = new BufferedReader(reader);
        String line = bufReader.readLine();
        while (line != null) {
            String[] parts = line.split("\\s+");
            String label = parts[2];
            long startTime = Long.valueOf(parts[0]);
            long endTime = Long.valueOf(parts[1]);
            float score = Float.valueOf(parts[3]);
            labels.add(new MasterLabel(label, startTime, endTime, score));
            line = bufReader.readLine();
        }
        return labels;
    }

    private PhonemeUtil() {
    }
}
