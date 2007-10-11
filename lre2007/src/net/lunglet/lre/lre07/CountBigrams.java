package net.lunglet.lre.lre07;

import com.googlecode.array4j.FloatMatrixUtils;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseUtils;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.math.FloatMatrixMath;
import cz.vutbr.fit.speech.phnrec.PhnRecFeatures;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.lunglet.lre.lre07.CrossValidationSplits.SplitEntry;

public final class CountBigrams {
    private static List<FloatDenseVector> readPhnRecZip(final String phonemePrefix, final File zipFile)
            throws IOException {
        InputStream stream = new FileInputStream(zipFile);
        PhnRecFeatures features = new PhnRecFeatures(phonemePrefix, stream);
        stream.close();
        FloatDenseMatrix posteriors = features.getPosteriors();
        List<FloatDenseVector> segment = new ArrayList<FloatDenseVector>();
        for (FloatDenseVector column : posteriors.columnsIterator()) {
            segment.add(column);
        }
        return segment;
    }

    private static FloatDenseVector calculateBigrams(final FloatDenseMatrix posteriors) {
        if (posteriors.columns() <= 1) {
            return new FloatDenseVector(posteriors.rows() * posteriors.rows());
        }
        FloatDenseMatrix b1 = FloatDenseUtils.subMatrixColumns(posteriors, 0, posteriors.columns() - 1);
        FloatDenseMatrix b2 = FloatDenseUtils.subMatrixColumns(posteriors, 1, posteriors.columns());
        FloatDenseMatrix bigrams = FloatMatrixMath.times(b1, b2.transpose());
        return FloatMatrixUtils.columnsVector(bigrams);
    }

    public static void main(final String[] args) throws UnsupportedAudioFileException, IOException {
        CrossValidationSplits cvsplits = Constants.CVSPLITS;
        Set<SplitEntry> splitEntries = cvsplits.getSplit("all");
        final String phonemePrefix = "ru";
        List<SplitEntry> splitEntriesList = new ArrayList<SplitEntry>(splitEntries);
        Collections.sort(splitEntriesList);
        FloatDenseVector globalBigrams = null;
        for (SplitEntry splitFile : splitEntriesList) {
            File zipFile = splitFile.getFile("_0.phnrec.zip");
            if (!zipFile.exists()) {
                continue;
            }
            System.out.println(zipFile);
            List<FloatDenseVector> segments = readPhnRecZip(phonemePrefix, zipFile);
            FloatDenseMatrix posteriors = new FloatDenseMatrix(segments.get(0).rows(), segments.size());
            for (int j = 0; j < segments.size(); j++) {
                posteriors.setColumn(j, segments.get(j));
            }
            FloatDenseVector bigrams = calculateBigrams(posteriors);
            if (globalBigrams != null) {
                globalBigrams.plusEquals(bigrams);
            } else {
                globalBigrams = bigrams;
            }
        }
        System.out.println(globalBigrams);
    }
}
