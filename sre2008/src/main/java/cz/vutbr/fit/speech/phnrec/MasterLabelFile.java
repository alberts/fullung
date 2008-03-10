package cz.vutbr.fit.speech.phnrec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.lunglet.util.AssertUtils;

public final class MasterLabelFile implements Iterable<MasterLabel> {
    private static List<MasterLabel> readLabels(final Reader reader) throws IOException {
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

    private final List<MasterLabel> labels;

    public MasterLabelFile(final File file) throws IOException {
        this(new FileReader(file));
    }

    public MasterLabelFile(final Reader reader) throws IOException {
        this.labels = readLabels(reader);
        if (labels.size() == 0) {
            throw new IOException("Invalid label file");
        }
    }

    public boolean containsTimestamp(final double t) {
        return t >= labels.get(0).getStartTime() && t <= labels.get(labels.size() - 1).getEndTime();
    }

    public double getLastEndTime() {
        return labels.get(labels.size() - 1).getEndTime();
    }

    /**
     * Returns the number of valid phonemes in the segment.
     * 
     * @param begin
     *                start of frame in seconds
     * @param end
     *                end of frame in seconds
     */
    public int getValidPhonemeCount(final double begin, final double end) {
        if (begin < labels.get(0).getStartTime()) {
            throw new IllegalArgumentException();
        }
        if (labels.get(labels.size() - 1).getEndTime() < end) {
            throw new IllegalArgumentException();
        }
        // set startIndex to last block to handle the case where start and end
        // are both the last timestamp in the file
        int startIndex = labels.size() - 1;
        for (int i = 0; i < labels.size(); i++) {
            MasterLabel label = labels.get(i);
            // the less than comparison means that startIndex is never set
            // inside this loop if start and end are equal to the last timestamp
            // in the file
            if (begin >= label.getStartTime() && begin < label.getEndTime()) {
                startIndex = i;
                break;
            }
        }
        int endIndex = -1;
        for (int i = startIndex; i < labels.size(); i++) {
            MasterLabel label = labels.get(i);
            if (end >= label.getStartTime() && end <= label.getEndTime()) {
                endIndex = i;
                break;
            }
        }
        AssertUtils.assertTrue(endIndex >= 0 && endIndex >= startIndex);
        int validPhonemeCount = 0;
        for (int i = startIndex; i <= endIndex; i++) {
            if (labels.get(i).isValid()) {
                validPhonemeCount++;
            }
        }
        return validPhonemeCount;
    }

    /**
     * @param begin
     *                start of frame in seconds
     * @param end
     *                end of frame in seconds
     */
    public boolean isOnlySpeech(final double begin, final double end) {
        if (begin < labels.get(0).getStartTime()) {
            throw new IllegalArgumentException();
        }
        if (labels.get(labels.size() - 1).getEndTime() < end) {
            throw new IllegalArgumentException();
        }
        // set startIndex to last block to handle the case where start and end
        // are both the last timestamp in the file
        int startIndex = labels.size() - 1;
        for (int i = 0; i < labels.size(); i++) {
            MasterLabel label = labels.get(i);
            // the less than comparison means that startIndex is never set
            // inside this loop if start and end are equal to the last timestamp
            // in the file
            if (begin >= label.getStartTime() && begin < label.getEndTime()) {
                startIndex = i;
                break;
            }
        }
        int endIndex = -1;
        for (int i = startIndex; i < labels.size(); i++) {
            MasterLabel label = labels.get(i);
            if (end >= label.getStartTime() && end <= label.getEndTime()) {
                endIndex = i;
                break;
            }
        }
        AssertUtils.assertTrue(endIndex >= 0 && endIndex >= startIndex);
        for (int i = startIndex; i <= endIndex; i++) {
            MasterLabel label = labels.get(i);
            if (!label.isValid()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<MasterLabel> iterator() {
        return labels.iterator();
    }
}
