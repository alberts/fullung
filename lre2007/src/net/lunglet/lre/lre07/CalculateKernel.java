package net.lunglet.lre.lre07;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import net.lunglet.hdf.H5File;
import net.lunglet.lre.lre07.CrossValidationSplits.SplitEntry;
import net.lunglet.svm.jacksvm.LinearKernelPrecomputer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class CalculateKernel {
    private static final Log LOG = LogFactory.getLog(CalculateKernel.class);

    public static void main(final String[] args) throws IOException {
        LOG.info("starting kernel calculator");
        H5File datah5 = new H5File(new File(Constants.WORKING_DIRECTORY, "czngrams.h5"), H5File.H5F_ACC_RDONLY);
        H5File kernelh5 = new H5File(new File(Constants.WORKING_DIRECTORY, "czkernel.h5"));
        CrossValidationSplits cvsplits = Constants.CVSPLITS;
        Set<SplitEntry> frontend = cvsplits.getSplit("frontend");
        LOG.info("frontend splits contain " + frontend.size() + " supervectors");
        int bufferSize = 1000;
        LinearKernelPrecomputer kernelComputer = new LinearKernelPrecomputer(datah5, kernelh5, bufferSize);
        kernelComputer.compute(frontend);
        kernelh5.close();
        datah5.close();
        LOG.info("kernel calculator done");
    }
}
