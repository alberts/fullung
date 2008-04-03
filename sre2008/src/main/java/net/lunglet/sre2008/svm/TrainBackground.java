package net.lunglet.sre2008.svm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.lunglet.array4j.Direction;
import net.lunglet.array4j.Storage;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseVector;
import net.lunglet.gmm.DiagCovGMM;
import net.lunglet.gmm.GMMUtils;
import net.lunglet.gridgain.ResultListener;
import net.lunglet.hdf.DataSet;
import net.lunglet.hdf.H5File;
import net.lunglet.io.HDFWriter;
import net.lunglet.sre2008.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TrainBackground {
    private static final String DATA_FILE = "Z:/data/sre04mfcc_1s1s.h5";

    private static final String GMM_FILE = "Z:/data/sre04gmm_1s1s.h5";

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainBackground.class);

    private static final String UBM_FILE = "Z:/data/ubm_floored_512_3.h5";

    public static void main(final String[] args) throws Exception {
        DiagCovGMM ubm = IOUtils.readDiagCovGMM(UBM_FILE);
        if (!GMMUtils.isGMMParametersFinite(ubm)) {
            LOGGER.error("UBM contains invalid parameters");
            throw new RuntimeException();
        }

        List<SpeakerTrainJob> jobs = new ArrayList<SpeakerTrainJob>();
        H5File dataFile = new H5File(DATA_FILE);
        for (DataSet dataset : dataFile.getRootGroup().getDataSets()) {
            String name = dataset.getName();
            dataset.close();
            jobs.add(new SpeakerTrainJob(name, DATA_FILE));
        }
        dataFile.close();
        Collections.sort(jobs);

        H5File gmmFile = new H5File(GMM_FILE, H5File.H5F_ACC_TRUNC);
        final HDFWriter writer = new HDFWriter(gmmFile);
        final List<SpeakerTrainResult> results = new ArrayList<SpeakerTrainResult>();
        ResultListener<SpeakerTrainResult> resultHandler = new ResultListener<SpeakerTrainResult>() {
            private int resultCount = 0;

            @Override
            public void onResult(final SpeakerTrainResult result) {
                results.add(result);
                LOGGER.info("Got result for {} [{}]", result.getName(), ++resultCount);
                FloatDenseVector sv = DenseFactory.floatVector(result.getModel(), Direction.ROW, Storage.DIRECT);
                synchronized (H5File.class) {
                    writer.write(result.getName(), sv);
                }
            }
        };
        if (true) {
//            new DefaultGrid<SpeakerTrainJob, SpeakerTrainResult>(SpeakerTrainTask.class, jobs, resultHandler).run();
        } else {
//            new LocalGrid<SpeakerTrainJob, SpeakerTrainResult>(SpeakerTrainTask.class, jobs, resultHandler).run();
        }
        writer.close();
    }
}
