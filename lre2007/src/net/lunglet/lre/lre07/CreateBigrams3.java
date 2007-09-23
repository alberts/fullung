package net.lunglet.lre.lre07;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
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
import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;

import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;

import cz.vutbr.fit.speech.phnrec.MasterLabel;
import cz.vutbr.fit.speech.phnrec.PhnRecFeatures;
import cz.vutbr.fit.speech.phnrec.PhonemeUtil;

public final class CreateBigrams3 {
    private static final String PHONEME_PREFIX = "cz";

    private static final int MAX_SEGMENTS = 5;

    private static final FilenameFilter SPHERE_FILTER = new FilenameSuffixFilter(".sph", true);

    private static FloatDenseVector calculateNGrams(final List<FloatDenseVector> segment) {
        FloatDenseMatrix posteriors = new FloatDenseMatrix(segment.get(0).rows(), segment.size());
        for (int j = 0; j < segment.size(); j++) {
            posteriors.setColumn(j, segment.get(j));
        }
        return PhonemeUtil.calculateNGrams(posteriors);
    }

    private static List<List<FloatDenseVector>> readPhnRec(final File zipFile) throws IOException {
        InputStream stream = new FileInputStream(zipFile);
        PhnRecFeatures features = new PhnRecFeatures(PHONEME_PREFIX, stream);
        stream.close();
        FloatDenseMatrix posteriors = features.getPosteriors();
        List<List<FloatDenseVector>> segments = new ArrayList<List<FloatDenseVector>>();
        List<FloatDenseVector> segment = new ArrayList<FloatDenseVector>();
        for (FloatDenseVector column : posteriors.columnsIterator()) {
            segment.add(column);
        }
        segments.add(segment);
        return segments;
    }

    private static List<List<FloatDenseVector>> readPhnRecSplit(final File zipFile) throws IOException {
        InputStream stream = new FileInputStream(zipFile);
        PhnRecFeatures features = new PhnRecFeatures(PHONEME_PREFIX, stream);
        stream.close();
        FloatDenseMatrix posteriors = features.getPosteriors();
        List<MasterLabel> labels = features.getLabels();
        List<List<FloatDenseVector>> segments = new ArrayList<List<FloatDenseVector>>();
        long duration = 0;
        List<FloatDenseVector> segment = new ArrayList<FloatDenseVector>();
        for (int i = 0, j = 0; i < labels.size(); i++) {
            MasterLabel label = labels.get(i);
            if (label.isValid()) {
                segment.add(posteriors.column(j++));
                // only take valid segments into account
                duration += label.getDuration();
            }
            // TODO tune this value based on duration of segments in NIST 30s data
            if (duration >= 25 * 1e7) {
                segments.add(segment);
                duration = 0L;
                segment = new ArrayList<FloatDenseVector>();
            }
        }
        // TODO maybe add last segment if it is long enough
        return segments;
    }

    private static List<List<FloatDenseVector>> readPhnRec(final File audioFile, final boolean splitChannels)
            throws UnsupportedAudioFileException, IOException {
        AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
        int channels = ais.getFormat().getChannels();
        final List<List<FloatDenseVector>> segments = new ArrayList<List<FloatDenseVector>>();
        for (int i = 0; i < channels; i++) {
            File zipFile = new File(audioFile.getCanonicalPath() + "_" + i + ".phnrec.zip");
            if (splitChannels) {
                segments.addAll(readPhnRecSplit(zipFile));
            } else {
                segments.addAll(readPhnRec(zipFile));
            }
        }
        return segments;
    }

    private static List<FloatDenseVector> calculateNGrams(final File audioFile, String id, final boolean splitChannels)
            throws UnsupportedAudioFileException, IOException {
        List<List<FloatDenseVector>> segments = readPhnRec(audioFile, splitChannels);
        Collections.shuffle(segments, new Random(id.hashCode()));
        segments = segments.subList(0, Math.min(MAX_SEGMENTS, segments.size()));
        List<FloatDenseVector> ngrams = new ArrayList<FloatDenseVector>();
        for (List<FloatDenseVector> segment : segments) {
            if (segment.size() < 2) {
                continue;
            }
            ngrams.add(calculateNGrams(segment));
        }
        return ngrams;
    }

    private static void writeNGrams(final String name, final String label, final List<FloatDenseVector> ngrams,
            final Group group, final IdCounter counter) {
        DataType dtype = FloatType.IEEE_F32LE;
        DataSet ds = group.createDataSet(name, dtype, ngrams.size(), ngrams.get(0).length());
        DataSpace fileSpace = ds.getSpace();
        DataSpace memSpace = new DataSpace(ngrams.get(0).length());
        int i = 0;
        int[] indexes = new int[ngrams.size()];
        for (FloatDenseVector x : ngrams) {
            if (x.length() != memSpace.getDim(0)) {
                throw new AssertionError();
            }
            indexes[i] = counter.next++;
            long[] start = {i++, 0};
            long[] count = {1, 1};
            long[] block = {1, memSpace.getDim(0)};
            fileSpace.selectHyperslab(SelectionOperator.SET, start, null, count, block);
            ds.write(x.data(), dtype, memSpace, fileSpace);
        }
        DataSpace space = new DataSpace(indexes.length);
        Attribute attr = ds.createAttribute("indexes", IntType.STD_I32LE, space);
        attr.write(indexes);
        attr.close();
        space.close();
        memSpace.close();
        fileSpace.close();
        ds.createAttribute("label", label);
        System.out.println(ds.getName() + " " + Arrays.toString(indexes) + " -> " + label);
        ds.close();
    }

    private static String getCallFriendId(final File file) {
        return file.getName().substring(3, file.getName().lastIndexOf("."));
    }

    private static String getNISTId(final File file) {
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }

    private static void readCallfriend(final Group root, final Map<String, String> labels, final IdCounter counter)
            throws UnsupportedAudioFileException, IOException {
        Group group = root.createGroup("callfriend");
        File[] files = FileUtils.listFiles("F:/CallFriend", SPHERE_FILTER, true);
        for (File file : files) {
            String id = getCallFriendId(file);
            String label = labels.get("/callfriend/" + id);
//            System.out.println(file + " -> " + label);
            List<FloatDenseVector> ngrams = calculateNGrams(file, id, true);
            if (ngrams.size() == 0) {
                System.out.println("no ngrams for " + file);
                continue;
            }
            writeNGrams(id, label, ngrams, group, counter);
        }
        group.close();
    }

    private static void readNist(final Group root, final String name, final Map<String, String> labels,
            final IdCounter counter) throws UnsupportedAudioFileException, IOException {
        Group group = root.createGroup(name);
        File[] files = FileUtils.listFiles("F:/NIST/" + name, SPHERE_FILTER, true);
        for (File file : files) {
            String id = getNISTId(file);
            String label = labels.get("/" + name + "/" + id);
//            System.out.println(file + " -> " + label);
            List<FloatDenseVector> ngrams = calculateNGrams(file, id, false);
            if (ngrams.size() == 0) {
                System.out.println("no ngrams for " + file);
                continue;
            }
            writeNGrams(id, label, ngrams, group, counter);
        }
        group.close();
    }

    private static class IdCounter {
        private int next = 0;
    }

    public static void main(final String[] args) throws UnsupportedAudioFileException, IOException {
        IdCounter counter = new IdCounter();
        H5File h5file = new H5File("F:/ngrams.h5");
        Group root = h5file.getRootGroup();
        Map<String, String> labels = readLabels("C:/home/albert/LRE2007/keysetc/albert/output/key.txt");
        readNist(root, "lid96d1", labels, counter);
        readNist(root, "lid96e1", labels, counter);
        readNist(root, "lid03e1", labels, counter);
        readNist(root, "lid05d1", labels, counter);
        readNist(root, "lid05e1", labels, counter);
        readCallfriend(root, labels, counter);
        h5file.close();
    }

    private static Map<String, String> readLabels(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        Map<String, String> labels = new HashMap<String, String>();
        String line = reader.readLine();
        while (line != null) {
            String[] parts = line.split("\\s+");
            String[] idparts = parts[0].split(",");
            String id = "/" + idparts[0] + "/" + idparts[1]; 
            String label = parts[1];
            labels.put(id, label);
            line = reader.readLine();
        }
        reader.close();
        return labels;
    }
}
