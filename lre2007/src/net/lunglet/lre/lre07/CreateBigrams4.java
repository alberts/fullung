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
import java.util.HashSet;
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
import net.lunglet.io.FileUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;

import cz.vutbr.fit.speech.phnrec.PhnRecFeatures;
import cz.vutbr.fit.speech.phnrec.PhonemeUtil;

public final class CreateBigrams4 {
    private static final String SPLITS_DIR = "C:/home/albert/LRE2007/keysetc/albert/mitpart2";

    private static class SplitFile implements Comparable<SplitFile> {
        private String id;

        private String corpus;

        private String filename;

        private String label;

        private int duration;

        public File getFile() {
            return new File("G:/MIT/data/" + duration + "/" + corpus + "/" + filename + "_0.phnrec.zip");
        }

        public boolean exists() {
            return getFile().exists();
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null || !(obj instanceof SplitFile)) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            SplitFile other = (SplitFile) obj;
            return new EqualsBuilder().append(id, other.id).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(id).toHashCode();
        }

        @Override
        public int compareTo(final SplitFile o) {
            return id.compareTo(o.id);
        }
    }

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
        FloatDenseMatrix posteriors = new FloatDenseMatrix(segments.get(0).rows(), segments.size());
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
        Set<SplitFile> splitFiles = readSplits("frontend");
        splitFiles.addAll(readSplits("backend"));
        splitFiles.addAll(readSplits("test"));
        System.out.println(splitFiles.size());
        System.exit(1);

        final String phonemePrefix = "ru";
        H5File h5file = new H5File("G:/" + phonemePrefix + "ngrams.h5");
        Map<String, Group> groups = new HashMap<String, Group>();
        for (SplitFile splitFile : splitFiles) {
            if (groups.containsKey(splitFile.corpus)) {
                continue;
            }
            Group group = h5file.getRootGroup().createGroup("/" + splitFile.corpus);
            groups.put(splitFile.corpus, group);
        }

        int index = 0;
        List<SplitFile> sortedfrontendFiles = new ArrayList<SplitFile>(splitFiles);
        Collections.sort(sortedfrontendFiles);
        for (SplitFile splitFile : sortedfrontendFiles) {
            File zipFile = splitFile.getFile();
//            System.out.println(zipFile);
            List<FloatDenseVector> segments = readPhnRecZip(phonemePrefix, zipFile);
            if (segments.size() < 2) {
                System.out.println("too few segments for " + zipFile);
                continue;
            }
            FloatDenseVector ngrams = calculateNGrams(segments);
            Group group = groups.get(splitFile.corpus);
            writeNGrams(splitFile.id, splitFile.label, ngrams, group, index++);
        }

        for (Group group : groups.values()) {
            group.close();
        }
        h5file.close();
    }

    private static Set<SplitFile> readSplits(final String prefix) throws IOException {
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                File file = new File(dir, name);
                if (!file.isFile()) {
                    return false;
                }
                String fileName = file.getName().toLowerCase();
                return fileName.startsWith(prefix + "_") && fileName.endsWith(".txt");
            }
        };
        File[] splitIndexFiles = FileUtils.listFiles(SPLITS_DIR, filter);
        Set<SplitFile> splitFiles = new HashSet<SplitFile>();
        for (File splitIndexFile : splitIndexFiles) {
            System.out.println(splitIndexFile);
            BufferedReader reader = new BufferedReader(new FileReader(splitIndexFile), 512 * 1024);
            try {
                String line = reader.readLine();
                while (line != null) {
                    String[] parts = line.split("\\s+");
                    SplitFile splitFile = new SplitFile();
                    splitFile.corpus = parts[0].toLowerCase();
                    splitFile.filename = parts[2];
                    splitFile.id = String.format("/%s/%s", splitFile.corpus, splitFile.filename);
                    splitFile.label = parts[3];
                    splitFile.duration = Integer.valueOf(parts[4]);
                    if (splitFile.duration != 3 && splitFile.duration != 10 && splitFile.duration != 30) {
                        throw new IOException();
                    }
                    splitFiles.add(splitFile);
                    line = reader.readLine();
                }
            } finally {
                reader.close();
            }
        }
        Set<SplitFile> invalidSplitFiles = new HashSet<SplitFile>();
        for (SplitFile splitFile : splitFiles) {
            if (!splitFile.exists()) {
                invalidSplitFiles.add(splitFile);
                // XXX get rid of this callfriend hack later
                if (!splitFile.corpus.toLowerCase().equals("callfriend")) {
                    System.out.println(splitFile.getFile() + " is missing");
                }
            }
        }
        splitFiles.removeAll(invalidSplitFiles);
        return splitFiles;
    }
}
