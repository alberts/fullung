package net.lunglet.gridgain;

import com.googlecode.array4j.FloatMatrixUtils;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.blas.FloatDenseBLAS;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridNode;
import org.gridgain.grid.GridTaskAdapter;
import org.gridgain.grid.GridTaskFuture;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class TestGrid {
    public static class TestTask extends GridTaskAdapter<TestJob> {
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

    public static class TestJob implements GridJob {
        private static final long serialVersionUID = 1L;

        @Override
        public void cancel() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Serializable execute() throws GridException {
//            System.out.println("doing hdf stuff");
//            FileCreatePropList fcpl = FileCreatePropList.DEFAULT;
//            FileAccessPropList fapl = new FileAccessPropListBuilder().setCore(1024, false).build();
//            String name = UUID.randomUUID().toString();
//            H5File h5 = new H5File(name, fcpl, fapl);
//            fapl.close();
//            h5.close();
//            System.out.println("doing mkl stuff");
//            FloatDenseMatrix x = new FloatDenseMatrix(1000, 1000, Orientation.COLUMN, Storage.DIRECT);
//            FloatDenseBLAS.DEFAULT.gemm(1.0f, x, x, 1.0f, x);
//            return null;
            Random rng = new Random(0);
            for (int n = 50; n < 751; n += 10) {
                final int r;
                if (n < 100) {
                    r = 5000;
                } else {
                    r = 500;
                }
                float alpha = 1.0f;
                Orientation orient = Orientation.COLUMN;
                Storage storage = Storage.DIRECT;
                FloatDenseMatrix a = new FloatDenseMatrix(n, n, orient, storage);
                FloatDenseMatrix b = new FloatDenseMatrix(n, n, orient, storage);
                float beta = 1.0f;
                FloatDenseMatrix c = new FloatDenseMatrix(n, n, orient, storage);
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
            }
            return null;
        }
    }

    public static void main(final String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        GridConfigurationAdapter cfg = new GridConfigurationAdapter();
        GridBasicTopologySpi topologySpi = new GridBasicTopologySpi();
        topologySpi.setLocalNode(false);
        topologySpi.setRemoteNodes(true);
        cfg.setPeerClassLoadingEnabled(true);
        cfg.setTopologySpi(topologySpi);
        cfg.setExecutorService(executorService);
        try {
            final Grid grid = GridFactory.start(cfg);
            List<GridTaskFuture> futures = new ArrayList<GridTaskFuture>();
            for (int i = 0; i < 1; i++) {
                GridTaskFuture future = grid.execute(TestTask.class.getName(), new TestJob());
                futures.add(future);
            }
            for (GridTaskFuture future : futures) {
                future.get();
            }
        } finally {
            GridFactory.stop(true);
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
    }
}
