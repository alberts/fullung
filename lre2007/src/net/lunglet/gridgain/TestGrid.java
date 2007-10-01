package net.lunglet.gridgain;

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
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.lunglet.hdf.FileAccessPropList;
import net.lunglet.hdf.FileAccessPropListBuilder;
import net.lunglet.hdf.FileCreatePropList;
import net.lunglet.hdf.H5File;
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
            System.out.println("doing hdf stuff");
            FileCreatePropList fcpl = FileCreatePropList.DEFAULT;
            FileAccessPropList fapl = new FileAccessPropListBuilder().setCore(1024, false).build();
            String name = UUID.randomUUID().toString();
            H5File h5 = new H5File(name, fcpl, fapl);
            fapl.close();
            h5.close();
            System.out.println("doing mkl stuff");
            FloatDenseMatrix x = new FloatDenseMatrix(1000, 1000, Orientation.COLUMN, Storage.DIRECT);
            FloatDenseBLAS.DEFAULT.gemm(1.0f, x, x, 1.0f, x);
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
            for (int i = 0; i < 50; i++) {
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
