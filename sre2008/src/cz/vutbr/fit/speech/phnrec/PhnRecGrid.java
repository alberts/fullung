package cz.vutbr.fit.speech.phnrec;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.Topic;
import com.sun.messaging.TopicConnectionFactory;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.lunglet.gridgain.GridTaskManager;
import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.spi.communication.jms.GridJmsCommunicationSpi;
import org.gridgain.grid.spi.communication.tcp.GridTcpCommunicationSpi;
import org.gridgain.grid.spi.discovery.jms.GridJmsDiscoverySpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class PhnRecGrid {
    static final class PhnRecJobParameters {
        String filename;

        int channel;

        boolean isDone() {
            return new File(filename + "." + channel + ".mlf").exists();
        }
    }

    private static List<PhnRecJobParameters> createJobParameters() throws IOException, UnsupportedAudioFileException {
        List<PhnRecJobParameters> jobParamsList = new ArrayList<PhnRecJobParameters>();
        String path = "E:\\todo\\SRE05";
        FilenameFilter filter = new FilenameSuffixFilter(".sph", true);
        boolean recurse = true;
        for (File inputFile : FileUtils.listFiles(path, filter, recurse)) {
            System.out.println("processing " + inputFile.getCanonicalPath());
            AudioFileFormat format = AudioSystem.getAudioFileFormat(inputFile);
            for (int channel = 0; channel < format.getFormat().getChannels(); channel++) {
                PhnRecJobParameters jobParams = new PhnRecJobParameters();
                jobParams.filename = inputFile.getCanonicalPath();
                jobParams.channel = channel;
                if (!jobParams.isDone()) {
                    System.out.println("adding work unit for channel " + channel);
                    jobParamsList.add(jobParams);
                }
            }
        }
        return jobParamsList;
    }

    public static void main(final String[] args) throws Exception {
        int maximumJobs = 250;
        System.setProperty("GRIDGAIN_HOME", System.getProperty("user.dir"));
        System.setProperty("gridgain.update.notifier", "false");
        final List<PhnRecJobParameters> jobParamsList = createJobParameters();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        GridConfigurationAdapter cfg = new GridConfigurationAdapter();
        final String gridName = "grid";
        cfg.setGridName(gridName);
        GridBasicTopologySpi topologySpi = new GridBasicTopologySpi();
        topologySpi.setLocalNode(false);
        topologySpi.setRemoteNodes(true);
        cfg.setPeerClassLoadingEnabled(true);
        cfg.setTopologySpi(topologySpi);
        cfg.setExecutorService(executorService);
        if (true) {
//            cfg.setCommunicationSpi(new GridTcpCommunicationSpi());
//            cfg.setDiscoverySpi(new GridMulticastDiscoverySpi());
            cfg.setCommunicationSpi(new GridTcpCommunicationSpi());
            GridJmsDiscoverySpi discoSpi = new GridJmsDiscoverySpi();
            TopicConnectionFactory connectionFactory = new TopicConnectionFactory();
            discoSpi.setConnectionFactory(connectionFactory);
            discoSpi.setTopic(new Topic("gridgaindisco"));
            discoSpi.setTimeToLive(600L);
            discoSpi.setHeartbeatFrequency(3000L);
            discoSpi.setMaximumMissedHeartbeats(10L);
            discoSpi.setHandshakeWaitTime(10000L);
            cfg.setDiscoverySpi(discoSpi);
        } else {
          TopicConnectionFactory connectionFactory = new TopicConnectionFactory();
//          connectionFactory.setProperty(ConnectionConfiguration.imqBrokerHostName, "dominatrix.chem.sun.ac.za");
          connectionFactory.setProperty(ConnectionConfiguration.imqBrokerHostName, "asok.dsp.sun.ac.za");
          connectionFactory.setProperty(ConnectionConfiguration.imqBrokerHostPort, "7676");
          GridJmsCommunicationSpi commSpi = new GridJmsCommunicationSpi();
          commSpi.setConnectionFactory(connectionFactory);
          commSpi.setTopic(new Topic("gridgaincomm"));
          cfg.setCommunicationSpi(commSpi);
          GridJmsDiscoverySpi discoSpi = new GridJmsDiscoverySpi();
          discoSpi.setConnectionFactory(connectionFactory);
          discoSpi.setTopic(new Topic("gridgaindisco"));
          cfg.setDiscoverySpi(discoSpi);
        }
        try {
            final Grid grid = GridFactory.start(cfg);
            // without this sleep, things break
            Thread.sleep(10000L);
            GridTaskManager<PhnRecJob> taskManager = null;
            taskManager = new GridTaskManager<PhnRecJob>(grid, PhnRecTask.class, maximumJobs);
            taskManager.execute(new PhnRecTaskFactory(jobParamsList));
        } finally {
            GridFactory.stop("grid", false);
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
    }
}
