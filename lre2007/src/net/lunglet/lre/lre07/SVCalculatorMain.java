package net.lunglet.lre.lre07;

import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import cz.vutbr.fit.speech.phnrec.PhnRecFeatures;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
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

public final class SVCalculatorMain {
    private static final int NTHREADS = 0;

    private static final CrossValidationSplits CVSPLITS = Constants.CVSPLITS;

    private static final String PHONEME_PREFIX = "cz";

    private static final String WORKING_DIRECTORY = Constants.WORKING_DIRECTORY;

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

    private static void write(final String name, final String label, final FloatDenseVector data, final Group group,
            final int index) {
        DataType dtype = FloatType.IEEE_F32LE;
        DataSet ds = group.createDataSet(name, dtype, 1, data.length());
        DataSpace fileSpace = ds.getSpace();
        DataSpace memSpace = new DataSpace(data.length());
        int[] indexes = new int[]{index};
        if (data.length() != memSpace.getDim(0)) {
            throw new AssertionError();
        }
        long[] start = {0, 0};
        long[] count = {1, 1};
        long[] block = {1, memSpace.getDim(0)};
        fileSpace.selectHyperslab(SelectionOperator.SET, start, null, count, block);
        ds.write(data.data(), dtype, memSpace, fileSpace);
        DataSpace space = new DataSpace(indexes.length);
        Attribute attr = ds.createAttribute("indexes", IntType.STD_I32LE, space);
        attr.write(indexes);
        attr.close();
        space.close();
        memSpace.close();
        fileSpace.close();
        ds.createAttribute("label", label);
        ds.close();
    }

    public static void main(final String[] args) throws InterruptedException, IOException, ExecutionException {
        BitSet bigramIndexes = TrigramSVCalculator.getBigramIndexes(PHONEME_PREFIX, 100);
        final SupervectorCalculator svcalc = new TrigramSVCalculator(bigramIndexes);
        Set<SplitEntry> splitEntries = CVSPLITS.getAllSplits();
        final H5File h5file = new H5File(new File(WORKING_DIRECTORY, PHONEME_PREFIX + "ngrams.h5"));
        final Map<String, Group> groups = new HashMap<String, Group>();
        for (SplitEntry splitFile : splitEntries) {
            if (groups.containsKey(splitFile.getCorpus())) {
                continue;
            }
            Group group = h5file.getRootGroup().createGroup("/" + splitFile.getCorpus());
            groups.put(splitFile.getCorpus(), group);
        }
        List<SplitEntry> splitEntriesList = new ArrayList<SplitEntry>(splitEntries);
        Collections.sort(splitEntriesList);
//        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(3 * NTHREADS);
//        ExecutorService executor = new ThreadPoolExecutor(NTHREADS, NTHREADS, 0L, TimeUnit.MILLISECONDS, workQueue,
//                new ThreadPoolExecutor.CallerRunsPolicy());
//        List<Future<?>> futures = new ArrayList<Future<?>>();
        int index = 0;
        for (final SplitEntry splitEntry : splitEntriesList) {
            final File zipFile = splitEntry.getFile("_0.phnrec.zip");
            final int index2 = index++;
//            Future<Void> future = executor.submit(new Callable<Void>() {
//                @Override
//                public Void call() throws Exception {
                    System.out.println(zipFile + " [" + (index2 + 1) + " of " + splitEntriesList.size() + "]");
                    List<FloatDenseVector> segments = readPhnRecZip(PHONEME_PREFIX, zipFile);
                    int rows = segments.get(0).rows();
                    int cols = segments.size();
                    FloatDenseMatrix x = new FloatDenseMatrix(rows, cols, Orientation.COLUMN, Storage.DIRECT);
                    for (int j = 0; j < segments.size(); j++) {
                        x.setColumn(j, segments.get(j));
                    }
                    FloatDenseVector y = svcalc.apply(x);
                    Group group = groups.get(splitEntry.getCorpus());
                    synchronized (h5file) {
                        write(splitEntry.getName(), splitEntry.getLanguage(), y, group, index2);
                    }
//                    return null;
//                }
//            });
//            futures.add(future);
        }
//        for (Future<?> future : futures) {
//            future.get();
//        }
//        executor.shutdown();
//        executor.awaitTermination(0L, TimeUnit.MILLISECONDS);
        for (Group group : groups.values()) {
            group.close();
        }
        h5file.close();
    }
}
