package net.lunglet.sre2008.v2;

import java.io.File;
import java.util.List;
import net.lunglet.hdf.H5File;
import net.lunglet.sre2008.svm.CalculateKernel;
import net.lunglet.util.MainTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

public final class CalculateKernel2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalculateKernel2.class);

    @CommandLineInterface(application = "CalculateKernel2")
    private static interface Arguments {
        @Option(shortName = "i", description = "data (input)")
        File getInput();

        @Option(shortName = "o", description = "kernel (output)")
        File getOutput();

        @Option(shortName = "b", defaultValue = "1000", description = "buffer columns")
        int getBufferColumns();
    }

    public static final class Main extends MainTemplate<Arguments> {
        public Main() {
            super(Arguments.class);
        }

        @Override
        protected int mainImpl(final Arguments args) throws Throwable {
            File dataFile = args.getInput();
            checkFileExists("data", dataFile);
            File kernelFile = args.getOutput();
            checkFileNotExists("kernel", kernelFile);
            int bufferColumns = args.getBufferColumns();
            if (bufferColumns <= 0) {
                throw new IllegalArgumentException("Buffer columns must be positive");
            }
            H5File datah5 = new H5File(dataFile);
            H5File kernelh5 = new H5File(kernelFile, H5File.H5F_ACC_TRUNC);
            LOGGER.info("Reading data from {}", datah5.getFileName());
            List<String> data = CalculateKernel.getNames(datah5);
            LOGGER.info("Writing kernel to {}", kernelh5.getFileName());
            CalculateKernel.calculateKernel(datah5, data, kernelh5, bufferColumns);
            kernelh5.close();
            datah5.close();
            return 0;
        }
    }

    public static void main(final String[] args) throws Throwable {
        new Main().main(args);
    }
}
