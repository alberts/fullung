package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.FloatVector;
import com.googlecode.array4j.io.HDFWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.H5Library;
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
        for (GridJobResult result : results) {
            if (result.getException() != null) {
                System.out.println(result.getException());
                continue;
            }
            Object[] data = (Object[]) result.getData();
            String modelName = (String) data[0];
            SvmTrainJob job = (SvmTrainJob) result.getJob();
            List<Handle2> trainData = job.getLocalData();
            final Map<Integer, Handle2> indexDataMap = new HashMap<Integer, Handle2>();
            for (Handle2 handle : trainData) {
                indexDataMap.put(handle.getIndex(), handle);
            }
            JackSVM2 svm = (JackSVM2) data[1];
            svm.setTrainData(trainData);
            for (SvmNode node : svm.getSvmNodes()) {
                final Handle2 handle = indexDataMap.get(node.getIndex());
                node.setHandle(new Handle() {
                    @Override
                    public FloatVector<?> getData() {
                        return handle.getData();
                    }

                    @Override
                    public int getLabel() {
                        throw new UnsupportedOperationException();
                    }
                });
            }
            System.out.println("reducing in thread " + Thread.currentThread().getId());
            synchronized (H5Library.class) {
                svm.compact();
                H5File modelsh5 = new H5File("G:/czmodels.h5", H5File.H5F_ACC_RDWR);
                HDFWriter writer = new HDFWriter(modelsh5);
                writer.write(modelName, svm.getModels());
                writer.close();
            }
            System.out.println("training of " + modelName + " is done");
        }
        return null;
    }
}
