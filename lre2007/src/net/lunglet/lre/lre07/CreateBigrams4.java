package net.lunglet.lre.lre07;

import com.googlecode.array4j.FloatMatrixUtils;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import cz.vutbr.fit.speech.phnrec.PhnRecFeatures;
import cz.vutbr.fit.speech.phnrec.PhonemeUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.lunglet.hdf.Attribute;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.DataType;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.IntType;
import net.lunglet.hdf.SelectionOperator;
import net.lunglet.lre.lre07.CrossValidationSplits.SplitEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class CreateBigrams4 {
    private static final Log LOG = LogFactory.getLog(CreateBigrams4.class);

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

    private static FloatDenseVector calculateNGrams(final List<FloatDenseVector> segments,
            final BitSet bigramIndexes) {
        int rows = segments.get(0).rows();
        int cols = segments.size();
        FloatDenseMatrix posteriors = new FloatDenseMatrix(rows, cols, Orientation.COLUMN, Storage.DIRECT);
        for (int j = 0; j < segments.size(); j++) {
            posteriors.setColumn(j, segments.get(j));
        }
        FloatDenseVector monograms = PhonemeUtil.calculateMonograms(posteriors);
        FloatDenseVector bigrams = PhonemeUtil.calculateBigrams(posteriors, 1);
        FloatDenseVector stagbi = PhonemeUtil.calculateBigrams(posteriors, 2);
//        FloatDenseVector trigrams = PhonemeUtil.calculateTrigrams(posteriors, bigramIndexes);
//        FloatDenseVector ngrams = FloatMatrixUtils.concatenate(monograms, bigrams, trigrams);
        FloatDenseVector ngrams = FloatMatrixUtils.concatenate(monograms, bigrams, stagbi);
//        FloatDenseVector ngrams = FloatMatrixUtils.concatenate(monograms, bigrams);
        return ngrams;
//        return monograms;
    }

    private static void writeNGrams(final String name, final String label, final FloatDenseVector ngrams,
            final Group group, final int index) {
        DataType dtype = FloatType.IEEE_F32LE;
        DataSet ds = group.createDataSet(name, dtype, 1, ngrams.length());
        DataSpace fileSpace = ds.getSpace();
        DataSpace memSpace = new DataSpace(ngrams.length());
        int[] indexes = new int[]{index};
        if (ngrams.length() != memSpace.getDim(0)) {
            throw new AssertionError();
        }
        long[] start = {0, 0};
        long[] count = {1, 1};
        long[] block = {1, memSpace.getDim(0)};
        fileSpace.selectHyperslab(SelectionOperator.SET, start, null, count, block);
        ds.write(ngrams.data(), dtype, memSpace, fileSpace);
        DataSpace space = new DataSpace(indexes.length);
        Attribute attr = ds.createAttribute("indexes", IntType.STD_I32LE, space);
        attr.write(indexes);
        attr.close();
        space.close();
        memSpace.close();
        fileSpace.close();
        ds.createAttribute("label", label);
        // TODO add duration attribute that is calculated from MLF
        System.out.println(ds.getName() + " " + label + " -> " + Arrays.toString(indexes));
        ds.close();
    }

    public static void main(final String[] args) throws UnsupportedAudioFileException, IOException {
        LOG.info("creating feature vectors");
        CrossValidationSplits cvsplits = Constants.CVSPLITS;
        Set<SplitEntry> splitEntries = cvsplits.getAllSplits();
        final String phonemePrefix = "cz";
        BitSet bigramIndexes = PhonemeUtil.getBigramIndexes(phonemePrefix, 75);
        String workingDir = Constants.WORKING_DIRECTORY;
        H5File h5file = new H5File(new File(workingDir, phonemePrefix + "ngrams.h5"));
        Map<String, Group> groups = new HashMap<String, Group>();
        for (SplitEntry splitFile : splitEntries) {
            if (groups.containsKey(splitFile.getCorpus())) {
                continue;
            }
            Group group = h5file.getRootGroup().createGroup("/" + splitFile.getCorpus());
            groups.put(splitFile.getCorpus(), group);
        }
        int index = 0;
        List<SplitEntry> splitEntriesList = new ArrayList<SplitEntry>(splitEntries);
        Collections.sort(splitEntriesList);
        System.out.println("calculating supervectors for " + splitEntriesList.size() + " entries");
        for (SplitEntry splitEntry : splitEntriesList) {
            File zipFile = splitEntry.getFile("_0.phnrec.zip");
            if (!zipFile.exists()) {
                LOG.info("skipping " + zipFile);
                continue;
            }
            List<FloatDenseVector> segments = readPhnRecZip(phonemePrefix, zipFile);
            FloatDenseVector ngrams = calculateNGrams(segments, bigramIndexes);
            Group group = groups.get(splitEntry.getCorpus());
            writeNGrams(splitEntry.getName(), splitEntry.getLanguage(), ngrams, group, index++);
        }
        for (Group group : groups.values()) {
            group.close();
        }
        h5file.close();
        LOG.info("feature vector calculation is done");
    }
}
