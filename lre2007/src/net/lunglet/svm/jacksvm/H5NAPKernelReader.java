package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.blas.FloatDenseBLAS;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseUtils;
import com.googlecode.array4j.io.HDFReader;
import com.googlecode.array4j.packed.FloatPackedMatrix;
import com.googlecode.array4j.util.AssertUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.H5File;

public final class H5NAPKernelReader {
    /** Original (un-NAPed) kernel. */
    private final FloatPackedMatrix kernel0;

    /** Data indexes. */
    private final int[] indexes;

    /**
     * LUT that maps from a data index to a position in the kernel matrix.
     */
    private final int[] indexToPositionMap;

    public H5NAPKernelReader(final H5File kernelh5) {
        DataSet orderds = kernelh5.getRootGroup().openDataSet("/order");
        DataSpace space = orderds.getSpace();
        this.indexes = new int[(int) space.getDim(0)];
        space.close();
        orderds.read(indexes);
        orderds.close();
        int maxIndex = -1;
        for (int i : indexes) {
            maxIndex = i > maxIndex ? i : maxIndex;
        }
        this.indexToPositionMap = new int[maxIndex + 1];
        Arrays.fill(indexToPositionMap, -1);
        for (int i = 0; i < indexes.length; i++) {
            indexToPositionMap[indexes[i]] = i;
        }
        // use heap storage to avoid large direct memory allocations
        this.kernel0 = FloatPackedMatrix.createSymmetric(indexes.length, Storage.HEAP);
        new HDFReader(kernelh5).read("/kernel", kernel0);
    }

    private FloatDenseMatrix napTransTimesData(final FloatDenseMatrix nap, final FloatDenseMatrix data) {
        FloatDenseMatrix napTrans = nap.transpose();
        FloatDenseMatrix c = new FloatDenseMatrix(napTrans.rows(), data.columns(), Orientation.COLUMN, Storage.DIRECT);
        FloatDenseBLAS.DEFAULT.gemm(1.0f, napTrans, data, 0.0f, c);
        return c;
    }

    public KernelReader getKernelReader(final List<Handle2> handles, final FloatDenseMatrix nap) {
        final Orientation orient = Orientation.COLUMN;
        final Storage storage = Storage.DIRECT;
        final FloatDenseMatrix h = new FloatDenseMatrix(nap.columns(), handles.size(), orient, storage);
        FloatDenseMatrix dataBuf = new FloatDenseMatrix(nap.rows(), 1000, orient, storage);
        Map<Integer, Handle2> indexedData = new HashMap<Integer, Handle2>();
        for (Handle2 handle : handles) {
            indexedData.put(handle.getIndex(), handle);
        }
        System.out.println("indexedData size = " + indexedData.size());
        System.out.println("indexes.length = " + indexes.length);
        int hcol = 0;
        int dataBufPos = 0;
        for (int index : indexes) {
            if (!indexedData.containsKey(index)) {
                continue;
            }
            Handle2 handle = indexedData.get(index);
            handle.getData(dataBuf.column(dataBufPos++));
            if (dataBufPos == dataBuf.columns()) {
                FloatDenseMatrix hpart = napTransTimesData(nap, dataBuf);
                for (int j = 0; j < hpart.columns(); j++) {
                    h.setColumn(hcol++, hpart.column(j));
                }
                System.out.println("hcol = " + hcol);
                dataBufPos = 0;
            }
        }
        if (dataBufPos > 0) {
            dataBuf = FloatDenseUtils.subMatrixColumns(dataBuf, 0, dataBufPos);
            FloatDenseMatrix hpart = napTransTimesData(nap, dataBuf);
            for (int j = 0; j < hpart.columns(); j++) {
                h.setColumn(hcol++, hpart.column(j));
            }
        }
        // if this assert fails, the kernel probably needs to be recalculated to
        // match the data
        AssertUtils.assertEquals(h.columns(), hcol);
        // TODO this is wrong if we're dealing with more than one test split,
        // because then kernel0 is bigger than napDelta
        int kernelDim = kernel0.rows();
        final FloatDenseMatrix napDelta = new FloatDenseMatrix(kernelDim, kernelDim, orient, storage);
        FloatDenseBLAS.DEFAULT.gemm(1.0f, h.transpose(), h, 0.0f, napDelta);
        return new KernelReader() {
            /**
             * @param i data index i
             * @param j data index j
             */
            @Override
            public float read(final int i, final int j) {
                int m = indexToPositionMap[i];
                int n = indexToPositionMap[j];
                return kernel0.get(m, n) - napDelta.get(m, n);
            }

            @Override
            public FloatPackedMatrix read(int[] indexes) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public float read(final int i, final int j) {
        return 0.0f;
    }

    public FloatPackedMatrix read(final int[] indexes) {
        throw new UnsupportedOperationException();
    }
}
