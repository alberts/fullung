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
        /*
         * SVM background and TNorm data should not overlap
         */
        GMM_DIMENSION = 512 * 38;
        // TODO evaluation should have a trn and ndx file
        // TODO should also have filelist for features so we can check that
        // everything is available
        if (false) {
            EVAL_FILE = "Z:\\data\\nap512v2\\sre05-1conv4w_1conv4w.txt";
            EVAL_DATA = "Z:\\data\\nap512v2\\sre05_mfcc.h5";
            EVAL_GMM = "Z:\\data\\nap512v2\\sre05_gmm.h5";
            EVAL_SVM = "Z:\\data\\nap512v2\\sre05_svm.h5";
        } else {
            EVAL_FILE = "Z:\\data\\nap512v2\\sre06-1conv4w_1conv4w.txt";
            EVAL_DATA = "Z:\\data\\nap512v2\\sre06_mfcc.h5";
            EVAL_GMM = "Z:\\data\\nap512v2\\sre06_gmm.h5";
            EVAL_SVM = "Z:\\data\\nap512v2\\sre06_svm.h5";
        }
        Gender gender = null;
//        Gender gender = Gender.MALE;
//        Gender gender = Gender.FEMALE;
        if (gender == null) {
            UBM_FILE = "Z:\\data\\nap512v2\\both\\ubm_final_512.h5";
            CHANNEL_FILE = "Z:\\data\\nap512v2\\both\\channel.h5";
            KERNEL_FILE = "Z:\\data\\nap512v2\\both\\kernel.h5";
            NAP_GMM = "Z:\\data\\nap512v2\\both\\nap_gmm.h5";
            SVM_BACKGROUND_GMM = "Z:\\data\\nap512v2\\both\\svm_background_gmm.h5";
            TNORM_GMM = "Z:\\data\\nap512v2\\both\\tnorm_gmm.h5";
            TNORM_SVM = "Z:\\data\\nap512v2\\both\\tnorm_svm.h5";
        } else if (Gender.MALE.equals(gender)) {
            UBM_FILE = "Z:\\data\\nap512v2\\male\\ubm_male_final_512.h5";
            CHANNEL_FILE = "Z:\\data\\nap512v2\\male\\channel.h5";
            KERNEL_FILE = "Z:\\data\\nap512v2\\male\\kernel.h5";
            NAP_GMM = "Z:\\data\\nap512v2\\male\\nap_gmm.h5";
            SVM_BACKGROUND_GMM = "Z:\\data\\nap512v2\\male\\svm_background_gmm.h5";
            TNORM_GMM = "Z:\\data\\nap512v2\\male\\tnorm_gmm.h5";
            TNORM_SVM = "Z:\\data\\nap512v2\\male\\tnorm_svm.h5";
        } else if (Gender.FEMALE.equals(gender)) {
            UBM_FILE = "Z:\\data\\nap512v2\\female\\ubm_female_final_512.h5";
            CHANNEL_FILE = "Z:\\data\\nap512v2\\female\\channel.h5";
            KERNEL_FILE = "Z:\\data\\nap512v2\\female\\kernel.h5";
            NAP_GMM = "Z:\\data\\nap512v2\\female\\nap_gmm.h5";
            SVM_BACKGROUND_GMM = "Z:\\data\\nap512v2\\female\\svm_background_gmm.h5";
            TNORM_GMM = "Z:\\data\\nap512v2\\female\\tnorm_gmm.h5";
            TNORM_SVM = "Z:\\data\\nap512v2\\female\\tnorm_svm.h5";
        } else {
            throw new AssertionError();
        }
        // TODO these should all point to file lists, maybe with a channel appended
        NAP_DATA = "Z:\\data\\nap512v2\\nap_mfcc.h5";
        SVM_BACKGROUND_DATA = "Z:\\data\\nap512v2\\svm_background_mfcc.h5";
        TNORM_DATA = "Z:\\data\\nap512v2\\tnorm_mfcc.h5";
    }
}
