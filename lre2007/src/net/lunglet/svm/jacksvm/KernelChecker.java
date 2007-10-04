package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.dense.FloatDenseVector;
import com.googlecode.array4j.math.FloatMatrixMath;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.Group;
import net.lunglet.hdf.H5File;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class KernelChecker {
    private static final Log LOG = LogFactory.getLog(KernelChecker.class);

    private static Set<String> getNames(final H5File datah5) {
        Set<String> names = new HashSet<String>();
        for (Group group : datah5.getRootGroup().getGroups()) {
            for (DataSet ds : group.getDataSets()) {
                names.add(ds.getName());
                ds.close();
            }
            group.close();
        }
        return names;
    }

    public static void main(final String[] args) {
        H5File datah5 = new H5File("C:/home/albert/LRE2007/work/data.h5", H5File.H5F_ACC_RDONLY);
        H5File kernelh5 = new H5File("C:/home/albert/LRE2007/work/kernel.h5", H5File.H5F_ACC_RDONLY);
        LOG.info("getting names of datasets");
        Set<String> names = getNames(datah5);

        // TODO get rid of DataVector
        LOG.info("opening data vectors");
        LinearKernelPrecomputer kernelComputer = new LinearKernelPrecomputer(datah5, kernelh5);
        Map<Integer, DataVector> vecs = kernelComputer.readDataVectors(names);

        LOG.info("reading kernel");
        H5KernelReader2 kernelReader = new H5KernelReader2(kernelh5);
        LOG.info("read kernel");
        DataSet kernelds = kernelh5.getRootGroup().openDataSet("/kernel");
        int[] order = kernelds.getIntArrayAttribute("order");
        Set<Integer> validIndexes = new HashSet<Integer>(Arrays.asList(ArrayUtils.toObject(order)));
        kernelds.close();
        int errorCount = 0;
        for (int i = 0; i < vecs.size(); i++) {
            DataVector veci = vecs.get(i);
            if (!validIndexes.contains(veci.getIndex())) {
                continue;
            }
            LOG.info(String.format("checking %s[%d] (%d)", veci.getName(), veci.getRow(), veci.getIndex()));
            FloatDenseVector x = veci.read();
            for (int j = i; j < vecs.size(); j++) {
                DataVector vecj = vecs.get(j);
                if (!validIndexes.contains(vecj.getIndex())) {
                    continue;
                }
                FloatDenseVector y = vecj.read();
                float kread = kernelReader.read(veci.getIndex(), vecj.getIndex());
                float k = FloatMatrixMath.dot(x, y);
                float delta = Math.abs((kread - k) / k);
                if (delta > 1e-3) {
                    LOG.error(String.format("invalid value for K(%d, %d): %.15e vs %.15e, delta = %.15e",
                        veci.getIndex(), vecj.getIndex(), k, kread, delta));
                    errorCount++;
                    if (errorCount > 20) {
                        LOG.fatal("too many errors, exiting");
                        return;
                    }
                }
            }
        }
        kernelh5.close();
        datah5.close();
    }
}
