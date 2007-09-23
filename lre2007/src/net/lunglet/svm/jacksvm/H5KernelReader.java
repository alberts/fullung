package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.Storage;
import com.googlecode.array4j.packed.FloatPackedMatrix;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.Point;
import net.lunglet.hdf.SelectionOperator;

public final class H5KernelReader implements KernelReader {
    private static long elementOffset(final long m, final long n) {
        long i = Math.min(m, n);
        long j = Math.max(m, n);
        // calculate offset for upper triangular entry
        return i + (j + 1L) * j / 2L;
    }

    private final H5File kernelh5;

    private final Map<Integer, Integer> orderLut;

    public H5KernelReader(final H5File kernelh5) {
        this.kernelh5 = kernelh5;
        DataSet kernelds = openKernelDataSet();
        int[] order = kernelds.getIntArrayAttribute("order");
        this.orderLut = new HashMap<Integer, Integer>();
        for (int i = 0; i < order.length; i++) {
            orderLut.put(order[i], i);
        }
        kernelds.close();
    }

    private DataSet openKernelDataSet() {
        return kernelh5.getRootGroup().openDataSet("/kernel");
    }

    public float read(final int i, final int j) {
        DataSet kernelds = openKernelDataSet();
        float[] value = new float[1];
        DataSpace memSpace = new DataSpace(1);
        memSpace.selectElements(SelectionOperator.SET, new Point(0));
        DataSpace fileSpace = kernelds.getSpace();
        Point srcPoint = new Point(elementOffset(orderLut.get(i), orderLut.get(j)));
        fileSpace.selectElements(SelectionOperator.SET, srcPoint);
        kernelds.read(FloatBuffer.wrap(value), FloatType.IEEE_F32LE, memSpace, fileSpace);
        memSpace.close();
        fileSpace.close();
        kernelds.close();
        return value[0];
    }

    public FloatPackedMatrix read(final int[] indexes) {
        final int n = indexes.length;
        FloatPackedMatrix kernel = FloatPackedMatrix.createSymmetric(n, Storage.DIRECT);
        if (n == 0) {
            return kernel;
        }
        // TODO read a fixed number of points at a time if these lists get too large
        List<Point> srcPoints = new ArrayList<Point>();
        List<Point> destPoints = new ArrayList<Point>();
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                int posi = orderLut.get(indexes[i]);
                int posj = orderLut.get(indexes[j]);
                srcPoints.add(new Point(elementOffset(posi, posj)));
                destPoints.add(new Point(elementOffset(i, j)));
                if (srcPoints.size() >= 200000) {
                    System.out.println("reading");
                    read(srcPoints, destPoints, kernel);
                    srcPoints.clear();
                    destPoints.clear();
                }
            }
        }
        if (srcPoints.size() > 0) {
            read(srcPoints, destPoints, kernel);
        }
        return kernel;
    }

    private void read(final List<Point> srcPoints, final List<Point> destPoints, final FloatPackedMatrix kernel) {
        DataSet kernelds = openKernelDataSet();
        int n = kernel.rows();
        DataSpace memSpace = new DataSpace(n * (n + 1) / 2);
        memSpace.selectElements(SelectionOperator.SET, destPoints.toArray(new Point[0]));
        DataSpace fileSpace = kernelds.getSpace();
        fileSpace.selectElements(SelectionOperator.SET, srcPoints.toArray(new Point[0]));
        kernelds.read(kernel.data(), FloatType.IEEE_F32LE, memSpace, fileSpace);
        memSpace.close();
        fileSpace.close();
        kernelds.close();
    }
}
