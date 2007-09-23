package cz.vutbr.fit.speech.phnrec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.io.MatrixInputStream;

// TODO factor out a PhnRecLabels class that only reads labels

public final class PhnRecFeatures {
    private static final String MLF_SUFFIX = ".mlf";

    private static final String POSTERIORS_SUFFIX = ".post";

    private final List<MasterLabel> labels;

    private final FloatDenseMatrix posteriors;

    private final List<MasterLabel> validLabels;

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
        this.posteriors = posteriors;
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
