package net.lunglet.sre2008;

public final class Constants {
    public static final String BACKGROUND_DATA;

    public static final String BACKGROUND_GMM;

    public static final String EVAL_DATA;

    public static final String EVAL_FILE;

    public static final String EVAL_GMM;

    public static final String EVAL_SVM;

    public static final int GMM_DIMENSION;

    public static final String KERNEL_FILE;

    public static final String TNORM_DATA;

    public static final String TNORM_GMM;

    public static final String TNORM_SVM;

    public static final String UBM_FILE;

    static {
        if (true) {
            BACKGROUND_DATA = "Z:\\data\\background_mfcc2_79.h5";
            BACKGROUND_GMM = "C:\\home\\albert\\SRE2008\\data\background_gmm.h5";
            EVAL_FILE = "C:\\home\\albert\\SRE2008\\scripts\\sre05-1conv4w_1conv4w.txt";
            EVAL_DATA = "Z:\\data\\sre05_1conv4w_1conv4w_mfcc2_79.h5";
            EVAL_GMM = "C:\\home\\albert\\SRE2008\\data\\eval_gmm.h5";
            EVAL_SVM = "C:\\home\\albert\\SRE2008\\data\\eval_svm.h5";
            GMM_DIMENSION = 512 * 79;
            KERNEL_FILE = "C:\\home\\albert\\SRE2008\\data\\kernel.h5";
            TNORM_DATA = "Z:\\data\\tnorm_mfcc2_79.h5";
            TNORM_GMM = "C:\\home\\albert\\SRE2008\\data\\tnorm_gmm.h5";
            TNORM_SVM = "C:\\home\\albert\\SRE2008\\data\\tnorm_svm.h5";
            UBM_FILE = "Z:\\data\\ubm8_final_79_512.h5";
        }
    }
}
