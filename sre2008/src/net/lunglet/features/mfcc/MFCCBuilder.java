package net.lunglet.features.mfcc;

import com.dvsoft.sv.toolbox.matrix.GaussWarp;
import com.dvsoft.sv.toolbox.matrix.JMatrix;
import cz.vutbr.fit.speech.phnrec.MasterLabelFile;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.lunglet.htk.HTKHeader;
import net.lunglet.htk.HTKInputStream;
import net.lunglet.htk.HTKOutputStream;
import net.lunglet.sound.sampled.SphereAudioFileReader;
import net.lunglet.sound.util.SoundUtils;
import net.lunglet.util.AssertUtils;
import net.lunglet.util.LoggingUtils;

public final class MFCCBuilder {
    private static final Logger LOGGER = LoggingUtils.getLogger(MFCCBuilder.class);

    /** Audio sample rate in hertz. */
    private static final int SAMPLE_RATE = 8000;

    /** Audio frame period in HTK units. */
    private static final int AUDIO_FRAME_PERIOD = 1250;

    /** MFCC frame period in HTK units. */
    private static final int MFCC_FRAME_PERIOD = 100000;

    /** MFCC frame length in HTK units. */
    private static final int MFCC_FRAME_LENGTH = 200000;

    private final File audioFile;

    private final int channels;

    private final MasterLabelFile[] mlfs;

    private final float[][][] mfccs;

    private static File createMLFFile(final File audioFile, final int channel) {
        return new File(audioFile.getAbsolutePath() + "." + channel + ".mlf");
    }

    public MFCCBuilder(final File audioFile) throws UnsupportedAudioFileException, IOException {
        this.audioFile = audioFile;
        AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(audioFile);
        AudioFormat audioFormat = audioFileFormat.getFormat();
        int sampleRate = (int) audioFormat.getSampleRate();
        AssertUtils.assertEquals(SAMPLE_RATE, sampleRate);
        this.channels = (Integer) audioFormat.getProperty(SphereAudioFileReader.CHANNELS_PROPERTY);
        AssertUtils.assertTrue(channels == 1 || channels == 2);
        this.mlfs = new MasterLabelFile[channels];
        for (int channel = 0; channel < channels; channel++) {
            File mlfFile = createMLFFile(audioFile, channel);
            FileReader reader = new FileReader(mlfFile);
            try {
                mlfs[channel] = new MasterLabelFile(reader);
            } finally {
                reader.close();
            }
        }
        this.mfccs = new float[channels][][];
    }

    public void createBaseMFCCs() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File tempWaveFile = null;
        File tempMFCCFile = null;
        try {
            tempWaveFile = File.createTempFile("waveform", ".htk", tempDir);
            tempMFCCFile = File.createTempFile("mfcc", ".htk", tempDir);
            for (int channel = 0; channel < channels; channel++) {
                LOGGER.info("Reading channel " + channel + " from " + audioFile);
                byte[] buf = SoundUtils.readChannel(audioFile, channel);
                LOGGER.info("Writing waveform to " + tempWaveFile);
                HTKOutputStream out = new HTKOutputStream(tempWaveFile);
                try {
                    out.writeWaveform(buf, AUDIO_FRAME_PERIOD);
                } finally {
                    out.close();
                }
                LOGGER.info("Running HCopy");
                runHCopy(tempWaveFile, tempMFCCFile);
                LOGGER.info("Reading MFCC from " + tempMFCCFile);
                HTKInputStream in = new HTKInputStream(tempMFCCFile);
                try {
                    in.mark(HTKHeader.SIZE);
                    HTKHeader header = in.readHeader();
                    AssertUtils.assertEquals(MFCC_FRAME_PERIOD, header.getFramePeriodHTK());
                    AssertUtils.assertTrue(header.hasEnergy());
                    in.reset();
                    mfccs[channel] = in.readMFCC();
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
    }

    public void removeNonSpeech() {
        LOGGER.info("Removing non-speech frames");
        float[][][] validMFCCs = new float[mfccs.length][][];
        final double framePeriod = MFCC_FRAME_PERIOD / 1.0e7;
        final double frameLength = MFCC_FRAME_LENGTH / 1.0e7;
        for (int channel = 0; channel < channels; channel++) {
            final float[][] mfcc = mfccs[channel];
            final float[][] otherMFCC;
            if (channels > 0) {
                otherMFCC = mfccs[channel == 0 ? 1 : 0];
            } else {
                otherMFCC = null;
            }
            MasterLabelFile mlf = mlfs[channel];
            byte[] frameStatus = new byte[mfcc.length];
            for (int i = 0; i < mfcc.length; i++) {
                double start = framePeriod * i;
                double end = start + frameLength;
                // deal with slight mismatch in timestamps due to differences
                // with MFCC parameters used for phoneme recognizer
                if (!mlf.containsTimestamp(start) || !mlf.containsTimestamp(end)) {
                    // discard remaining frames
                    break;
                }
                if (!mlf.isOnlySpeech(start, end)) {
                    frameStatus[i] = -1;
                    continue;
                }
                if (channels == 1) {
                    frameStatus[i] = 1;
                    continue;
                }
                // cross channel squelch
                double e1 = Math.exp(mfcc[i][mfcc[i].length - 1] - 1.0);
                double e2 = Math.exp(otherMFCC[i][otherMFCC[i].length - 1] - 1.0);
                if (10.0 * Math.log10(e1/e2) < 3.0) {
                    frameStatus[i] = -2;
                }
                frameStatus[i] = 1;
            }
            int keepCount = 0;
            for (byte status : frameStatus) {
                if (status > 0) {
                    keepCount++;
                }
            }
            float[][] validMFCC = new float[keepCount][];
            for (int i = 0, j = 0; i < mfcc.length; i++) {
                if (frameStatus[i] > 0) {
                    validMFCC[j++] = mfcc[i];
                }
            }
            validMFCCs[channel] = validMFCC;
        }
        for (int channel = 0; channel < channels; channel++) {
            mfccs[channel] = validMFCCs[channel];
        }
    }

    public void gaussianize() {
        LOGGER.info("Gaussianizing features");
        for (int channel = 0; channel < channels; channel++) {
            JMatrix features = new JMatrix(mfccs[channel]);
            features = features.transpose();
            GaussWarp.warp(features);
            features = features.transpose();
            mfccs[channel] = features.toFloatArray();
        }
    }

    public void delta() {
    }

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

    public static void main(final String[] args) throws IOException, UnsupportedAudioFileException {
        MFCCBuilder mfccBuilder = new MFCCBuilder(new File("jaaa.sph"));
        mfccBuilder.createBaseMFCCs();
        mfccBuilder.removeNonSpeech();
        mfccBuilder.gaussianize();
        mfccBuilder.delta();
        mfccBuilder.delta();
        LOGGER.info("done.");
    }
}
