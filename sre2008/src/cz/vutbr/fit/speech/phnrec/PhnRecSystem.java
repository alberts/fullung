package cz.vutbr.fit.speech.phnrec;

import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.lunglet.io.FileUtils;
import net.lunglet.util.zip.ZipUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class PhnRecSystem {
    // TODO get rid of short names at some point
    public enum PhnRecSystemId {
        PHN_CZ_SPDAT_LCRC_N1500("cz"), PHN_HU_SPDAT_LCRC_N1500("hu"), PHN_RU_SPDAT_LCRC_N1500("ru");

        private final String shortName;

        PhnRecSystemId(final String shortName) {
            this.shortName = shortName;
        }
    }

    private static String join(final Collection<? extends String> strs, final String separator) {
        StringBuilder builder = new StringBuilder();
        for (String str : strs) {
            builder.append(str);
            builder.append(separator);
        }
        return builder.toString();
    }

    private final PhnRecSystemId systemId;

    private Log log = LogFactory.getLog(PhnRecSystem.class);

    private final File phnRecExe;

    private final Set<String> phonemes;

    private final File workingDir;

    public PhnRecSystem(final PhnRecSystemId systemId) throws IOException {
        this.systemId = systemId;
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        this.workingDir = FileUtils.createTempDirectory("phnrec", null, baseDir);
        workingDir.deleteOnExit();
        if (!workingDir.isDirectory()) {
            throw new RuntimeException();
        }
        log.info("Working directory: " + workingDir);
        InputStream stream = getClass().getResourceAsStream("phnrec.zip");
        if (stream == null) {
            throw new RuntimeException("phnrec.zip not in classpath");
        }
        ZipInputStream zis = new ZipInputStream(stream);
        List<File> tempFiles = new ArrayList<File>();
        Collections.addAll(tempFiles, ZipUtils.extractAll(zis, workingDir));
        zis.close();
        String modelZip = systemId.name() + ".zip";
        stream = getClass().getResourceAsStream(modelZip);
        if (stream == null) {
            throw new RuntimeException(modelZip + " not in classpath");
        }
        zis = new ZipInputStream(stream);
        Collections.addAll(tempFiles, ZipUtils.extractAll(zis, workingDir));
        zis.close();
        for (File tempFile : tempFiles) {
            tempFile.deleteOnExit();
        }
        this.phnRecExe = new File(workingDir, System.getProperty("phnrec.exe", "phnrec.exe"));
        phnRecExe.setExecutable(true);
        log.info("phnrec executable: " + phnRecExe);
        if (!phnRecExe.isFile()) {
            throw new RuntimeException();
        }
        this.phonemes = new HashSet<String>();
        File dictsDir = new File(workingDir, "dicts");
        File phonemesFile = new File(dictsDir, "phonemes");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(phonemesFile));
            String line = reader.readLine();
            while (line != null) {
                if (phonemes.contains(line)) {
                    throw new RuntimeException("duplicate phoneme");
                }
                phonemes.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processChannel(final byte[] channelData, final ZipOutputStream out) throws IOException {
        File tempPcmFile = null;
        File tempPostFile = null;
        File tempStringsFile = null;
        try {
            tempPcmFile = File.createTempFile("pcm", ".snd", workingDir);
            tempPostFile = File.createTempFile("post", ".htk", workingDir);
            tempStringsFile = File.createTempFile("mlf", ".txt", workingDir);
            FileOutputStream fos = new FileOutputStream(tempPcmFile);
            fos.write(channelData);
            fos.close();
            waveformToPosteriors(tempPcmFile, tempPostFile);
            FloatDenseMatrix posteriors = readPosteriors(tempPostFile);
            log.info("posteriors size = [" + posteriors.rows() + ", " + posteriors.columns() + "]");
            posteriorsToStrings(tempPostFile, tempStringsFile);
            List<MasterLabel> labels = readStrings(tempStringsFile);
            PosteriorsConverter postConv = new PosteriorsConverter(posteriors, labels);
            out.putNextEntry(new ZipEntry(systemId.shortName + ".mlf"));
            postConv.writeMasterLabels(out);
            out.closeEntry();
            out.putNextEntry(new ZipEntry(systemId.shortName + ".post"));
            postConv.writePhonemePosteriors(out);
            out.closeEntry();
        } finally {
            for (File tempFile : new File[]{tempPcmFile, tempPostFile, tempStringsFile}) {
                tempFile.delete();
            }
        }
    }

    public void posteriorsToStrings(final File posteriorsFile, final File stringsFile) throws IOException {
        List<String> command = new ArrayList<String>();
        command.add(phnRecExe.getAbsolutePath());
        command.add("-v");
        command.add("-c");
        command.add(workingDir.getAbsolutePath());
        // source is posteriors in HTK format
        command.add("-s");
        command.add("post");
        command.add("-i");
        command.add(posteriorsFile.getAbsolutePath());
        // target is labeled phonemes in MLF format
        command.add("-t");
        command.add("str");
        command.add("-o");
        command.add(stringsFile.getAbsolutePath());
        runPhnRec(command);
    }

    public FloatDenseMatrix readPosteriors(final File posteriorsFile) throws IOException {
        FloatDenseMatrix posteriors = FloatDenseUtils.readHTK(posteriorsFile);
        // transpose so that frames correspond to columns
        posteriors = posteriors.transpose();
        if (posteriors.rows() != 3 * phonemes.size()) {
            throw new RuntimeException();
        }
        return posteriors;
    }

    public List<MasterLabel> readStrings(final File stringsFile) throws IOException {
        List<MasterLabel> labels = PhonemeUtil.readMasterLabels(new FileReader(stringsFile));
        for (MasterLabel label : labels) {
            if (!phonemes.contains(label.label)) {
                throw new RuntimeException("invalid phoneme: " + label);
            }
        }
        return labels;
    }

    private void runPhnRec(final List<String> command) throws IOException {
        log.info("Executing " + join(command, " "));
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();
        while (line != null) {
            log.info(line);
            line = reader.readLine();
        }
        reader.close();
        try {
            int exitValue = process.waitFor();
            if (exitValue != 0) {
                throw new RuntimeException();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void waveformToPosteriors(final File pcmFile, final File posteriorsFile) throws IOException {
        List<String> command = new ArrayList<String>();
        command.add(phnRecExe.getAbsolutePath());
        command.add("-v");
        command.add("-c");
        command.add(workingDir.getAbsolutePath());
        // source is single channel linear 16-bit PCM data
        command.add("-s");
        command.add("wf");
        command.add("-w");
        command.add("lin16");
        command.add("-i");
        command.add(pcmFile.getAbsolutePath());
        // target is posteriors in HTK format
        command.add("-t");
        command.add("post");
        command.add("-o");
        command.add(posteriorsFile.getAbsolutePath());
        runPhnRec(command);
    }
}
