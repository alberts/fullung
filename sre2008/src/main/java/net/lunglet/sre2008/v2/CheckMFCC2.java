package net.lunglet.sre2008.v2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.lunglet.array4j.matrix.dense.DenseFactory;
import net.lunglet.array4j.matrix.dense.FloatDenseMatrix;
import net.lunglet.hdf.DataSet;
import net.lunglet.io.HDFReader;
import net.lunglet.util.AssertUtils;
import net.lunglet.util.MainTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

public final class CheckMFCC2 {
    @CommandLineInterface(application = "CheckMFCC2")
    private static interface Arguments {
        @Option(shortName = "f", description = "filelist")
        File getFilelist();
    }

    private static class Main extends MainTemplate<Arguments> {
        public Main() {
            super(Arguments.class);
        }

        @Override
        protected int mainImpl(final Arguments args) throws Throwable {
            File filelist = args.getFilelist();
            checkFileExists("file list", filelist);
            List<String> mfccFiles = readFilelist2(filelist);
            for (String name : mfccFiles) {
                final String filename;
                final int channel;
                if (name.endsWith(":a") || name.endsWith(":b")) {
                    int len = name.length();
                    filename = name.substring(0, len - 2);
                    channel = name.substring(len - 1, len).equals("a") ? 0 : 1;
                } else {
                    filename = name;
                    channel = 0;
                }
                LOGGER.debug("Checking {}:{}", filename, channel);
                try {
                    checkMFCC(filename, channel);
                } catch (Throwable t) {
                    LOGGER.error(filename + ":" + channel + " is invalid", t);
                }
            }
            return 0;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckMFCC2.class);

    private static boolean checkMFCC(final String filename, final int channel) {
        HDFReader reader = new HDFReader(filename);
        DataSet dataset = reader.getH5File().getRootGroup().openDataSet("/mfcc/" + channel);
        int[] dims = dataset.getIntDims();
        dataset.close();
        FloatDenseMatrix mfccMat = DenseFactory.floatRowDirect(dims);
        reader.read("/mfcc/" + channel, mfccMat);
        reader.close();
        float[][] mfcc = mfccMat.toRowArrays();
        LOGGER.info("{}:{} contains {} features", new Object[]{filename, channel, mfcc.length});
        // TODO make this threshold configurable
        if (mfcc.length < 1000) {
            LOGGER.error("{}:{} contains only {} < 1000 features", new Object[]{filename, channel, mfcc.length});
            return false;
        }
        for (int i = 0; i < mfcc.length; i++) {
            for (int j = 0; j < mfcc[i].length; j++) {
                float v = mfcc[i][j];
                AssertUtils.assertFalse(Float.isInfinite(v));
                AssertUtils.assertFalse(Float.isNaN(v));
                if (v < -3.0f || v > 3.0f) {
                    LOGGER.error("{}:{} contains a value outside the range [-3.0, 3.0]");
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(final String[] args) throws Throwable {
        new Main().main(args);
    }

    private static List<String> readFilelist2(final File filelist) throws IOException {
        List<String> files = new ArrayList<String>();
        BufferedReader lineReader = new BufferedReader(new FileReader(filelist));
        try {
            String line = lineReader.readLine();
            while (line != null) {
                line = line.trim();
                final String filename;
                if (line.endsWith(":a") || line.endsWith(":b")) {
                    filename = line.substring(0, line.length() - 2);
                } else {
                    filename = line;
                }
                files.add(line);
                line = lineReader.readLine();
            }
        } finally {
            lineReader.close();
        }
        return files;
    }
}
