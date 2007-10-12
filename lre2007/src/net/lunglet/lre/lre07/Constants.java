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
//    public static final String SPLITS_DIRECTORY = "C:/home/albert/LRE2007/work/splits/mitpart2other";
    public static final String SPLITS_DIRECTORY = "C:/home/albert/LRE2007/work/splits/mitpart6";

//    public static final String WORKING_DIRECTORY = "E:/albert";
    public static final String WORKING_DIRECTORY = "G:/";

    public static final CrossValidationSplits CVSPLITS;

    static {
        try {
            int testSplits = 1;
            int backendSplits = 10;
            boolean scoreEval = false;
            File splitsDir = new File(SPLITS_DIRECTORY);
            File dataDir = new File(DATA_DIRECTORY);
            CVSPLITS = new CrossValidationSplits(testSplits, backendSplits, splitsDir, dataDir, scoreEval);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
