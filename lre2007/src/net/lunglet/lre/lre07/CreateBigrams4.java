package net.lunglet.lre.lre07;

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

public final class CreateBigrams4 {
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

    private static FloatDenseVector calculateNGrams(final List<FloatDenseVector> segments) {
        int rows = segments.get(0).rows();
        int cols = segments.size();
        FloatDenseMatrix posteriors = new FloatDenseMatrix(rows, cols, Orientation.COLUMN, Storage.DIRECT);
        for (int j = 0; j < segments.size(); j++) {
            posteriors.setColumn(j, segments.get(j));
        }
        return PhonemeUtil.calculateNGrams(posteriors);
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
        System.out.println(ds.getName() + " " + label + " -> " + Arrays.toString(indexes));
        ds.close();
    }

    public static void main(final String[] args) throws UnsupportedAudioFileException, IOException {
        CrossValidationSplits cvsplits = Constants.CVSPLITS;
        Set<SplitEntry> splitEntries = cvsplits.getAllSplits();
        final String phonemePrefix = "cz";
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
        for (SplitEntry splitEntry : splitEntriesList) {
            File zipFile = splitEntry.getFile("_0.phnrec.zip");
            if (!zipFile.exists()) {
                continue;
            }
            List<FloatDenseVector> segments = readPhnRecZip(phonemePrefix, zipFile);
            FloatDenseVector ngrams = calculateNGrams(segments);
            Group group = groups.get(splitEntry.getCorpus());
            writeNGrams(splitEntry.getName(), splitEntry.getLanguage(), ngrams, group, index++);
        }
        for (Group group : groups.values()) {
            group.close();
        }
        h5file.close();
    }

//    public static void main2(final String[] args) throws UnsupportedAudioFileException, IOException {
//        CrossValidationSplits cvsplits = Constants.CVSPLITS;
//        Set<SplitEntry> splitFiles = cvsplits.getAllSplits();
//        String workingDir = Constants.WORKING_DIRECTORY;
//        H5File h5file = new H5File(new File(workingDir, "ngrams.h5"));
//        Map<String, Group> groups = new HashMap<String, Group>();
//        for (SplitEntry splitFile : splitFiles) {
//            if (groups.containsKey(splitFile.getCorpus())) {
//                continue;
//            }
//            Group group = h5file.getRootGroup().createGroup("/" + splitFile.getCorpus());
//            groups.put(splitFile.getCorpus(), group);
//        }
//        int index = 0;
//        List<SplitEntry> sortedfrontendFiles = new ArrayList<SplitEntry>(splitFiles);
//        Collections.sort(sortedfrontendFiles);
//        for (SplitEntry splitFile : sortedfrontendFiles) {
//            List<FloatDenseVector> ngramsList = new ArrayList<FloatDenseVector>();
//            for (String phonemePrefix : new String[]{"cz", "hu", "ru"}) {
//                File zipFile = splitFile.getFile("_0.phnrec.zip");
//                List<FloatDenseVector> segments = readPhnRecZip(phonemePrefix, zipFile);
//                ngramsList.add(calculateNGrams(segments));
//            }
//            FloatDenseVector ngrams = FloatMatrixUtils.concatenate(ngramsList.toArray(new FloatDenseVector[0]));
//            Group group = groups.get(splitFile.getCorpus());
//            writeNGrams(splitFile.getName(), splitFile.getLanguage(), ngrams, group, index++);
//        }
//        for (Group group : groups.values()) {
//            group.close();
//        }
//        h5file.close();
//    }
}
