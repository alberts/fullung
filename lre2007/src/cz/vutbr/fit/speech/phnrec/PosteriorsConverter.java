package cz.vutbr.fit.speech.phnrec;

import com.googlecode.array4j.FloatMatrixUtils;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseUtils;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.io.MatrixOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class PosteriorsConverter {
    // 10ms frame period in the HTK time unit
    private static final long FRAME_PERIOD = 100000L;

    private static final List<String> PHONEMES_TO_IGNORE = Collections.unmodifiableList(Arrays.asList(new String[]{
            "int", "oth", "pau", "spk"}));

    private final List<MasterLabel> labels;

    private final FloatDenseMatrix posteriors;

    public PosteriorsConverter(final FloatDenseMatrix posteriors, final Collection<? extends MasterLabel> labels) {
        this.posteriors = posteriors;
        this.labels = new ArrayList<MasterLabel>(labels);
    }

    public List<FloatDenseVector> getPhonemePosteriors() {
        List<FloatDenseVector> postsums = new ArrayList<FloatDenseVector>();
        for (MasterLabel label : labels) {
            if (PHONEMES_TO_IGNORE.contains(label.label)) {
                continue;
            }
            int startIndex = (int) (label.startTime / FRAME_PERIOD);
            int endIndex = (int) (label.endTime / FRAME_PERIOD);
            FloatDenseMatrix postpart = FloatDenseUtils.subMatrixColumns(posteriors, startIndex, endIndex);
            FloatDenseVector postsum = FloatMatrixUtils.columnSum(postpart);
            postsums.add(postsum);
        }
        return postsums;
    }

    public void writeMasterLabels(final File outputFile) throws IOException {
        writeMasterLabels(new FileOutputStream(outputFile));
    }

    public void writeMasterLabels(final OutputStream out) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        for (MasterLabel label : labels) {
            writer.write(label.toString());
            writer.write("\n");
        }
        writer.flush();
    }

    public void writePhonemePosteriors(final File outputFile) throws IOException {
        writePhonemePosteriors(new FileOutputStream(outputFile));
    }

    public void writePhonemePosteriors(final OutputStream out) throws IOException {
        List<FloatDenseVector> postsums = getPhonemePosteriors();
        MatrixOutputStream matOut = new MatrixOutputStream(out);
        matOut.writeColumnsAsMatrix(postsums);
        matOut.flush();
    }
}
