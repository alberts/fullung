package net.lunglet.svm.jacksvm;

import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseVector;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;
import net.lunglet.lre.lre07.Constants;

// TODO score test_i against fe_i_j for all j and then average over scores

// TODO score test_i against language-averaged support vectors (call it fe_i)

public final class MultipleSVMScorer {
    private static FloatDenseMatrix readFrontend(final int tidx, final int beidx) throws IOException,
            ClassNotFoundException {
        File dataDir = new File(Constants.WORKING_DIRECTORY, "sdv_sun_1");
        File file = new File(dataDir, "frontend_0_0.dat.gz");
        ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
        JackSVM2 svm = (JackSVM2) ois.readObject();
        ois.close();
        FloatDenseMatrix sv = svm.getSupportVectors();
        FloatDenseVector rhos = svm.getRhos();
        FloatDenseMatrix model = new FloatDenseMatrix(sv.columns() + 1, sv.rows(), Orientation.COLUMN, Storage.DIRECT);
        model.setRow(model.rows() - 1, rhos);
        return model;
    }

    public static void main(final String[] args) throws IOException, ClassNotFoundException {
        readFrontend(0, 0);
    }
}
