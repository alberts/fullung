package cz.vutbr.fit.speech.phnrec;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;
import net.lunglet.sound.util.SoundUtils;
import net.lunglet.util.zip.ZipUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.GridJobAdapter;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridTaskFuture;
import org.gridgain.grid.GridTaskSplitAdapter;
import org.gridgain.grid.spi.collision.fifoqueue.GridFifoQueueCollisionSpi;
import org.gridgain.grid.spi.failover.never.GridNeverFailoverSpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class PhnRecGrid2 {
    private static final class PhnRecJob extends GridJobAdapter<Serializable> {
        private static final String FTP_HOST = "localhost";

        private static final String FTP_USERNAME = "username";

        private static final String FTP_PASSWORD = "password";

//        private static final File BASE_TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));
        private static final File BASE_TEMP_DIR = new File("C:\\temp\\temp");

        private static final ThreadLocal<File> PHNREC_DIR = new ThreadLocal<File>() {
            @Override
            protected File initialValue() {
                try {
                    File tempDir = FileUtils.createTempDirectory("phnrec", ".tmp", BASE_TEMP_DIR);
                    deleteOnExit(tempDir);
                    String resourcePrefix = "/cz/vutbr/fit/speech/phnrec/";
                    InputStream[] streams = new InputStream[]{
                            getClass().getResourceAsStream(resourcePrefix + "PHN_CZ_SPDAT_LCRC_N1500.zip"),
                            getClass().getResourceAsStream(resourcePrefix + "phnrec.zip")};
                    for (InputStream stream : streams) {
                        if (stream == null) {
                            throw new RuntimeException();
                        }
                        List<File> tempFiles = new ArrayList<File>();
                        ZipInputStream zis = new ZipInputStream(stream);
                        Collections.addAll(tempFiles, ZipUtils.extractAll(zis, tempDir));
                        zis.close();
                        for (File tempFile : tempFiles) {
                            deleteOnExit(tempFile);
                        }
                    }
                    return tempDir;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        private static final long serialVersionUID = 1L;

        private static void deleteOnExit(final File file) {
            if (true) {
                file.deleteOnExit();
            }
        }

        private final int channel;

        private final String name;

        public PhnRecJob(final String name, final int channel) {
            this.name = name;
            this.channel = channel;
        }

        @Override
        public Serializable execute() throws GridException {
            final FTPClient ftp = new FTPClient();
            File phnrecDir = PHNREC_DIR.get();
            File pcmFile = null;
            File mlfFile = null;
            try {
                ftp.connect(FTP_HOST);
                ftp.login(FTP_USERNAME, FTP_PASSWORD);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                boolean result = ftp.retrieveFile(name, baos);
                if (!result) {
                    throw new RuntimeException("FTP retrieve failed");
                }
                byte[] sphBuf = baos.toByteArray();
                byte[] pcmBuf = SoundUtils.readChannel(new ByteArrayInputStream(sphBuf), channel);
                pcmFile = File.createTempFile("pcm", ".snd", phnrecDir);
                DataOutputStream output = new DataOutputStream(new FileOutputStream(pcmFile));
                output.write(pcmBuf);
                output.close();
                mlfFile = File.createTempFile("mlf", ".txt", phnrecDir);
                run(phnrecDir, pcmFile, mlfFile);
                FileInputStream mlfStream = new FileInputStream(mlfFile);
                try {
                    result = ftp.storeFile(name + "." + channel + ".mlf", mlfStream);
                } finally {
                    mlfStream.close();
                }
                if (!result) {
                    throw new RuntimeException("FTP store failed");
                }
            } catch (IOException e) {
                throw new GridException(null, e);
            } catch (InterruptedException e) {
                throw new GridException(null, e);
            } finally {
                if (pcmFile != null) {
                    pcmFile.delete();
                }
                if (mlfFile != null) {
                    mlfFile.delete();
                }
                if (ftp.isConnected()) {
                    try {
                        ftp.disconnect();
                    } catch (IOException e) {
                        // ignore this exception
                    }
                }
            }
            return null;
        }

        private static final void run(final File phnrecDir, final File pcmFile, final File mlfFile) throws IOException,
                InterruptedException {
            List<String> command = new ArrayList<String>();
            command.add(new File(phnrecDir, "phnrec.exe").getAbsolutePath());
            command.add("-v");
            command.add("-c");
            command.add(phnrecDir.getAbsolutePath());
            // source is single channel linear 16-bit PCM data
            command.add("-s");
            command.add("wf");
            command.add("-w");
            command.add("lin16");
            command.add("-i");
            command.add(pcmFile.getAbsolutePath());
            // target is mlf text file
            command.add("-t");
            command.add("str");
            command.add("-o");
            command.add(mlfFile.getAbsolutePath());
            StringBuilder commandBuilder = new StringBuilder();
            for (String str : command) {
                commandBuilder.append(str);
                commandBuilder.append(" ");
            }
            String commandString = commandBuilder.toString().trim();
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    System.out.println(line);
                }
            }
            reader.close();
            int exitValue = process.waitFor();
            if (exitValue != 0) {
                throw new RuntimeException("Command failed: " + commandString);
            }
        }
    }

    private static final class PhnRecTask extends GridTaskSplitAdapter<PhnRecJob, Void> {
        private static final long serialVersionUID = 1L;

        @Override
        public Void reduce(final List<GridJobResult> results) throws GridException {
            return null;
        }

        @Override
        protected Collection<PhnRecJob> split(int gridSize, PhnRecJob job) throws GridException {
            List<PhnRecJob> jobs = new ArrayList<PhnRecJob>();
            jobs.add(job);
            return jobs;
        }
    }
    
    public static void main(final String[] args) throws Exception {
        PhnRecJob job1 = new PhnRecJob("/SRE04/jaaa.sph", 0);
        job1.execute();
//        PhnRecJob job2 = new PhnRecJob("/SRE04/jaaa.sph", 1);
//        job2.execute();
    }

    public static void main2(final String[] args) throws Exception {
        System.setProperty("GRIDGAIN_HOME", System.getProperty("user.dir"));
        System.setProperty("gridgain.update.notifier", "false");

        final File baseDirectory = new File("C:\\temp\\data").getAbsoluteFile();
        final int baseDirLength = baseDirectory.getAbsolutePath().length();
        final String[] workDirectories = {"SRE04", "SRE06"};
        final FilenameFilter filter = new FilenameSuffixFilter(".sph", true);
        final boolean recurse = true;

        GridConfigurationAdapter cfg = new GridConfigurationAdapter();
        setBasicConfiguration(cfg);
        ExecutorService executorService = Executors.newCachedThreadPool();
        cfg.setExecutorService(executorService);
        final Grid grid = GridFactory.start(cfg);
        try {
            // sleep for a while so that remove nodes can be discovered
            Thread.sleep(10000L);
            // submit jobs
            List<GridTaskFuture<Void>> futures = new ArrayList<GridTaskFuture<Void>>();
            for (String workDir : workDirectories) {
                File path = new File(baseDirectory, workDir);
                for (File audioFile : FileUtils.listFiles(path, filter, recurse)) {
                    AudioFileFormat format = AudioSystem.getAudioFileFormat(audioFile);
                    for (int channel = 0; channel < format.getFormat().getChannels(); channel++) {
                        File mlfFile = new File(audioFile.getAbsolutePath() + "." + channel + ".mlf");
                        if (!mlfFile.exists()) {
                            String name = audioFile.getAbsolutePath().substring(baseDirLength + 1);
                            name = "/" + name.replace('\\', '/');
                            System.out.println(name);
                            PhnRecJob job = new PhnRecJob(name, channel);
                            GridTaskFuture<Void> future = grid.execute(PhnRecTask.class, new PhnRecJob(null, 0));
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

    private static void setBasicConfiguration(final GridConfigurationAdapter cfg) {
        cfg.setGridName("grid");
        GridBasicTopologySpi topologySpi = new GridBasicTopologySpi();
        topologySpi.setLocalNode(true);
        topologySpi.setRemoteNodes(false);
        cfg.setTopologySpi(topologySpi);
        cfg.setPeerClassLoadingEnabled(true);
        GridFifoQueueCollisionSpi collisionSpi = new GridFifoQueueCollisionSpi();
        collisionSpi.setParallelJobsNumber(1);
        cfg.setCollisionSpi(collisionSpi);
        cfg.setFailoverSpi(new GridNeverFailoverSpi());
    }
}
