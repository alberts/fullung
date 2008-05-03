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
        UBM_FILE = "C:\\home\\albert\\SRE2008\\data\\ubm_final_512.h5";
        SVM_BACKGROUND_GMM = "C:\\home\\albert\\SRE2008\\data\\sv_Winitpca20_5iters_svm.h5";
        KERNEL_FILE = "C:\\home\\albert\\SRE2008\\data\\kernel.h5";
        TNORM_GMM = "C:\\home\\albert\\SRE2008\\data\\sv_Winitpca20_5iters_tnorm.h5";
        TNORM_SVM = "C:\\home\\albert\\SRE2008\\data\\tnorm_svm.h5";
        EVAL_GMM = "C:\\home\\albert\\SRE2008\\data\\sv_Winitpca20_5iters_sre06.h5";
        EVAL_SVM = "C:\\home\\albert\\SRE2008\\data\\eval_svm.h5";
        EVAL_FILE = "C:\\home\\albert\\SRE2008\\data\\sre06-1conv4w_1conv4w.txt";
        NAP_DATA = null;
        NAP_GMM = null;
        CHANNEL_FILE = null;
        SVM_BACKGROUND_DATA = null;
        TNORM_DATA = null;
        EVAL_DATA = null;
    }
}
