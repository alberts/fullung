package net.lunglet.svm.jacksvm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridNode;
import org.gridgain.grid.GridTaskAdapter;

public final class SvmTrainTask extends GridTaskAdapter<SvmTrainJob> {
    private static final long serialVersionUID = 1L;

    private final Random rng = new Random();
    
    @Override
    public Map<? extends GridJob, GridNode> map(final List<GridNode> subgrid, final SvmTrainJob job) throws GridException {
        if (subgrid == null || subgrid.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Map<GridJob, GridNode> map = new HashMap<GridJob, GridNode>(1);
        map.put(job, subgrid.get(rng.nextInt(subgrid.size())));
        return map;
    }

    @Override
    public Object reduce(final List<GridJobResult> results) throws GridException {
        for (GridJobResult result : results) {
            if (result.getException() != null) {
                System.out.println(result.getException());
                continue;
            }
            Object[] data = (Object[]) result.getData();
            String modelname = (String) data[0];
            JackSVM2 svm = (JackSVM2) data[1];
        }
        return null;
    }
}
