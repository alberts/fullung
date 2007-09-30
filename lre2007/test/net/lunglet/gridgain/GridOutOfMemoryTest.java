package net.lunglet.gridgain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.junit.Test;

public final class GridOutOfMemoryTest {
    public static class OOMTask extends GridTaskAdapter<Void> {
        private static final long serialVersionUID = 1L;

        @Override
        public Map<? extends GridJob, GridNode> map(List<GridNode> subgrid, Void arg) throws GridException {
            Map<GridJob, GridNode> map = new HashMap<GridJob, GridNode>(subgrid.size());
            for (GridNode node : subgrid) {
                map.put(new GridJob() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void cancel() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public Serializable execute() throws GridException {
                        List<Object> objs = new ArrayList<Object>();
                        while (true) {
                            objs.add(new byte[1024 * 1024]);
                        }
                    }
                }, node);
            }
            return map;
        }

        @Override
        public Object reduce(List<GridJobResult> results) throws GridException {
            return null;
        }
    }

    @Test
    public void test() throws GridException {
        GridBasicTopologySpi topSpi = new GridBasicTopologySpi();
        topSpi.setLocalNode(false);
        topSpi.setRemoteNodes(true);
        GridConfigurationAdapter cfg = new GridConfigurationAdapter();
        cfg.setTopologySpi(topSpi);
        final Grid grid = GridFactory.start(cfg);
        GridTaskFuture future = grid.execute(OOMTask.class.getName(), null);
        future.get();
        GridFactory.stop(true);
    }
}
