package net.lunglet.features.mfcc;

import java.io.File;
import java.io.FileOutputStream;
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
import net.lunglet.util.PlatformUtils;

// TODO add parser for HTK configuration file so that we can get values like the MFCC frame length

public final class HTKMFCCBuilder {
    private static final int MFCC_FRAME_LENGTH = 200000;

    private static void copyStreamToFile(final InputStream stream, final File file) throws IOException {
        byte[] buf = new byte[16384];
        FileOutputStream fos = new FileOutputStream(file);
        try {
            int bytesRead = stream.read(buf);
            while (bytesRead >= 0) {
                fos.write(buf, 0, bytesRead);
                bytesRead = stream.read(buf);
            }
        } finally {
            fos.close();
        }
    }

    private final File hcopyFile;
    
    private final File configFile;
    
    public HTKMFCCBuilder() {
        try {
            File tempDir = new File(".");
            if (PlatformUtils.isWindows()) {
                hcopyFile = File.createTempFile("HCopy", ".exe", tempDir);
                copyStreamToFile(getClass().getResourceAsStream("HCopy.exe"), hcopyFile);
            } else {
                hcopyFile = File.createTempFile("HCopy", "", tempDir);
                copyStreamToFile(getClass().getResourceAsStream("HCopy"), hcopyFile);
                hcopyFile.setExecutable(true);
            }
            hcopyFile.deleteOnExit();
            configFile = File.createTempFile("config", ".mfcc", tempDir);
            copyStreamToFile(getClass().getResourceAsStream("config.mfcc"), configFile);
            configFile.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void runHCopy(final File waveform, final File mfcc) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        List<String> command = new ArrayList<String>();
        command.add(hcopyFile.getAbsolutePath());
        command.add("-C");
        command.add(configFile.getAbsolutePath());
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
