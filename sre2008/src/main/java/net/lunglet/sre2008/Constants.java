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
        GMM_DIMENSION = 512 * 38;
        if (true) {
            EVAL_FILE = "C:\\home\\albert\\SRE2008\\scripts\\sre05-1conv4w_1conv4w.txt";
            EVAL_DATA = "C:\\home\\albert\\SRE2008\\data\\sre05_mfcc.h5";
            EVAL_GMM = "C:\\home\\albert\\SRE2008\\data\\sre05_gmm.h5";
            EVAL_SVM = "C:\\home\\albert\\SRE2008\\data\\sre05_svm.h5";
        } else {
            EVAL_FILE = "C:\\home\\albert\\SRE2008\\scripts\\sre06-1conv4w_1conv4w.txt";
            EVAL_DATA = "C:\\home\\albert\\SRE2008\\data\\sre06_mfcc.h5";
            EVAL_GMM = "C:\\home\\albert\\SRE2008\\data\\sre06_gmm.h5";
            EVAL_SVM = "C:\\home\\albert\\SRE2008\\data\\sre06_svm.h5";
        }
        KERNEL_FILE = "C:\\home\\albert\\SRE2008\\data\\kernel.h5";
        NAP_DATA = "C:\\home\\albert\\SRE2008\\data\\nap_mfcc.h5";
        NAP_GMM = "C:\\home\\albert\\SRE2008\\data\\nap_gmm.h5";
        SVM_BACKGROUND_DATA = "C:\\home\\albert\\SRE2008\\data\\svm_background_mfcc.h5";
        SVM_BACKGROUND_GMM = "C:\\home\\albert\\SRE2008\\data\\svm_background_gmm.h5";
        TNORM_DATA = "C:\\home\\albert\\SRE2008\\data\\tnorm_mfcc.h5";
        TNORM_GMM = "C:\\home\\albert\\SRE2008\\data\\tnorm_gmm.h5";
        TNORM_SVM = "C:\\home\\albert\\SRE2008\\data\\tnorm_svm.h5";
        UBM_FILE = "C:\\home\\albert\\SRE2008\\data\\ubm_final_512.h5";
    }
}
