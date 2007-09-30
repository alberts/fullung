package net.lunglet.svm.jacksvm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.DataSetCreatePropListBuilder;
import net.lunglet.hdf.DataSpace;
import net.lunglet.hdf.DataType;
import net.lunglet.hdf.FloatType;
import net.lunglet.hdf.H5File;
import net.lunglet.hdf.DataSetCreatePropListBuilder.FillTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class LinearKernelPrecomputer2 {
    private final H5File datah5;

    private final H5File kernelh5;

    private final Log log = LogFactory.getLog(LinearKernelPrecomputer2.class);

    public LinearKernelPrecomputer2(final H5File datah5, final H5File kernelh5) {
        this.datah5 = datah5;
        this.kernelh5 = kernelh5;
    }

    public void compute(final Collection<Handle2> handles) {
        List<Handle2> handlesList = new ArrayList<Handle2>(handles);
        Collections.sort(handlesList);
        long gramdim = handlesList.size() * (handlesList.size() + 1L) / 2L;
        DataSpace gramspace = new DataSpace(gramdim);
        DataSetCreatePropListBuilder builder = new DataSetCreatePropListBuilder();
        builder.setFillTime(FillTime.NEVER);
        DataType dtype = FloatType.IEEE_F32LE;
        DataSet gramds = kernelh5.getRootGroup().createDataSet("kernel", dtype, gramspace, builder.build());
        gramspace.close();
        throw new UnsupportedOperationException();
    }
}
