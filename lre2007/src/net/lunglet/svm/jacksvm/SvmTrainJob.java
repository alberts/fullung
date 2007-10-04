package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.FloatVector;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import net.lunglet.hdf.H5File;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;

public final class SvmTrainJob implements GridJob {
    private static final long serialVersionUID = 1L;

    private final String modelName;

    private final transient List<Handle2> localTrainData;
    
    private final List<Handle2> remoteTrainData;

    public SvmTrainJob(final String modelName, final List<Handle2> trainData) {
        this.modelName = modelName;
        this.localTrainData = trainData;
        this.remoteTrainData = new ArrayList<Handle2>(trainData.size());
        for (Handle2 handle : trainData) {
            this.remoteTrainData.add(new AbstractHandle2(handle.getName(), handle.getIndex(), handle.getLabel()) {
                private static final long serialVersionUID = 1L;

                @Override
                public FloatVector<?> getData() {
                    throw new UnsupportedOperationException();
                }
            });
        }
    }
    
    public List<Handle2> getLocalData() {
        return localTrainData;
    }

    @Override
    public void cancel() {
        throw new UnsupportedOperationException();
    }

    private static class KernelReaderHolder {
        private static final KernelReader INSTANCE;

        static {
            String filename = "E:/albert/hungrams_kernel.h5";
            System.out.println("reading kernel from " + filename);
            // TODO introduce a net.lunglet.datadir property for finding this
            // kind of file that has been manually distributed to all the nodes
            H5File kernelh5 = new H5File(filename, H5File.H5F_ACC_RDONLY);
            INSTANCE = new H5KernelReader2(kernelh5);
            kernelh5.close();
        }
    }

    private static KernelReader getKernelReader() {
        return KernelReaderHolder.INSTANCE;
    }

    @Override
    public Serializable execute() throws GridException {
        System.out.println("training " + modelName);
        JackSVM2 svm = new JackSVM2(getKernelReader());
        svm.train(remoteTrainData);
        return new Object[]{modelName, svm};
    }
}
