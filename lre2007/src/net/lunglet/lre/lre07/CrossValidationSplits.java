package net.lunglet.lre.lre07;

import com.googlecode.array4j.FloatVector;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.SelectionOperator;
import net.lunglet.svm.jacksvm.AbstractHandle2;
import net.lunglet.svm.jacksvm.Handle2;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public final class CrossValidationSplits {
    public final class SplitEntry implements Comparable<SplitEntry> {
        private String corpus;

        private String filename;

        private String language;

        private int duration;

        public String getName() {
            return String.format("/%s/%s", corpus, filename);
        }

        public File getFile(final String suffix) {
            return new File(getFile().getPath() + suffix);
        }

        public File getFile() {
            return new File(dataDirectory, duration + "/" + corpus + "/" + filename);
        }

        public String getCorpus() {
            return corpus;
        }

        public int getDuration() {
            return duration;
        }

        public String getLanguage() {
            return language;
        }

        public String getBaseName() {
            return filename.substring(0, filename.lastIndexOf(".sph"));
        }

        public void setDuration(final int duration) {
            if (duration != 3 && duration != 10 && duration != 30) {
                throw new IllegalArgumentException();
            }
            this.duration = duration;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null || !(obj instanceof SplitEntry)) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            SplitEntry other = (SplitEntry) obj;
            return new EqualsBuilder().append(corpus, other.corpus).append(filename, other.filename).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(corpus).append(filename).toHashCode();
        }

        @Override
        public int compareTo(final SplitEntry o) {
            return getName().compareTo(o.getName());
        }
    }

    private final File splitsDirectory;

    private final File dataDirectory;

    private final Map<String, Set<SplitEntry>> splits;

    private final int testSplits;

    private final int backendSplits;

    public CrossValidationSplits() throws IOException {
        this(10, 10, new File(Constants.SPLITS_DIRECTORY), new File(Constants.DATA_DIRECTORY));
    }

    public CrossValidationSplits(int testSplits, final int backendSplits) throws IOException {
        this(testSplits, backendSplits, new File(Constants.SPLITS_DIRECTORY), new File(Constants.DATA_DIRECTORY));
    }

    public CrossValidationSplits(final int testSplits, final int backendSplits, final File splitsDirectory,
            final File dataDirectory) throws IOException {
        this.splitsDirectory = splitsDirectory;
        this.dataDirectory = dataDirectory;
        this.testSplits = testSplits;
        this.backendSplits = backendSplits;
        this.splits = readSplits();
    }

    public CrossValidationSplits(final String splitsDirectory, final String dataDirectory) throws IOException {
        this(10, 10, new File(splitsDirectory), new File(dataDirectory));
    }

    public int getTestSplits() {
        return testSplits;
    }

    public int getBackendSplits() {
        return backendSplits;
    }

    public Set<SplitEntry> getSplit(final String name) {
        return splits.get(name);
    }

    public Set<SplitEntry> getAllSplits() {
        return splits.get("all");
    }

    private static FloatDenseVector getHandleData(final H5File datah5, final String name, final long[] start) {
        DataSet dataset = datah5.getRootGroup().openDataSet(name);
        DataSpace fileSpace = dataset.getSpace();
        int len = (int) fileSpace.getDim(1);
        DataSpace memSpace = new DataSpace(len);
        // TODO investigate effect of heap vs direct storage here
        FloatDenseVector data = new FloatDenseVector(len, Orientation.COLUMN, Storage.HEAP);
        long[] count = {1, 1};
        long[] block = {1, fileSpace.getDim(1)};
        fileSpace.selectHyperslab(SelectionOperator.SET, start, null, count, block);
        dataset.read(data.data(), FloatType.IEEE_F32LE, memSpace, fileSpace);
        fileSpace.close();
        memSpace.close();
        dataset.close();
        return data;
    }

    private FloatDenseMatrix getData(final H5File datah5, final Set<SplitEntry> splits) {
        return null;
    }

    public FloatDenseMatrix getBackendData(final H5File datah5) {
        return getData(datah5, splits.get("backend"));
    }

    public FloatDenseMatrix getTestData(final H5File datah5) {
        return getData(datah5, splits.get("test"));
    }

    public Map<String, Handle2> getDataMap(final String splitName, final H5File datah5) {
        Map<String, Handle2> data = new HashMap<String, Handle2>();
        for (Handle2 handle : getData(splitName, datah5)) {
            data.put(handle.getName(), handle);
        }
        return data;
    }

    public List<Handle2> getData(final String splitName, final Map<String, Handle2> data) {
        List<Handle2> handles = new ArrayList<Handle2>();
        for (SplitEntry entry : getSplit(splitName)) {
            handles.add(data.get(entry.getName()));
        }
        // sort handles here so that indexes into handle list always retrieve
        // the same data
        Collections.sort(handles);
        return handles;
    }

    public List<Handle2> getData(final String splitName, final H5File datah5) {
        List<Handle2> handles = new ArrayList<Handle2>();
        for (SplitEntry entry : splits.get(splitName)) {
            final String name = entry.getName();
            DataSet ds = datah5.getRootGroup().openDataSet(name);
            int[] indexes = ds.getIntArrayAttribute("indexes");
            String label = ds.getStringAttribute("label");
            ds.close();
            for (int i = 0; i < indexes.length; i++) {
                final int j = i;
                final int index = indexes[i];
                handles.add(new AbstractHandle2(name, index, label) {
                    @Override
                    public FloatVector<?> getData() {
                        return getHandleData(datah5, name, new long[]{j, 0});
                    }
                });
            }
        }
        return handles;
    }

    private Set<SplitEntry> readSplit(final String splitName) throws IOException {
        File splitFile = new File(splitsDirectory, splitName + ".txt");
        BufferedReader reader = new BufferedReader(new FileReader(splitFile), 1024 * 1024);
        String line = reader.readLine();
        Set<SplitEntry> entries = new HashSet<SplitEntry>();
        while (line != null) {
            SplitEntry entry = new SplitEntry();
            String[] parts = line.split("\\s+");
            // TODO remove this toLowerCase call
            entry.corpus = parts[0].toLowerCase();
            entry.filename = parts[2];
            entry.language = parts[3];
            entry.setDuration(Integer.valueOf(parts[4]));

            if (!entry.corpus.equals("callfriend") && !entry.filename.equals("tgtd.sph.2.30s.sph")) {
                entries.add(entry);
            }

            line = reader.readLine();
        }
        return entries;
    }

    private Map<String, Set<SplitEntry>> readSplits() throws IOException {
        Map<String, Set<SplitEntry>> splits = new HashMap<String, Set<SplitEntry>>();
        Set<SplitEntry> frontend = new HashSet<SplitEntry>();
        Set<SplitEntry> backend = new HashSet<SplitEntry>();
        Set<SplitEntry> test = new HashSet<SplitEntry>();
        for (int i = 0; i < testSplits; i++) {
            String testname = "test_" + i;
            Set<SplitEntry> testi = readSplit(testname);
            splits.put(testname, testi);
            test.addAll(testi);
            for (int j = 0; j < backendSplits; j++) {
                final String fename = "frontend_" + i + "_" + j;
                Set<SplitEntry> feij = readSplit(fename);
                splits.put(fename, feij);
                frontend.addAll(feij);
                final String bename = "backend_" + i + "_" + j;
                Set<SplitEntry> beij = readSplit(bename);
                splits.put(bename, beij);
                backend.addAll(beij);
            }
        }
        splits.put("frontend", frontend);
        splits.put("backend", backend);
        splits.put("test", test);
        Set<SplitEntry> all = new HashSet<SplitEntry>();
        all.addAll(frontend);
        all.addAll(backend);
        all.addAll(test);
        splits.put("all", all);
        return splits;
    }
}
