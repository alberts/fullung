package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.Storage;
import com.googlecode.array4j.io.HDFReader;
import com.googlecode.array4j.packed.FloatPackedMatrix;
import java.util.HashMap;
import java.util.Map;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.H5File;

/**
 * Kernel reader that reads the whole kernel up front.
 * @author albert
 */
public final class H5KernelReader2 implements KernelReader {
    private final FloatPackedMatrix kernel;

    private final H5File kernelh5;

    private final Map<Integer, Integer> orderLut;

    public H5KernelReader2(final H5File kernelh5) {
        this.kernelh5 = kernelh5;
        DataSet kernelds = openKernelDataSet();
        int[] order = kernelds.getIntArrayAttribute("order");
        this.orderLut = new HashMap<Integer, Integer>();
        for (int i = 0; i < order.length; i++) {
            orderLut.put(order[i], i);
        }
        this.kernel = FloatPackedMatrix.createSymmetric(order.length, Storage.DIRECT);
        new HDFReader(kernelh5).read(kernel, "/kernel");
        kernelds.close();

    }

    private DataSet openKernelDataSet() {
        return kernelh5.getRootGroup().openDataSet("/kernel");
    }

    public float read(final int i, final int j) {
        return kernel.get(orderLut.get(i), orderLut.get(j));
    }

    public FloatPackedMatrix read(final int[] indexes) {
        int dim = indexes.length;
        FloatPackedMatrix subkernel = FloatPackedMatrix.createSymmetric(dim, Storage.DIRECT);
        for (int i = 0; i < indexes.length; i++) {
            for (int j = i; j < indexes.length; j++) {
                subkernel.set(i, j, read(indexes[i], indexes[j]));
            }
        }
        return subkernel;
    }
}
