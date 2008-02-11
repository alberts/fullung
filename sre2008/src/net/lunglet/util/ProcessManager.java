package net.lunglet.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipInputStream;
import net.lunglet.io.FileUtils;
import net.lunglet.util.zip.ZipUtils;

public final class ProcessManager {
    private static final boolean DELETE_ON_EXIT = true;

    private static final String ZIP_SUFFIX = ".zip";

    private static void copyFile(final File src, final File dest) throws IOException {
        copyStream(new FileInputStream(src), new FileOutputStream(dest));
    }

    private static void copyStream(final InputStream in, final OutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) >= 0){
          out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    private static void deleteOnExit(final File file) {
        if (DELETE_ON_EXIT) {
            file.deleteOnExit();
        }
    }

    private final File executable;

    private final File workingDir;

    public ProcessManager(final String name) throws IOException {
        this(new String[]{name});
    }

    public ProcessManager(final String[] resources) throws IOException {
        this(resources, new File(System.getProperty("java.io.tmpdir")));
    }

    public ProcessManager(final String[] resources, final File tmpdir) throws IOException {
        this.workingDir = FileUtils.createTempDirectory("java", null, tmpdir);
        deleteOnExit(workingDir);
        if (!workingDir.isDirectory()) {
            throw new RuntimeException();
        }
        File resourceFile = new File(resources[0]);
        if (resourceFile.exists()) {
            this.executable = resourceFile;
        } else {
            File file = extractResource(resources[0]);;
            if (file == null) {
                throw new IllegalArgumentException();
            }
            this.executable = file;
        }
        for (int i = 1; i < resources.length; i++) {
            extractResource(resources[i]);
        }
    }

    private File extractResource(final String resourceName) throws IOException {
        URL url = getClass().getResource(resourceName);
        if (url.getProtocol().toLowerCase().equals("file")) {
            final File file;
            try {
                file = new File(URLDecoder.decode(url.getPath(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            if (file.getAbsolutePath().endsWith(ZIP_SUFFIX)) {
                extractZip(new FileInputStream(file));
                return null;
            } else {
                File destFile = new File(workingDir, file.getName());
                copyFile(file, destFile);
                deleteOnExit(destFile);
                return destFile;
            }
        } else {
            InputStream in = getClass().getResourceAsStream(resourceName);
            if (in == null) {
                throw new RuntimeException();
            }
            if (resourceName.endsWith(ZIP_SUFFIX)) {
                extractZip(in);
                return null;
            } else {
                String name = resourceName.substring(resourceName.lastIndexOf("/"));
                File destFile = new File(workingDir, name);
                copyStream(in, new FileOutputStream(destFile));
                deleteOnExit(destFile);
                return destFile;
            }
        }
    }

    private void extractZip(final InputStream stream) throws IOException {
        List<File> tempFiles = new ArrayList<File>();
        ZipInputStream zis = new ZipInputStream(stream);
        Collections.addAll(tempFiles, ZipUtils.extractAll(zis, workingDir));
        zis.close();
        for (File tempFile : tempFiles) {
            deleteOnExit(tempFile);
        }
    }

    public File getWorkingDirectory() {
        return workingDir;
    }

    private List<String> reallyRun(final List<String> command) throws IOException {
        List<String> output = new ArrayList<String>();
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();
        // TODO make sure we don't lose the first line
        while (line != null) {
            line = reader.readLine();
            if (line != null) {
                System.out.println(line);
                output.add(line);
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
                throw new RuntimeException("Command failed: " + commandBuilder.toString().trim());
            }
        } catch (InterruptedException e) {
            // TODO this has happened before... don't know why
            throw new RuntimeException(e);
        }
        return output;
    }

    public List<String> run(final List<String> arguments) throws IOException {
        List<String> command = new ArrayList<String>();
        command.add(executable.getAbsolutePath());
        command.addAll(arguments);
        return reallyRun(command);
    }

    public List<String> run(final String... arguments) throws IOException {
        return run(Arrays.asList(arguments));
    }
}
