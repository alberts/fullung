package net.lunglet.sre2008.v2;

import com.dvsoft.sv.toolbox.gmm.JMapGMM;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import net.lunglet.sre2008.io.IOUtils;
import net.lunglet.sre2008.util.Converters;
import net.lunglet.util.MainTemplate;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;

public final class TrainGMM {
    @CommandLineInterface(application = "TrainGMM")
    private static interface Arguments {
        @Option(shortName = "u", description = "channel compensation data")
        File getChannel();

        @Option(shortName = "c", description = "configuration")
        File getConfiguration();

        @Option(shortName = "o", description = "output")
        File getOutput();

        @Option(shortName = "u", description = "UBM file")
        File getUbm();

        boolean isChannel();

        boolean isConfiguration();
    }

    public static final class Main extends MainTemplate<Arguments> {
        private final Logger logger = LoggerFactory.getLogger(getClass());

        public Main() {
            super(Arguments.class);
        }

        @Override
        protected int mainImpl(final Arguments args) throws Throwable {
            File ubmFile = args.getUbm();
            checkFileExists("UBM", ubmFile);
            final File channelFile;
            if (args.isChannel()) {
                channelFile = args.getChannel();
                checkFileExists("channel compensation", channelFile);
            } else {
                channelFile = null;
            }
            File outputFile = args.getOutput();
            checkFileNotExists("output", outputFile);
            JMapGMM ubm = Converters.convert(IOUtils.readDiagCovGMM(ubmFile));
            ServiceFactory factory = new ServiceFactory(args);
            ExecutorService executorService = factory.getExecutorService();
            try {
            } finally {
                logger.debug("Shutting down executor service");
                executorService.shutdown();
                executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
            }
            return 0;
        }
    }

    private static final class ServiceFactory {
        private final ExecutorService executorService;

        public ServiceFactory(final Arguments args) {
            final ApplicationContext ctx;
            if (args.isConfiguration()) {
                ctx = new FileSystemXmlApplicationContext(args.getConfiguration().getPath());
            } else {
                ctx = null;
            }
            // TODO can probably do something better here
            if (ctx != null && ctx.containsBean("executorService")) {
                this.executorService = (ExecutorService) ctx.getBean("executorService");
            } else {
                throw new NotImplementedException();
            }
        }

        public ExecutorService getExecutorService() {
            return executorService;
        }
    }

    public static void main(final String[] args) throws Throwable {
        new Main().main(args);
    }
}
