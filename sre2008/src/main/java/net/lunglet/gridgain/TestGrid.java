package net.lunglet.gridgain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.lunglet.array4j.Order;
import net.lunglet.array4j.Storage;
import net.lunglet.array4j.blas.FloatDenseBLAS;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.array4j.matrix.util.FloatMatrixUtils;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridNode;
import org.gridgain.grid.GridTaskAdapter;
import org.gridgain.grid.logger.GridLogger;
import org.gridgain.grid.resources.GridLoggerResource;

public final class TestGrid {
    public static class TestJob implements GridJob {
        private static final Random rng = new Random(0);

        private static final long serialVersionUID = 1L;

        private final int i;

        @GridLoggerResource
        private GridLogger logger = null;

        public TestJob(final int i) {
            this.i = i;
        }

        @Override
        public void cancel() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Serializable execute() throws GridException {
            if (false && rng.nextInt(100) == 0) {
                throw new RuntimeException("BORK");
            }
            if (false) {
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    throw new GridException(e);
                }
            }
            logger.info("Hello " + i);
            return null;
        }

        public Serializable execute2() throws GridException {
            int n = 100;
            final int r;
            if (n < 100) {
                r = 5000;
            } else {
                r = 500;
            }
            float alpha = 1.0f;
            Order order = Order.COLUMN;
            Storage storage = Storage.DIRECT;
            FloatDenseMatrix a = DenseFactory.floatMatrix(n, n, order, storage);
            FloatDenseMatrix b = DenseFactory.floatMatrix(n, n, order, storage);
            float beta = 1.0f;
            FloatDenseMatrix c = DenseFactory.floatMatrix(n, n, order, storage);
            FloatMatrixUtils.fillRandom(a, rng);
            FloatMatrixUtils.fillRandom(b, rng);
            FloatMatrixUtils.fillRandom(c, rng);
            for (int i = 0; i < 10; i++) {
                FloatDenseBLAS.DEFAULT.gemm(alpha, a, b, beta, c);
            }
            long startTime = System.nanoTime();
            for (int i = 0; i < r; i++) {
                FloatDenseBLAS.DEFAULT.gemm(alpha, a, b, beta, c);
            }
            long t = System.nanoTime() - startTime;
            int f = 2 * (n + 1) * n * n;
            double mfs = f / (t / 1000.0) * r;
            System.out.println(String.format("n = %d, mfs = %f", n, mfs));
            return null;
        }
    }

    public static class TestTask extends GridTaskAdapter<TestJob, Object> {
        private static final long serialVersionUID = 1L;

        private final Random rng = new Random();

        @Override
        public Map<? extends GridJob, GridNode> map(List<GridNode> subgrid, TestJob arg) throws GridException {
            Map<TestJob, GridNode> map = new HashMap<TestJob, GridNode>();
            map.put(arg, subgrid.get(rng.nextInt(subgrid.size())));
            return map;
        }

        @Override
        public Object reduce(List<GridJobResult> results) throws GridException {
            return null;
        }
    }

    public static void main(final String[] args) throws Exception {
        List<TestJob> tasks = new ArrayList<TestJob>();
        for (int i = 0; i < 1000; i++) {
            tasks.add(new TestJob(i));
        }
        if (true) {
            new DefaultGrid<TestJob, Object>(TestTask.class, tasks, null).run();
        } else {
            new LocalGrid<TestJob, Object>(TestTask.class, tasks, null).run();
        }
    }
}
