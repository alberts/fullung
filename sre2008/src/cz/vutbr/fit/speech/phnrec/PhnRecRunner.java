package cz.vutbr.fit.speech.phnrec;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;
import net.lunglet.sound.util.SoundUtils;
import net.lunglet.util.zip.ZipUtils;

public final class PhnRecRunner {
    private static final String OUTPUT_SUFFIX = ".mlf";

    //     private static final File BASE_TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));
    private static final File BASE_TEMP_DIR = new File("C:\\home\\albert\\temp\\temp");

    private static final class Task {
        private final File filename;

        private final int channel;

        public Task(final File filename, final int channel) {
            this.filename = filename;
            this.channel = channel;
        }

        public boolean isDone() {
            return getOutputFile().exists();
        }

        public File getFilename() {
            return filename;
        }

        public int getChannel() {
            return channel;
        }

        public File getOutputFile() {
            return new File(filename + "." + channel + OUTPUT_SUFFIX);
        }
    }

    private static final void deleteOnExit(final File file) {
        if (true) {
            file.deleteOnExit();
        }
    }

    private static List<Task> createTasks(final String path) throws IOException, UnsupportedAudioFileException {
        List<Task> tasks = new ArrayList<Task>();
        FilenameFilter filter = new FilenameSuffixFilter(".sph", true);
        boolean recurse = true;
        for (File inputFile : FileUtils.listFiles(path, filter, recurse)) {
            System.out.println("processing " + inputFile.getCanonicalPath());
            AudioFileFormat format = AudioSystem.getAudioFileFormat(inputFile);
            for (int channel = 0; channel < format.getFormat().getChannels(); channel++) {
                Task task = new Task(inputFile.getCanonicalFile(), channel);
                if (!task.isDone()) {
                    System.out.println("adding work unit for channel " + channel);
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    private static final class TaskCallable implements Callable<Void> {
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

        private final Task task;

        public TaskCallable(final Task task) {
            this.task = task;
        }

        @Override
        public Void call() throws Exception {
            File phnrecDir = PHNREC_DIR.get();
            File pcmFile = null;
            File mlfFile = null;
            try {
                pcmFile = File.createTempFile("pcm", ".snd", phnrecDir);
                byte[] buf = SoundUtils.readChannel(task.getFilename(), task.getChannel());
                DataOutputStream output = new DataOutputStream(new FileOutputStream(pcmFile));
                output.write(buf);
                output.close();
                mlfFile = File.createTempFile("mlf", ".txt", phnrecDir);
                run(phnrecDir, pcmFile, mlfFile);
                FileUtils.copyTo(mlfFile, task.getOutputFile());
            } finally {
                if (pcmFile != null) {
                    pcmFile.delete();
                }
                if (mlfFile != null) {
                    mlfFile.delete();
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

            System.out.println("Executing: " + commandString);

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

                throw new RuntimeException("command failed: " + commandString);
            }
        }
    }

    public static void main(final String[] args) throws InterruptedException, IOException,
            UnsupportedAudioFileException {
        List<Task> tasks = new ArrayList<Task>();
        tasks.addAll(createTasks("C:\\SRE2008\\SRE04"));
        tasks.addAll(createTasks("C:\\SRE2008\\SRE05"));
//        tasks.addAll(createTasks("C:\\SRE2008\\SRE06"));
//        tasks.addAll(createTasks("C:\\SRE2008\\SRE00"));
//        tasks.addAll(createTasks("C:\\SRE2008\\SRE99"));
        final int nThreads = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        List<Future<Void>> futures = new ArrayList<Future<Void>>();
        for (final Task task : tasks) {
            Future<Void> future = executorService.submit(new TaskCallable(task));
            futures.add(future);
        }
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (Throwable e) {
                System.err.println("task failed: " + e.getMessage());
            }
        }
        executorService.shutdown();
        executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
    }
}
