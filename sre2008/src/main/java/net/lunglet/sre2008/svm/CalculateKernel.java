package net.lunglet.sre2008.svm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.lunglet.array4j.blas.FloatDenseBLAS;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.array4j.matrix.util.FloatMatrixUtils;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSetCreatePropListBuilder;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.DataType;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.SelectionOperator;
import net.lunglet.hdf.DataSetCreatePropListBuilder.FillTime;
import net.lunglet.io.HDFReader;
import net.lunglet.sre2008.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CalculateKernel {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalculateKernel.class);

    private static DataSet createKernelDataSet(final H5File kernelh5, final long size) {
        DataType dtype = FloatType.IEEE_F32LE;
        long dim = size * (size + 1L) / 2L;
        DataSpace space = new DataSpace(dim);
        DataSetCreatePropListBuilder builder = new DataSetCreatePropListBuilder();
        builder.setFillTime(FillTime.NEVER);
        DataSet kernelds = kernelh5.getRootGroup().createDataSet("kernel", dtype, space, builder.build());
        space.close();
        return kernelds;
    }

    private static void writeKernelBlock(final DataSet kernelds, final FloatDenseMatrix kernelBlock,
            final int firstBlockRow, final int firstBlockColumn) {
        DataType dtype = FloatType.IEEE_F32LE;
        for (int m = 0; m < kernelBlock.columns(); m++) {
            FloatDenseVector col = kernelBlock.column(m);
            if (col.stride() != 1) {
                throw new AssertionError();
            }
            int blockColumn = firstBlockColumn + m;
            DataSpace memSpace = new DataSpace(Math.min(blockColumn - firstBlockRow + 1, kernelBlock.rows()));
            DataSpace fileSpace = kernelds.getSpace();
            long[] start = new long[]{elementOffset(firstBlockRow, blockColumn)};
            long[] count = new long[]{1L};
            long[] block = new long[]{memSpace.getDim(0)};
            fileSpace.selectHyperslab(SelectionOperator.SET, start, null, count, block);
            kernelds.write(col.data(), dtype, memSpace, fileSpace);
            fileSpace.close();
            memSpace.close();
        }
    }

    private static void readBlock(final H5File dataFile, final List<String> block, final FloatDenseMatrix buf) {
        if (block.size() > buf.columns()) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < block.size(); i++) {
            String name = block.get(i);
            new HDFReader(dataFile).read(name, buf.column(i));
            if (!FloatMatrixUtils.isAllFinite(buf.column(i))) {
                LOGGER.error("{} contains invalid values", name);
                throw new RuntimeException();
            }
        }
    }

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

    public static void main(final String[] args) {
        final int bufferColumns = 1790;
        final int bufferRows = 512 * 38;

        LOGGER.info("starting kernel calculator with " + bufferColumns + " buffer columns");
        H5File datah5 = new H5File(Constants.SVM_BACKGROUND_GMM);
        H5File kernelh5 = new H5File(Constants.KERNEL_FILE, H5File.H5F_ACC_TRUNC);

        LOGGER.info("reading data");
        // TODO read names from a list instead of using all names so that we can
        // combine SRE04 UBM data and NAP data in a single file
        List<String> data = getNames(datah5);

        LOGGER.info("creating kernel dataset");
        DataSet kernelds = createKernelDataSet(kernelh5, data.size());

        List<List<String>> blocks = new ArrayList<List<String>>();
        for (int i = 0; i < data.size(); i += bufferColumns) {
            List<String> block = data.subList(i, Math.min(data.size(), i + bufferColumns));
            blocks.add(block);
        }
        LOGGER.info(data.size() + " data elements split into " + blocks.size() + " blocks");
        int[] blockColumns = new int[blocks.size()];
        for (int i = 1; i < blockColumns.length; i++) {
            blockColumns[i] = blockColumns[i - 1] + blocks.get(i - 1).size();
        }
        FloatDenseMatrix a = DenseFactory.floatColumnDirect(bufferRows, bufferColumns);
        FloatDenseMatrix b = DenseFactory.floatColumnDirect(bufferRows, bufferColumns);
        int blockInA = -1;
        int blockInB = -1;
        for (int[] ij : new BlockPairs(blocks.size())) {
            final int i = ij[0];
            final int j = ij[1];
            LOGGER.info("before: i=" + i + ", j=" + j + ", blockInA=" + blockInA + ", blockInB=" + blockInB);
            final FloatDenseMatrix x;
            final FloatDenseMatrix y;
            if (i == j) {
                List<String> block = blocks.get(i);
                final FloatDenseMatrix z;
                if (i == blockInA) {
                    z = a;
                } else if (i == blockInB) {
                    z = b;
                } else {
                    LOGGER.info("read block " + i + " into buffer a");
                    readBlock(datah5, block, a);
                    z = a;
                    blockInA = i;
                }
                x = FloatMatrixUtils.subMatrixColumns(z, 0, block.size());
                y = x;
            } else {
                if (i == blockInA || i == blockInB) {
                    List<String> block = blocks.get(j);
                    if (i == blockInA) {
                        LOGGER.info("read block " + j + " into buffer b");
                        readBlock(datah5, block, b);
                        blockInB = j;
                    } else {
                        LOGGER.info("read block " + j + " into buffer a");
                        readBlock(datah5, block, a);
                        blockInA = j;
                    }
                } else {
                    List<String> block = blocks.get(i);
                    if (j == blockInA) {
                        LOGGER.info("read block " + i + " into buffer b");
                        readBlock(datah5, block, b);
                        blockInB = i;
                    } else {
                        LOGGER.info("read block " + i + " into buffer a");
                        readBlock(datah5, block, a);
                        blockInA = i;
                    }
                }
                // ensure that block with the lowest index is passed as the
                // first argument of the gemm, and restrict the columns of the
                // matrices to the number of vectors in the block
                if (blockInA <= blockInB) {
                    x = FloatMatrixUtils.subMatrixColumns(a, 0, blocks.get(blockInA).size());
                    y = FloatMatrixUtils.subMatrixColumns(b, 0, blocks.get(blockInB).size());
                } else {
                    x = FloatMatrixUtils.subMatrixColumns(b, 0, blocks.get(blockInB).size());
                    y = FloatMatrixUtils.subMatrixColumns(a, 0, blocks.get(blockInA).size());
                }
            }
            LOGGER.info("after: i=" + i + ", j=" + j + ", blockInA=" + blockInA + ", blockInB=" + blockInB);
            FloatDenseMatrix kernelBlock = DenseFactory.floatColumnDirect(x.columns(), y.columns());
            LOGGER.info("calling gemm");
            FloatDenseBLAS.DEFAULT.gemm(1.0f, x.transpose(), y, 0.0f, kernelBlock);
            LOGGER.info("gemm done");
            writeKernelBlock(kernelds, kernelBlock, blockColumns[i], blockColumns[j]);
        }
        kernelds.close();
        kernelh5.close();
        datah5.close();
        LOGGER.info("kernel calculation done");
    }

    private static long elementOffset(final long m, final long n) {
        if (m > n) {
            throw new IllegalArgumentException();
        }
        // calculate offset for upper triangular entry
        return m + (n + 1L) * n / 2L;
    }

    private static class BlockPairs implements Iterable<int[]> {
        private final int blocks;

        private final int maxiter;

        public BlockPairs(final int blocks) {
            this.blocks = blocks;
            this.maxiter = blocks * (blocks + 1) / 2 - 1;
        }

        @Override
        public Iterator<int[]> iterator() {
            return new Iterator<int[]>() {
                private int iter = 0;

                private int i = 0;

                private int j = 0;

                private int inci = 1;

                private int incj = 1;

                @Override
                public boolean hasNext() {
                    return iter <= maxiter;
                }

                @Override
                public int[] next() {
                    if (!hasNext()) {
                        return null;
                    }
                    int[] ij = new int[]{i, j};
                    if ((iter & 1) == 0) {
                        j += incj;
                        if (j == blocks) {
                            j -= 1;
                            i -= 2;
                            incj = -incj;
                            inci = -inci;
                        }
                    } else {
                        i += inci;
                        if (i == -1) {
                            i += 1;
                            j += 2;
                            incj = -incj;
                            inci = -inci;
                        }
                    }
                    iter++;
                    return ij;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
}
