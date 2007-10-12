package net.lunglet.lre.lre07;

import java.io.File;
import java.io.IOException;

public final class Constants {
//    public static final String DATA_DIRECTORY = "E:/albert/MIT/data";
    public static final String DATA_DIRECTORY = "G:/MIT/data";

//    public static final String SPLITS_DIRECTORY = "E:/albert/splits/mitpart2";
//    public static final String SPLITS_DIRECTORY = "E:/albert/splits/mitpart2other";
//    public static final String SPLITS_DIRECTORY = "E:/albert/splits/mitpart6";
//    public static final String SPLITS_DIRECTORY = "C:/home/albert/LRE2007/work/splits/mitpart2";
    public static final String SPLITS_DIRECTORY = "C:/home/albert/LRE2007/work/splits/mitpart2other";
//    public static final String SPLITS_DIRECTORY = "C:/home/albert/LRE2007/work/splits/mitpart6";

//    public static final String WORKING_DIRECTORY = "E:/albert";
    public static final String WORKING_DIRECTORY = "G:/";

    public static final CrossValidationSplits CVSPLITS;

    static {
        try {
            int testSplits = 10;
            int backendSplits = 10;
            boolean includeEval = true;
            CVSPLITS = new CrossValidationSplits(testSplits, backendSplits, new File(SPLITS_DIRECTORY), new File(
                    DATA_DIRECTORY), includeEval);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
