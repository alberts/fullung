package uk.ac.cam.eng.htk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.lunglet.htk.HTKOutputStream;
import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;
import net.lunglet.sound.util.SoundUtils;

public final class HCopyRunner {
    private static final String OUTPUT_SUFFIX = ".mfc1";
    
    private static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));

    private static class Task {
        File filename;

        int channel;

        boolean isDone() {
            return new File(filename + "." + channel + OUTPUT_SUFFIX).exists()
                    || new File(filename + "." + channel + OUTPUT_SUFFIX + ".gz").exists();
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
                Task task = new Task();
                task.filename = inputFile.getCanonicalFile();
                task.channel = channel;
                if (!task.isDone()) {
                    System.out.println("adding work unit for channel " + channel);
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    private static void run(final File inputFile, final File outputFile) throws IOException {
        System.out.println("Running HCopy for " + inputFile);
        File tempFile = File.createTempFile("mfc", ".htk", TEMP_DIR);
        List<String> command = new ArrayList<String>();
        command.add("HCopy.exe");
//        command.add("-D");
//        command.add("-A");
//        command.add("-V");
        command.add("-C");
        command.add("config" + OUTPUT_SUFFIX);
        command.add(inputFile.getPath());
        command.add(tempFile.getPath());
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        try {
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
            try {
                int exitValue = process.waitFor();
                if (exitValue != 0) {
                    StringBuilder commandBuilder = new StringBuilder();
                    for (String str : command) {
                        commandBuilder.append(str);
                        commandBuilder.append(" ");
                    }
                    throw new RuntimeException("command failed: " + commandBuilder.toString().trim());
                } else {
                    FileUtils.copyTo(tempFile, outputFile);
                    System.out.println("Copied from " + tempFile + " to " + outputFile);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }

    public static void main(final String[] args) throws InterruptedException, IOException,
            UnsupportedAudioFileException {
        final String path = "C:\\temp\\data";
        final int nThreads = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        List<Task> tasks = createTasks(path);
        List<Future<Void>> futures = new ArrayList<Future<Void>>();
        for (final Task task : tasks) {
            Future<Void> future = executorService.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    byte[] buf = SoundUtils.readChannel(task.filename, task.channel);
                    File tempFile = null;
                    try  {
                        tempFile = File.createTempFile("wav", ".htk", TEMP_DIR);
                        HTKOutputStream out = new HTKOutputStream(tempFile.getAbsolutePath());
                        // sample period of 8 kHz in 100ns units = 1250
                        out.writeWave(buf, 1250);
                        out.close();
                        File outFile = new File(task.filename + "." + task.channel + OUTPUT_SUFFIX);
                        run(tempFile, outFile);
                        return null;
                    } finally {
                        if (tempFile != null) {
                            tempFile.delete();
                        }
                    }
                }
            });
            futures.add(future);
        }
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (Throwable e) {
                System.err.println("HCopy failed: " + e.getMessage());
            }
        }
        executorService.shutdown();
        executorService.awaitTermination(0L, TimeUnit.MILLISECONDS);
    }
}
