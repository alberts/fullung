package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.FloatVector;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.lunglet.hdf.H5File;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJob;

public final class SvmTrainJob implements GridJob {
    private static final long serialVersionUID = 1L;

    private final String modelName;

    private final transient List<Handle2> localData;

    private final ArrayList<Handle3> networkData;

    public SvmTrainJob(final String modelName, final List<Handle2> trainData) {
        this.modelName = modelName;
        this.localData = Collections.unmodifiableList(new ArrayList<Handle2>(trainData));
        this.networkData = new ArrayList<Handle3>();
        for (Handle2 handle : trainData) {
            networkData.add(new Handle3(handle.getIndex(), handle.getLabel(), handle.getName()));
        }
    }

    public List<Handle2> getLocalData() {
        if (localData == null) {
            throw new IllegalStateException();
        }
        return localData;
    }

    @Override
    public void cancel() {
        throw new UnsupportedOperationException();
    }

    private static class KernelReaderHolder {
        private static final KernelReader INSTANCE;

        static {
            String filename = "G:/czngrams_kernel.h5";
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
        List<Handle2> remoteData = new ArrayList<Handle2>();
        for (final Handle3 handle : networkData) {
            remoteData.add(new Handle2() {
                @Override
                public FloatVector<?> getData() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int getIndex() {
                    return handle.getIndex();
                }

                @Override
                public String getLabel() {
                    return handle.getLabel();
                }

                @Override
                public String getName() {
                    return handle.getName();
                }

                @Override
                public List<Score> getScores() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void setScores(final List<Score> scores) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int compareTo(final Handle2 o) {
                    throw new UnsupportedOperationException();
                }
            });
        }
//        String filename = "G:/czngrams_kernel.h5";
//        System.out.println("reading kernel from " + filename);
//        H5File kernelh5 = new H5File(filename, H5File.H5F_ACC_RDONLY);
//        KernelReader kernelReader = new H5KernelReader2(kernelh5);
//        JackSVM2 svm = new JackSVM2(kernelReader);
        JackSVM2 svm = new JackSVM2(getKernelReader());
        svm.train(remoteData);
//        kernelh5.close();
        return new Object[]{modelName, svm};
    }
}
