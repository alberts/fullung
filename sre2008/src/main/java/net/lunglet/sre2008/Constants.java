package net.lunglet.sre2008;

public final class Constants {
    public static final String EVAL_DATA;

    public static final String EVAL_FILE;

    public static final String EVAL_GMM;

    public static final String EVAL_SVM;

    public static final int GMM_DIMENSION;

    public static final String KERNEL_FILE;

    public static final String NAP_DATA;

    public static final String NAP_GMM;

    public static final String SVM_BACKGROUND_DATA;

    public static final String SVM_BACKGROUND_GMM;

    public static final String TNORM_DATA;

    public static final String TNORM_GMM;

    public static final String TNORM_SVM;

    public static final String UBM_FILE;

    static {
        /*
         * SVM background and TNorm data should not overlap
         */
        if (true) {
//            EVAL_FILE = "C:\\home\\albert\\SRE2008\\scripts\\sre05-1conv4w_1conv4w.txt";
//            EVAL_DATA = "Z:\\data\\sre05_1conv4w_1conv4w_mfcc2_79.h5";
            EVAL_FILE = "C:\\home\\albert\\SRE2008\\scripts\\sre06-1conv4w_1conv4w.txt";
            EVAL_DATA = "Z:\\data\\tnorm79\\sre06_1conv4w_1conv4w_mfcc2_79.h5";
            EVAL_GMM = "C:\\home\\albert\\SRE2008\\data\\eval_gmm.h5";
            EVAL_SVM = "C:\\home\\albert\\SRE2008\\data\\eval_svm.h5";
            GMM_DIMENSION = 512 * 79;
            KERNEL_FILE = "Z:\\data\\tnorm79\\kernel.h5";
            NAP_DATA = "Z:\\data\\tnorm79\\nap_mfcc2_79.h5";
            NAP_GMM = "Z:\\data\\tnorm79\\nap_gmm.h5";
            SVM_BACKGROUND_DATA = "Z:\\data\\tnorm79\\svm_background_mfcc2_79.h5";
            SVM_BACKGROUND_GMM = "Z:\\data\\tnorm79\\svm_background_gmm.h5";
            TNORM_DATA = "Z:\\data\\tnorm79\\tnorm_mfcc2_79.h5";
            TNORM_GMM = "z:\\data\\tnorm79\\tnorm_gmm.h5";
            TNORM_SVM = "z:\\data\\tnorm79\\tnorm_svm.h5";
            UBM_FILE = "Z:\\data\\tnorm79\\ubm8_final_79_512.h5";
        }
    }
}
