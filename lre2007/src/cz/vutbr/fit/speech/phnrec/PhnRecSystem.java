package cz.vutbr.fit.speech.phnrec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.array4j.dense.FloatDenseMatrix;
import com.googlecode.array4j.dense.FloatDenseUtils;

public final class PhnRecSystem {
    private static String join(final Collection<? extends String> strs, final String separator) {
        StringBuilder builder = new StringBuilder();
        for (String str : strs) {
            builder.append(str);
            builder.append(separator);
        }
        return builder.toString();
    }

    private Log log = LogFactory.getLog(PhnRecSystem.class);

    private final File phnRecExe;

    private final Set<String> phonemes;

    private final String shortName;

    private final File systemConfigDir;

    public PhnRecSystem(final String name, final String shortName) {
        this.shortName = shortName;
        String phnRecDir = System.getProperty("phnrec.dir", "C:/phnrec_v2_14");
        this.systemConfigDir = new File(phnRecDir, name);
        try {
            log.info("phnrec system dir = " + systemConfigDir.getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!systemConfigDir.isDirectory()) {
            throw new RuntimeException();
        }
        this.phnRecExe = new File(phnRecDir, System.getProperty("phnrec.exe", "phnrec.exe"));
        try {
            log.info("phnrec exe = " + phnRecExe.getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!phnRecExe.isFile()) {
            throw new RuntimeException();
        }
        this.phonemes = new HashSet<String>();
        File dictsDir = new File(systemConfigDir, "dicts");
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

    public String getShortName() {
        return shortName;
    }

    public void posteriorsToStrings(final File posteriorsFile, final File stringsFile) throws IOException {
        List<String> command = new ArrayList<String>();
        command.add(phnRecExe.getAbsolutePath());
        command.add("-v");
        command.add("-c");
        command.add(systemConfigDir.getAbsolutePath());
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
//        String[] cmdarray = command.toArray(new String[0]);
//        Process process = Runtime.getRuntime().exec(cmdarray);
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
        command.add(systemConfigDir.getAbsolutePath());
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
