package net.lunglet.features.mfcc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.lunglet.htk.HTKHeader;
import net.lunglet.htk.HTKInputStream;
import net.lunglet.htk.HTKOutputStream;
import net.lunglet.sound.util.SoundUtils;

// TODO add parser for HTK configuration file so that we can get values like the MFCC frame length

public final class HTKMFCCBuilder {
    private static final int MFCC_FRAME_LENGTH = 200000;

    private static void runHCopy(final File waveform, final File mfcc) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        File workingDir = new File("C:\\home\\albert\\SRE2008\\scripts");
        processBuilder.directory(workingDir);
        List<String> command = new ArrayList<String>();
        command.add(new File(workingDir, "HCopy.exe").getAbsolutePath());
        command.add("-C");
        command.add(new File(workingDir, "config.mfcc").getAbsolutePath());
        command.add(waveform.getAbsolutePath());
        command.add(mfcc.getAbsolutePath());
        processBuilder.command(command);
        try {
            Process process = processBuilder.start();
            int exitValue = process.waitFor();
            if (exitValue != 0) {
                throw new RuntimeException("HCopy process failed");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Features[] apply(final InputStream stream) throws UnsupportedAudioFileException, IOException {
        AudioInputStream ais = AudioSystem.getAudioInputStream(stream);
        byte[][] channelsData = SoundUtils.readChannels(ais);
        int channels = channelsData.length;
        Features[] features = new Features[channels];
        float sampleRate = ais.getFormat().getSampleRate();
        int waveFramePeriod = (int) (1.0e7 / sampleRate);
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File tempWaveFile = null;
        File tempMFCCFile = null;
        try {
            tempWaveFile = File.createTempFile("waveform", ".htk", tempDir);
            tempMFCCFile = File.createTempFile("mfcc", ".htk", tempDir);
            for (int channel = 0; channel < channels; channel++) {
                byte[] buf = channelsData[channel];
                HTKOutputStream out = new HTKOutputStream(tempWaveFile);
                try {
                    out.writeWaveform(buf, waveFramePeriod);
                } finally {
                    out.close();
                }
                runHCopy(tempWaveFile, tempMFCCFile);
                HTKInputStream in = new HTKInputStream(tempMFCCFile);
                try {
                    in.mark(HTKHeader.SIZE);
                    HTKHeader header = in.readHeader();
                    int mfccFramePeriod = header.getFramePeriodHTK();
                    int mfccFrameLength = MFCC_FRAME_LENGTH;
                    boolean hasEnergy = header.hasEnergy();
                    in.reset();
                    float[][] mfcc = in.readMFCC();
                    features[channel] = new Features(mfcc, mfccFramePeriod, mfccFrameLength, hasEnergy);
                } finally {
                    in.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (tempWaveFile != null) {
                tempWaveFile.delete();
            }
            if (tempMFCCFile != null) {
                tempMFCCFile.delete();
            }
        }
        return features;
    }
}
