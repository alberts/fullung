package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.dense.FloatDenseMatrix;
import java.io.IOException;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.H5File;

// TODO score test_i against fe_i_j for all j and then average over scores

// TODO score test_i against language-averaged support vectors (call it fe_i)

public final class MultipleSVMScorer {
    public static void main(final String[] args) throws IOException, ClassNotFoundException {
        H5File modelsh5 = new H5File("G:/models.h5", H5File.H5F_ACC_RDONLY);
        for (DataSet ds : modelsh5.getRootGroup().getDataSets()) {
            // TODO read data from _test_0
            // TODO read floatdensematrix here
            FloatDenseMatrix model = new FloatDenseMatrix(19183, 14);
            ds.close();
            // TODO read data from backend_0_0
            // TODO score
            // train more, score more
            // TODO do averaging stuff
        }
        modelsh5.close();
    }
}
