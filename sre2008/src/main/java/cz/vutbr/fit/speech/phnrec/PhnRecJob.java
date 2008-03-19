package cz.vutbr.fit.speech.phnrec;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipInputStream;
import net.lunglet.io.FileUtils;
import net.lunglet.sound.util.SoundUtils;
import net.lunglet.util.zip.ZipUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridJobAdapter;

public final class PhnRecJob extends GridJobAdapter<Serializable> {
    private static final File BASE_TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));
//    private static final File BASE_TEMP_DIR = new File("C:\\temp");

    private static final String FTP_HOST = "asok.dsp.sun.ac.za";
//    private static final String FTP_HOST = "localhost";

    private static final String FTP_PASSWORD = "gridgain123";

    private static final String FTP_USERNAME = "gridgain";

    private static final ThreadLocal<File> PHNREC_DIR = new ThreadLocal<File>() {
        @Override
        protected File initialValue() {
            try {
                File tempDir = FileUtils.createTempDirectory("phnrec", ".tmp", BASE_TEMP_DIR);
                deleteOnExit(tempDir);
                String resourcePrefix = "/cz/vutbr/fit/speech/phnrec/";
                InputStream[] streams = new InputStream[]{
                        PhnRecJob.class.getResourceAsStream(resourcePrefix + "PHN_CZ_SPDAT_LCRC_N1500.zip"),
                        PhnRecJob.class.getResourceAsStream(resourcePrefix + "phnrec.zip")};
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

    private Serializable reallyExecute() throws GridException {
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
            ftp.disconnect();

            byte[] sphBuf = baos.toByteArray();
            byte[] pcmBuf = SoundUtils.readChannel(new ByteArrayInputStream(sphBuf), channel);
            pcmFile = File.createTempFile("pcm", ".snd", phnrecDir);
            DataOutputStream output = new DataOutputStream(new FileOutputStream(pcmFile));
            output.write(pcmBuf);
            output.close();
            mlfFile = File.createTempFile("mlf", ".txt", phnrecDir);
            run(pcmFile, mlfFile);

            ftp.connect(FTP_HOST);
            ftp.login(FTP_USERNAME, FTP_PASSWORD);
            FileInputStream mlfStream = new FileInputStream(mlfFile);
            try {
                result = ftp.storeFile(name + "." + channel + ".mlf", mlfStream);
            } finally {
                mlfStream.close();
            }
            if (!result) {
                throw new RuntimeException("FTP store failed");
            }
            ftp.disconnect();
        } catch (IOException e) {
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

    @Override
    public Serializable execute() throws GridException {
        try {
            return reallyExecute();
        } catch (RuntimeException e) {
            throw new GridException("Job for " + name + " channel = " + channel + " failed", e);
        }
    }

    private void run(final File pcmFile, final File mlfFile) throws IOException {
        File phnrecDir = PHNREC_DIR.get();
        List<String> command = new ArrayList<String>();
//        command.add(new File(phnrecDir, "phnrec.exe").getAbsolutePath());
         command.add("/home/albert/opt/bin/phnrec");
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
        try {
            int exitValue = process.waitFor();
            if (exitValue != 0) {
                throw new RuntimeException("Command failed: " + commandString);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return name + " channel = " + channel;
    }
}
