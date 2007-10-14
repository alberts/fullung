package net.lunglet.lre.lre07;

import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.blas.FloatDenseBLAS;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseUtils;
import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.util.BufferUtils;
import java.io.File;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.lunglet.hdf.Attribute;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSetCreatePropListBuilder;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.DataType;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.IntType;
import net.lunglet.hdf.SelectionOperator;
import net.lunglet.hdf.DataSetCreatePropListBuilder.FillTime;
import net.lunglet.lre.lre07.CrossValidationSplits.SplitEntry;
import net.lunglet.svm.jacksvm.Handle2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class CalculateKernel2 {
    private static final Log LOG = LogFactory.getLog(CalculateKernel2.class);

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

    private static void readBlock(final Map<String, Handle2> dataMap, final List<SplitEntry> block,
            final FloatDenseMatrix buf) {
        if (block.size() > buf.columns()) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < block.size(); i++) {
            dataMap.get(block.get(i).getName()).getData(buf.column(i));
        }
    }

    public static void main(final String[] args) {
        final int bufferColumns = 2000;
        LOG.info("kernel calculator starting with " + bufferColumns + " buffer columns");
        H5File datah5 = new H5File(new File(Constants.WORKING_DIRECTORY, "czngrams.h5"), H5File.H5F_ACC_RDONLY);
        H5File kernelh5 = new H5File(new File(Constants.WORKING_DIRECTORY, "czkernel.h5"));
        CrossValidationSplits cvsplits = Constants.CVSPLITS;
        List<SplitEntry> data = new ArrayList<SplitEntry>(cvsplits.getSplit("frontend"));
        if (data.size() == 0) {
            throw new AssertionError();
        }
        LOG.info("reading data map");
        // get map of handles that discard their data after every read
        Map<String, Handle2> dataMap = cvsplits.getDataMap("frontend", datah5);
        Collections.sort(data);
        LOG.info("creating kernel dataset");
        DataSet kernelds = createKernelDataSet(kernelh5, data.size());
        List<List<SplitEntry>> blocks = new ArrayList<List<SplitEntry>>();
        for (int i = 0; i < data.size(); i += bufferColumns) {
            List<SplitEntry> block = data.subList(i, Math.min(data.size(), i + bufferColumns));
            blocks.add(block);
        }
        LOG.info("data split into " + blocks.size() + " blocks");
        int[] blockColumns = new int[blocks.size()];
        for (int i = 1; i < blockColumns.length; i++) {
            blockColumns[i] = blocks.get(i - 1).size();
        }
        int bufferRows = dataMap.get(data.get(0).getName()).getData().length();
        Orientation orient = Orientation.COLUMN;
        Storage storage = Storage.DIRECT;
        FloatDenseMatrix a = new FloatDenseMatrix(bufferRows, bufferColumns, orient, storage);
        FloatDenseMatrix b = new FloatDenseMatrix(bufferRows, bufferColumns, orient, storage);
        int cbufSize = bufferColumns * bufferColumns;
        FloatBuffer cbuf = BufferUtils.createFloatBuffer(cbufSize, storage);
        int blockInA = -1;
        int blockInB = -1;
        for (int[] ij : new BlockPairs(blocks.size())) {
            final int i = ij[0];
            final int j = ij[1];
            LOG.info("before: i=" + i + ", j=" + j + ", blockInA=" + blockInA + ", blockInB=" + blockInB);
            final FloatDenseMatrix x;
            final FloatDenseMatrix y;
            if (i == j) {
                List<SplitEntry> block = blocks.get(i);
                final FloatDenseMatrix z;
                if (i == blockInA) {
                    z = a;
                } else if (i == blockInB) {
                    z = b;
                } else {
                    LOG.info("read block " + i + " into buffer a");
                    readBlock(dataMap, block, a);
                    z = a;
                    blockInA = i;
                }
                x = FloatDenseUtils.subMatrixColumns(z, 0, block.size());
                y = x;
            } else {
                if (i == blockInA || i == blockInB) {
                    List<SplitEntry> block = blocks.get(j);
                    if (i == blockInA) {
                        LOG.info("read block " + j + " into buffer b");
                        readBlock(dataMap, block, b);
                        blockInB = j;
                    } else {
                        LOG.info("read block " + j + " into buffer a");
                        readBlock(dataMap, block, a);
                        blockInA = j;
                    }
                } else {
                    List<SplitEntry> block = blocks.get(i);
                    if (j == blockInA) {
                        LOG.info("read block " + i + " into buffer b");
                        readBlock(dataMap, block, b);
                        blockInB = i;
                    } else {
                        LOG.info("read block " + i + " into buffer a");
                        readBlock(dataMap, block, a);
                        blockInA = i;
                    }
                }
                x = FloatDenseUtils.subMatrixColumns(a, 0, blocks.get(blockInA).size());
                y = FloatDenseUtils.subMatrixColumns(b, 0, blocks.get(blockInB).size());
            }
            LOG.info("after: i=" + i + ", j=" + j + ", blockInA=" + blockInA + ", blockInB=" + blockInB);
            FloatDenseMatrix kernelBlock = new FloatDenseMatrix(cbuf, x.columns(), y.columns(), 0, 1, orient);
            LOG.info("calling gemm");
            FloatDenseBLAS.DEFAULT.gemm(1.0f, x.transpose(), y, 0.0f, kernelBlock);
            LOG.info("gemm done");
            writeKernelBlock(kernelds, kernelBlock, blockColumns[i], blockColumns[j]);
        }
        int[] order = new int[data.size()];
        for (int i = 0; i < order.length; i++) {
            order[i] = dataMap.get(data.get(i).getName()).getIndex();
        }
        DataSpace space = new DataSpace(order.length);
        Attribute attr = kernelds.createAttribute("order", IntType.STD_I32LE, space);
        attr.write(order);
        attr.close();
        kernelds.close();
        kernelh5.close();
        datah5.close();
        LOG.info("kernel calculation done");
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
