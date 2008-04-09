package net.lunglet.sre2008.ubm;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.lunglet.array4j.Order;
import net.lunglet.array4j.Storage;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DataCache implements Iterable<FloatDenseMatrix> {
    private static List<String> getNames(final H5File h5file) {
        List<String> names = new ArrayList<String>();
        for (Group group : h5file.getRootGroup().getGroups()) {
            for (DataSet ds : group.getDataSets()) {
                names.add(ds.getName());
                ds.close();
            }
            group.close();
        }
        Collections.sort(names);
        return names;
    }

    private final Map<String, SoftReference<FloatDenseMatrix>> cache;

    private final H5File h5file;

    private final Logger logger = LoggerFactory.getLogger(DataCache.class);

    private final List<String> names;

    private final HDFReader reader;

    public DataCache(final String name) {
        this.h5file = new H5File(name, H5File.H5F_ACC_RDONLY);
        this.names = getNames(h5file);
        this.reader = new HDFReader(h5file, 16777216);
        this.cache = new HashMap<String, SoftReference<FloatDenseMatrix>>();
        Collections.reverse(names);
    }

    public void close() {
        cache.clear();
        reader.close();
    }

    @Override
    public Iterator<FloatDenseMatrix> iterator() {
        Collections.reverse(names);
        final Iterator<String> iter = names.iterator();
        return new Iterator<FloatDenseMatrix>() {
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public FloatDenseMatrix next() {
                String name = iter.next();
                SoftReference<FloatDenseMatrix> ref = cache.get(name);
                if (ref != null) {
                    FloatDenseMatrix data = ref.get();
                    if (data != null) {
                        logger.info("Returning {} from the cache", name);
                        return data;
                    }
                    logger.info("Reading {} again (was in the cache, but was gc'ed)", name);
                } else {
                    logger.info("Reading {}", name);
                }
                DataSet dataset = h5file.getRootGroup().openDataSet(name);
                int[] dims = dataset.getIntDims();
                dataset.close();
                FloatDenseMatrix data = DenseFactory.floatMatrix(dims, Order.ROW, Storage.HEAP);
                reader.read(name, data);
                cache.put(name, new SoftReference<FloatDenseMatrix>(data));
                return data;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
