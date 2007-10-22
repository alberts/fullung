package cz.vutbr.fit.speech.phnrec;

import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.io.MatrixInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// TODO factor out a PhnRecLabels class that only reads labels

public final class PhnRecFeatures {
    private static final String MLF_SUFFIX = ".mlf";

    private static final String POSTERIORS_SUFFIX = ".post";

    private final List<MasterLabel> labels;

    private final FloatDenseMatrix posteriors;

    private final List<MasterLabel> validLabels;

    private static final Map<String, int[]> POSTERIOR_ROWS_TO_IGNORE;

    static {
        Map<String, int[]> posteriorRowsToIgnore = new HashMap<String, int[]>();
        try {
            for (String prefix : new String[]{"cz", "hu", "ru"}) {
                int ignoreIndex = 0;
                int[] rowsToIgnore = new int[3 * PosteriorsConverter.PHONEMES_TO_IGNORE.size()];
                InputStream stream = PhnRecFeatures.class.getResourceAsStream(prefix + "phonemes.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                int phonemeIndex = 0;
                String line = reader.readLine();
                while (line != null) {
                    if (PosteriorsConverter.PHONEMES_TO_IGNORE.contains(line)) {
                        rowsToIgnore[ignoreIndex++] = 3 * phonemeIndex;
                        rowsToIgnore[ignoreIndex++] = 3 * phonemeIndex + 1;
                        rowsToIgnore[ignoreIndex++] = 3 * phonemeIndex + 2;
                    }
                    phonemeIndex++;
                    line = reader.readLine();
                }
                reader.close();
                posteriorRowsToIgnore.put(prefix, rowsToIgnore);
            }
            POSTERIOR_ROWS_TO_IGNORE = Collections.unmodifiableMap(posteriorRowsToIgnore);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PhnRecFeatures(final String prefix, final InputStream stream) throws IOException {
        ZipInputStream zis = new ZipInputStream(stream);
        ZipEntry entry = zis.getNextEntry();
        byte[] buf = new byte[65536];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<MasterLabel> labels = null;
        FloatDenseMatrix posteriors = null;
        while (entry != null) {
            baos.reset();
            String mlfName = prefix + MLF_SUFFIX;
            String postName = prefix + POSTERIORS_SUFFIX;
            String entryName = entry.getName();
            if ((mlfName.equals(entryName) || postName.equals(entryName)) && !entry.isDirectory()) {
                int n;
                while ((n = zis.read(buf, 0, buf.length)) > -1) {
                    baos.write(buf, 0, n);
                }
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                if (entryName.endsWith(MLF_SUFFIX)) {
                    labels = PhonemeUtil.readMasterLabels(new InputStreamReader(bais));
                } else {
                    posteriors = new MatrixInputStream(bais).readMatrix();
                }
            }
            zis.closeEntry();
            if (labels != null && posteriors != null) {
                break;
            }
            entry = zis.getNextEntry();
        }
        zis.close();
        if (labels == null) {
            throw new RuntimeException("no labels for prefix " + prefix);
        }
        if (posteriors == null) {
            throw new RuntimeException("no posteriors for prefix " + prefix);
        }
        List<MasterLabel> validLabels = new ArrayList<MasterLabel>();
        for (MasterLabel label : labels) {
            if (label.isValid()) {
                validLabels.add(label);
            }
        }
        if (posteriors.columns() != validLabels.size()) {
            throw new RuntimeException();
        }
        this.labels = Collections.unmodifiableList(labels);
        int[] rowsToIgnore = POSTERIOR_ROWS_TO_IGNORE.get(prefix);
        int validRows = posteriors.rows() - rowsToIgnore.length;
        FloatDenseMatrix validPosteriors = new FloatDenseMatrix(validRows, posteriors.columns());
        for (int i = 0, j = 0; i < posteriors.rows(); i++) {
            if (Arrays.binarySearch(rowsToIgnore, i) >= 0) {
                continue;
            }
            validPosteriors.setRow(j++, posteriors.row(i));
        }
        this.posteriors = validPosteriors;
        this.validLabels = Collections.unmodifiableList(validLabels);
    }

    public List<MasterLabel> getLabels() {
        return labels;
    }

    public FloatDenseMatrix getPosteriors() {
        return posteriors;
    }

    public List<MasterLabel> getValidLabels() {
        return validLabels;
    }

    public List<Segment> getValidSegments() {
        long startTime = 0L;
        long endTime = 0L;
        List<Segment> segments = new ArrayList<Segment>();
        boolean firstLabel = true;
        for (MasterLabel label : validLabels) {
            if (firstLabel) {
                startTime = label.startTime;
                endTime = label.endTime;
                firstLabel = false;
                continue;
            }
            if (endTime == label.startTime) {
                // advance endTime if two valid segments follow each other
                endTime = label.endTime;
            } else {
                segments.add(new Segment(startTime, endTime));
                startTime = label.startTime;
                endTime = label.endTime;
            }
        }
        // add last segment
        if (startTime != endTime) {
            segments.add(new Segment(startTime, endTime));
        }
        return segments;
    }
}
