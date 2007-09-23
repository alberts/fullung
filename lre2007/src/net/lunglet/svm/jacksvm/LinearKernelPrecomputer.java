package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.blas.FloatDenseBLAS;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseUtils;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.util.BufferUtils;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.lunglet.hdf.Attribute;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSetCreatePropListBuilder;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.DataType;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.IntType;
import net.lunglet.hdf.SelectionOperator;
import net.lunglet.hdf.DataSetCreatePropListBuilder.FillTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class LinearKernelPrecomputer {
    private static long elementOffset(final long m, final long n) {
        if (m > n) {
            throw new IllegalArgumentException();
        }
        // calculate offset for upper triangular entry
        return m + (n + 1L) * n / 2L;
    }

    private final int bufferSize;

    private final H5File datah5;

    private final H5File kernelh5;

    private final Log log = LogFactory.getLog(LinearKernelPrecomputer.class);

    public LinearKernelPrecomputer(final H5File datah5, final H5File kernelh5) {
        this(datah5, kernelh5, 1000);
    }

    public LinearKernelPrecomputer(final H5File datah5, final H5File kernelh5, final int bufferSize) {
        this.datah5 = datah5;
        this.kernelh5 = kernelh5;
        this.bufferSize = bufferSize;
    }

    // TODO alternate between front to back and back to front iteration over
    // blocks to make optimal use of disk cache
    public void compute(final Set<String> names) {
        Map<Integer, DataVector> vecsMap = readDataVectors(names);
        List<DataVector> vecs = new ArrayList<DataVector>(vecsMap.values());
        if (vecs.size() == 0) {
            throw new IllegalArgumentException();
        }
        DataType dtype = FloatType.IEEE_F32LE;
        long gramdim = vecs.size() * (vecs.size() + 1L) / 2L;
        DataSpace gramspace = new DataSpace(gramdim);
        DataSetCreatePropListBuilder builder = new DataSetCreatePropListBuilder();
        builder.setFillTime(FillTime.NEVER);
        DataSet gramds = kernelh5.getRootGroup().createDataSet("kernel", dtype, gramspace, builder.build());
        gramspace.close();
        int[] order = new int[vecs.size()];
        // calculate parts of gram matrix
        int bufrows = vecs.get(0).length();
        Orientation orient = Orientation.COLUMN;
        Storage storage = Storage.DIRECT;
        FloatDenseMatrix a = new FloatDenseMatrix(bufrows, bufferSize, orient, storage);
        FloatDenseMatrix b = new FloatDenseMatrix(bufrows, bufferSize, orient, storage);
        FloatBuffer cbuf = BufferUtils.createFloatBuffer(bufferSize * bufferSize, storage);
        for (int i = 0; i < vecs.size(); i += a.columns()) {
            int j = Math.min(i + a.columns(), vecs.size());
            log.info("reading x");
            List<DataVector> xvecs = vecs.subList(i, j);
            for (int m = 0; m < xvecs.size(); m++) {
                xvecs.get(m).read(a.column(m));
                order[i + m] = xvecs.get(m).getIndex();
            }
            log.info("read x[" + i + ", " + j + "] done");
            FloatDenseMatrix x = FloatDenseUtils.subMatrixColumns(a, 0, xvecs.size());
            for (int k = i; k < vecs.size(); k += b.columns()) {
                int l = Math.min(k + b.columns(), vecs.size());
                final FloatDenseMatrix y;
                if (k == i) {
                    y = x;
                } else {
                    log.info("reading y");
                    List<DataVector> yvecs = vecs.subList(k, l);
                    for (int m = 0; m < yvecs.size(); m++) {
                        yvecs.get(m).read(b.column(m));
                    }
                    y = FloatDenseUtils.subMatrixColumns(b, 0, yvecs.size());
                    log.info("read y[" + k + ", " + l + "] done");
                }
                log.info("gemm");
                // TODO just take a view on an existing grampart here
                // TODO will require proper support for BLAS's leading dimension concept
                FloatDenseMatrix grampart = new FloatDenseMatrix(cbuf, x.columns(), y.columns(), 0, 1, orient);
                // TODO might want to do a syrk here when x==y
                FloatDenseBLAS.DEFAULT.gemm(1.0f, x.transpose(), y, 0.0f, grampart);
                log.info("gemm done");
                log.info("writing");
                for (int m = 0; m < grampart.columns(); m++) {
                    FloatDenseVector gramcol = grampart.column(m);
                    if (gramcol.stride() != 1) {
                        throw new AssertionError();
                    }
                    int gramcolidx = m + k;
                    DataSpace memSpace = new DataSpace(Math.min(gramcolidx - i + 1, grampart.rows()));
                    DataSpace fileSpace = gramds.getSpace();
                    long[] start = new long[]{elementOffset(i, gramcolidx)};
                    long[] count = new long[]{1L};
                    long[] block = new long[]{memSpace.getDim(0)};
                    fileSpace.selectHyperslab(SelectionOperator.SET, start, null, count, block);
                    gramds.write(gramcol.data(), dtype, memSpace, fileSpace);
                    fileSpace.close();
                    memSpace.close();
                }
                log.info("writing done");
            }
        }
        DataSpace space = new DataSpace(order.length);
        Attribute attr = gramds.createAttribute("order", IntType.STD_I32LE, space);
        attr.write(order);
        attr.close();
        space.close();
    }

    public Map<Integer, DataVector> readDataVectors(final Set<String> names) {
        Set<String> namesCopy = new HashSet<String>(names);
        Group root = datah5.getRootGroup();
        Map<Integer, DataVector> vecs = new HashMap<Integer, DataVector>();
        for (Group group : root.getGroups()) {
            for (DataSet ds : group.getDataSets()) {
                String name = ds.getName();
                if (!names.contains(name)) {
                    continue;
                }
                namesCopy.remove(name);
                DataSpace space = ds.getSpace();
                int[] indexes = new int[(int) space.getDim(0)];
                Attribute attr = ds.openAttribute("indexes");
                attr.read(indexes);
                attr.close();
                for (int i = 0; i < space.getDim(0); i++) {
                    vecs.put(indexes[i], new DataVector(indexes[i], ds, i));
                }
                space.close();
            }
            group.close();
        }
        if (namesCopy.size() != 0) {
            throw new AssertionError();
        }
        return vecs;
    }
}
