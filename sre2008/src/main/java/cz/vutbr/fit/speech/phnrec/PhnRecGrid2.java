package cz.vutbr.fit.speech.phnrec;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.jms.Topic;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobAdapter;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridTaskFuture;
import org.gridgain.grid.GridTaskSplitAdapter;
import org.gridgain.grid.spi.collision.jobstealing.GridJobStealingCollisionSpi;
import org.gridgain.grid.spi.communication.tcp.GridTcpCommunicationSpi;
import org.gridgain.grid.spi.discovery.jms.GridJmsDiscoverySpi;
import org.gridgain.grid.spi.failover.jobstealing.GridJobStealingFailoverSpi;
import org.gridgain.grid.spi.failover.never.GridNeverFailoverSpi;
import org.gridgain.grid.spi.loadbalancing.roundrobin.GridRoundRobinLoadBalancingSpi;
import org.gridgain.grid.spi.topology.attributes.GridAttributesTopologySpi;

public final class PhnRecGrid2 {
    private static GridConfigurationAdapter createGridConfiguration() {
        GridConfigurationAdapter cfg = new GridConfigurationAdapter();
        cfg.setGridName("grid");
//        GridBasicTopologySpi topologySpi = new GridBasicTopologySpi();
//        topologySpi.setLocalNode(false);
//        topologySpi.setRemoteNodes(true);
        GridAttributesTopologySpi topologySpi = new GridAttributesTopologySpi();
        Map<String, Serializable> topologyAttrs = new HashMap<String, Serializable>(1);
        topologyAttrs.put("group", "worker");
        topologySpi.setAttributes(topologyAttrs);
        cfg.setTopologySpi(topologySpi);
        Map<String, Serializable> userAttrs = new HashMap<String, Serializable>(1);
        userAttrs.put("group", "submit");
        cfg.setUserAttributes(userAttrs);
        cfg.setPeerClassLoadingEnabled(true);
        cfg.setCommunicationSpi(new GridTcpCommunicationSpi());
        GridJmsDiscoverySpi discoSpi = new GridJmsDiscoverySpi();
//        TopicConnectionFactory connectionFactory = new TopicConnectionFactory();
        Topic topic = null;
//        try {
//            String imqBrokerHostName = "asok.dsp.sun.ac.za";
//            String imqBrokerHostName = "localhost";
//            connectionFactory.setProperty(ConnectionConfiguration.imqBrokerHostName, imqBrokerHostName);
//            connectionFactory.setProperty(ConnectionConfiguration.imqBrokerHostPort, "7676");
//            topic = new Topic("gridgaindisco");
//        } catch (JMSException e) {
//            throw new RuntimeException(e);
//        }
//        discoSpi.setConnectionFactory(connectionFactory);
        discoSpi.setTopic(topic);
        discoSpi.setTimeToLive(600L);
        discoSpi.setHeartbeatFrequency(3000L);
        discoSpi.setMaximumMissedHeartbeats(10L);
        discoSpi.setHandshakeWaitTime(10000L);
        cfg.setDiscoverySpi(discoSpi);
//        cfg.setDiscoverySpi(new GridMulticastDiscoverySpi());
        cfg.setFailoverSpi(new GridNeverFailoverSpi());
        GridRoundRobinLoadBalancingSpi loadBalancingSpi = new GridRoundRobinLoadBalancingSpi();
        loadBalancingSpi.setPerTask(false);
        cfg.setLoadBalancingSpi(loadBalancingSpi);
        GridJobStealingCollisionSpi collisionSpi = new GridJobStealingCollisionSpi();
        collisionSpi.setActiveJobsThreshold(2);
        collisionSpi.setWaitJobsThreshold(0);
        collisionSpi.setMessageExpireTime(5000L);
        cfg.setCollisionSpi(collisionSpi);
        GridJobStealingFailoverSpi failoverSpi = new GridJobStealingFailoverSpi();
        failoverSpi.setMaximumFailoverAttempts(10);
        cfg.setFailoverSpi(failoverSpi);
        return cfg;
    }

    public static final class TestTask extends GridTaskSplitAdapter<Object, Object> {
        private static final long serialVersionUID = 1L;

        @Override
        protected Collection<? extends GridJob> split(int gridSize, final Object arg) throws GridException {
            List<GridJob> jobs = new ArrayList<GridJob>();
            jobs.add(new GridJobAdapter<Serializable>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Serializable execute() throws GridException {
                    System.out.println("Job for TestTask executing: " + arg);
                    return null;
                }
            });
            return jobs;
        }

        @Override
        public Object reduce(List<GridJobResult> results) throws GridException {
            return null;
        }
    }

    public static void main(final String[] args) throws Exception {
        System.setProperty("GRIDGAIN_HOME", System.getProperty("user.dir"));
        System.setProperty("gridgain.update.notifier", "false");
        final File baseDirectory = new File("E:\\").getAbsoluteFile();
        final String[] workDirectories = {"SRE06"};
        final FilenameFilter filter = new FilenameSuffixFilter(".sph", true);
        final boolean recurse = true;
        GridConfigurationAdapter cfg = createGridConfiguration();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        cfg.setExecutorService(executorService);
        final Grid grid = GridFactory.start(cfg);
        try {
            Thread.sleep(10000L);
//            List<GridTaskFuture<Object>> futures = new ArrayList<GridTaskFuture<Object>>();
//            for (int i = 0; i < 1000; i++) {
//                GridTaskFuture<Object> future = grid.execute(TestTask.class, i);
//                futures.add(future);
//            }
//          for (GridTaskFuture<Object> future : futures) {
//              try {
//                  future.get();
//              } catch (Throwable t) {
//                  System.err.println("Task failed: " + t.getMessage());
//              }
//          }
            List<GridTaskFuture<Void>> futures = new ArrayList<GridTaskFuture<Void>>();
            for (String workDir : workDirectories) {
                File path = new File(baseDirectory, workDir).getCanonicalFile();
                for (File audioFile : FileUtils.listFiles(path, filter, recurse)) {
                    audioFile = audioFile.getCanonicalFile();
                    AudioFileFormat format = AudioSystem.getAudioFileFormat(audioFile);
                    for (int channel = 0; channel < format.getFormat().getChannels(); channel++) {
                        File mlfFile = new File(audioFile.getAbsolutePath() + "." + channel + ".mlf");
                        if (!mlfFile.exists()) {
                            String name = audioFile.getPath().replace(path.getParent(), "");
                            name = "/" + name.replace('\\', '/');
                            PhnRecJob job = new PhnRecJob(name, channel);
                            GridTaskFuture<Void> future = grid.execute(PhnRecTask.class, job);
                            futures.add(future);
                        }
                    }
                }
            }
            for (GridTaskFuture<Void> future : futures) {
                try {
                    future.get();
                } catch (Throwable t) {
                    System.err.println("Task failed: " + t.getMessage());
                }
            }
        } finally {
            GridFactory.stop(cfg.getGridName(), false);
            executorService.shutdown();
            executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
        }
    }
}
