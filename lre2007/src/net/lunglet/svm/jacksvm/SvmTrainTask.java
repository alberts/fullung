package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.FloatVector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.lunglet.svm.Handle;
import net.lunglet.svm.SvmNode;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridNode;
import org.gridgain.grid.GridTaskAdapter;

public final class SvmTrainTask extends GridTaskAdapter<SvmTrainJob> {
    private static final long serialVersionUID = 1L;

    private final Random rng = new Random();

    @Override
    public Map<? extends GridJob, GridNode> map(final List<GridNode> subgrid, final SvmTrainJob job)
            throws GridException {
        if (subgrid == null || subgrid.isEmpty()) {
            throw new IllegalArgumentException();
        }
        // make it more likely that a node will be chosen if it has more processors
        List<GridNode> procgrid = new ArrayList<GridNode>();
        for (GridNode node : subgrid) {
            String numproc = (String) node.getAttribute("NUMBER_OF_PROCESSORS");
            if (numproc != null) {
                for (int i = 0; i < Integer.valueOf(numproc); i++) {
                    procgrid.add(node);
                }
            } else {
                procgrid.add(node);
            }
        }
        Map<GridJob, GridNode> map = new HashMap<GridJob, GridNode>(1);
        GridNode node = procgrid.get(rng.nextInt(procgrid.size()));
        System.out.println("assigning task to " + node.getPhysicalAddress());
        map.put(job, node);
        return map;
    }

    @Override
    public Object reduce(final List<GridJobResult> results) throws GridException {
        if (results.size() != 1) {
            throw new GridException("expected exactly 1 result");
        }
        GridJobResult result = results.get(0);
        if (result.getException() != null) {
            System.out.println(result.getException());
            return null;
        }
        Object[] data = (Object[]) result.getData();
        SvmTrainJob job = (SvmTrainJob) result.getJob();
        final List<Handle2> trainData = job.getLocalData();
        JackSVM2 svm = (JackSVM2) data[1];
        for (final SvmNode node : svm.getSvmNodes()) {
            final Handle2 handle = trainData.get(node.getIndex());
            node.setHandle(new Handle() {
                @Override
                public FloatVector<?> getData() {
                    return handle.getData();
                }

                @Override
                public int getLabel() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int getIndex() {
                    return handle.getIndex();
                }
            });
        }
        return result.getData();
    }
}
