package net.lunglet.svm.jacksvm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

import net.lunglet.hdf.H5Library;
import net.lunglet.svm.Handle;
import net.lunglet.svm.SvmNode;

import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridNode;
import org.gridgain.grid.GridTaskAdapter;

import com.googlecode.array4j.FloatVector;

// TODO figure out which thread calls reduce, etc.

public final class SvmTrainTask extends GridTaskAdapter<SvmTrainJob> {
    private static final long serialVersionUID = 1L;

    private final Random rng = new Random();

    @Override
    public Map<? extends GridJob, GridNode> map(final List<GridNode> subgrid, final SvmTrainJob job)
            throws GridException {
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
            // TODO can probably remove this when we fix HDF
            synchronized (H5Library.class) {
                svm.compact();
            }
            try {
                String fileName = modelName + ".dat.gz";
                ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(fileName)));
                oos.writeObject(svm);
                oos.close();
            } catch (IOException e) {
                throw new GridException(null, e);
            }
            System.out.println("training of " + modelName + " is done");
        }
        return null;
    }
}
