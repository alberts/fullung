package net.lunglet.lre.lre07;

import java.io.IOException;

public final class Constants {
//    public static final String DATA_DIRECTORY = "E:/albert/data";
    public static final String DATA_DIRECTORY = "G:/MIT/data";

//    public static final String SPLITS_DIRECTORY = "E:/albert/splits/mitpart2";
//    public static final String SPLITS_DIRECTORY = "C:/home/albert/LRE2007/work/splits/mitpart2";
    public static final String SPLITS_DIRECTORY = "C:/home/albert/LRE2007/work/splits/mitpart2other";
//    public static final String SPLITS_DIRECTORY = "C:/home/albert/LRE2007/work/splits/mitpart6";

//    public static final String WORKING_DIRECTORY = "E:/albert";
    public static final String WORKING_DIRECTORY = "G:/";

    public static final CrossValidationSplits CVSPLITS;

    public static final int NTHREADS = 2;

    static {
        try {
            int testSplits = 1;
            int backendSplits = 10;
            CVSPLITS = new CrossValidationSplits(testSplits, backendSplits);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
