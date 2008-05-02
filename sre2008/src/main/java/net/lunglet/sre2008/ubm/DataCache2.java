package net.lunglet.sre2008.ubm;

import com.google.common.collect.Iterators;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.lunglet.array4j.Order;
import net.lunglet.array4j.Storage;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DataCache2 implements Iterable<FloatDenseMatrix> {
    private final Map<String, SoftReference<FloatDenseMatrix>> cache;

    private final Logger logger = LoggerFactory.getLogger(DataCache2.class);

    private final List<String> names;

    private final HDFReader reader;

    public DataCache2(final String name) throws IOException {
        this.names = new ArrayList<String>();
        BufferedReader lineReader = new BufferedReader(new FileReader(name));
        try {
            String line = lineReader.readLine();
            while (line != null) {
                line = line.trim();
                final String filename;
                if (line.endsWith(":a") || line.endsWith(":b")) {
                    filename = line.substring(0, line.length() - 2);
                } else {
                    filename = line;
                }
                if (!new File(filename).isFile()) {
                    throw new IllegalArgumentException(filename + " is not a file");
                }
                names.add(line);
                line = lineReader.readLine();
            }
        } finally {
            lineReader.close();
        }
        this.reader = new HDFReader(16777216);
        this.cache = new HashMap<String, SoftReference<FloatDenseMatrix>>();
        Collections.sort(names);
        Collections.reverse(names);
    }

    public void close() {
        cache.clear();
    }

    public FloatDenseMatrix get(final String name) {
        SoftReference<FloatDenseMatrix> ref = cache.get(name);
        if (ref != null) {
            FloatDenseMatrix data = ref.get();
            if (data != null) {
                logger.info("Returning {} from the cache", name);
                return data;
            }
            logger.debug("Reading {} again (was in the cache, but was gc'ed)", name);
        }

        final String filename;
        final int channel;
        if (name.endsWith(":a") || name.endsWith(":b")) {
            int len = name.length();
            filename = name.substring(0, len - 2);
            channel = name.substring(len - 1, len).equals("a") ? 0 : 1;
        } else {
            filename = name;
            channel = 0;
        }
        H5File h5file = new H5File(filename);
        final String datasetName = "/mfcc/" + channel;
        DataSet dataset = h5file.getRootGroup().openDataSet(datasetName);
        int[] dims = dataset.getIntDims();
        dataset.close();
        FloatDenseMatrix data = DenseFactory.floatMatrix(dims, Order.ROW, Storage.HEAP);
        reader.read(h5file, datasetName, data);
        if (false) {
            // XXX caching is disabled for now...
            cache.put(name, new SoftReference<FloatDenseMatrix>(data));
        }
        h5file.close();
        logger.info("Read {}:{} {}", new Object[]{filename, channel, Arrays.toString(dims)});
        return data;
    }

    @Override
    public Iterator<FloatDenseMatrix> iterator() {
        Collections.reverse(names);
        final Iterator<String> namesIterator = names.iterator();
        return new Iterator<FloatDenseMatrix>() {
            @Override
            public boolean hasNext() {
                return namesIterator.hasNext();
            }

            @Override
            public FloatDenseMatrix next() {
                return get(namesIterator.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public Iterable<FloatDenseVector> rowsIterator() {
        Collections.reverse(names);
        final Iterator<String> namesIterator = names.iterator();
        return new Iterable<FloatDenseVector>() {
            @Override
            public Iterator<FloatDenseVector> iterator() {
                return Iterators.concat(new Iterator<Iterator<FloatDenseVector>>() {
                    @Override
                    public boolean hasNext() {
                        return namesIterator.hasNext();
                    }

                    @Override
                    public Iterator<FloatDenseVector> next() {
                        return get(namesIterator.next()).rowsIterator().iterator();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                });
            }
        };
    }
}
