package net.lunglet.sre2008;

public final class Constants {
    public static final String CHANNEL_FILE;

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
        GMM_DIMENSION = 512 * 38;
        UBM_FILE = "Z:\\data\\nikov0\\ubm_final_512.h5";
//        UBM_FILE = "z:\\data\\nap512v2\\both\\ubm_final_512.h5";

        SVM_BACKGROUND_GMM = "Z:\\data\\nikov0\\sv10_svm.h5";
//        SVM_BACKGROUND_GMM = "Z:\\data\\nap512v2\\both\\svm_background_gmm.h5";
        KERNEL_FILE = "C:\\home\\albert\\SRE2008\\data\\kernel.h5";
        TNORM_GMM = "Z:\\data\\nikov0\\sv10_tnorm.h5";
//        TNORM_GMM = "Z:\\data\\nap512v2\\both\\tnorm_gmm.h5";
        TNORM_SVM = "C:\\home\\albert\\SRE2008\\data\\tnorm_svm.h5";
        EVAL_GMM = "Z:\\data\\nikov0\\sv10_sre06.h5";
//        EVAL_GMM = "Z:\\data\\nap512v2\\both\\sre06_gmm.h5";
        EVAL_SVM = "C:\\home\\albert\\SRE2008\\data\\eval_svm.h5";
        EVAL_FILE = "Z:\\data\\nap512v2\\sre06-1conv4w_1conv4w.txt";

        NAP_DATA = null;
        NAP_GMM = null;
        CHANNEL_FILE = null;
        SVM_BACKGROUND_DATA = null;
        TNORM_DATA = null;
        EVAL_DATA = null;
    }
}
