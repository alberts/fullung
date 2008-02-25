package net.lunglet.features.mfcc;

import cz.vutbr.fit.speech.phnrec.MasterLabelFile;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.lunglet.sound.sampled.SphereAudioFileReader;
import net.lunglet.util.AssertUtils;

// load sphere header

// load mlf

// load htk base mfcc

// check last mlf timestamp against sph header to make sure its all there

// check mfcc size against window + length info from sph header

// remove silence (as determined by mlf), giving us a bunch of blocks
// each block can just be a float[][] again
// with another big float[][] that contains all the valid stuff
//cross channel squelch: nuke features where energy differs by less than 3db
//10log10(e1/e2) > 3dB = 10log10(2) (dubbel die energy)
// do cross channel squelch, if available
// discard blocks that are too small to work with the amount of deltaing we want to do (N blocks for N sized delta window)

// leaves us with a float[][] array to gaussianize

// gaussianize across all blocks

// delta and delta-delta, etc. duplicate at boundaries like htk does

// deltawindow = 2, accwindow = 2

public class MFCCBuilder {
    private final int channels;
    
    private final MasterLabelFile[] mlfs;

    private static File createMLFFile(final File audioFile, final int channel) {
        return new File(audioFile.getAbsolutePath() + "." + channel + ".mlf");
    }

    public MFCCBuilder(final File audioFile) throws UnsupportedAudioFileException, IOException {
        AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(audioFile);
        AudioFormat audioFormat = audioFileFormat.getFormat();
        int sampleRate = (int) audioFormat.getSampleRate();
        AssertUtils.assertEquals(8000, sampleRate);
        this.channels = (Integer) audioFormat.getProperty(SphereAudioFileReader.CHANNELS_PROPERTY);
        AssertUtils.assertTrue(channels == 1 || channels == 2);
        this.mlfs = new MasterLabelFile[channels];
        for (int channel = 0; channel < channels; channel++) {
            File mlfFile = createMLFFile(audioFile, channel);
            if (!mlfFile.exists()) {
                throw new RuntimeException("Required file " + mlfFile + " doesn't exist");
            }
            mlfs[channel] = new MasterLabelFile(mlfFile);
        }
    }

    private static void runHCopy(final File src, final File tgt) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new File("C:\\home\\albert\\SRE2008\\scripts"));
        processBuilder.command("HCopy.exe", "-C", "config.mfcc", src.getAbsolutePath(), tgt.getAbsolutePath());
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
        MFCCBuilder mfccBuilder1 = new MFCCBuilder(new File("jaaa.sph"));
        MFCCBuilder mfccBuilder2 = new MFCCBuilder(new File("xdac.sph"));

//        List<MasterLabel> mlf = PhonemeUtil.readMasterLabels(new FileReader("jaaa.sph.0.mlf"));
//        System.out.println(mlf.get(0));
//
//        byte[] buf = SoundUtils.readChannel(new File("jaaa.sph"), 0);
//        SoundUtils.readChannel(new File("jaaa.sph"), 1);
//
//        File tempFile = File.createTempFile("waveform", ".htk");
//        HTKOutputStream out = new HTKOutputStream(tempFile);
//        out.writeWaveform(buf, 1250);
//        out.close();
//
//        File tempFile2 = File.createTempFile("mfcc", ".htk");

//        ProcessBuilder processBuilder = new ProcessBuilder();
//        processBuilder.redirectErrorStream(true);
////        processBuilder.environment();
////        processBuilder.command("HCopy.exe", "-C", "config.mfcc");
//        Process process = processBuilder.start();
//        try {
//            int exitValue = process.waitFor();
//            if (exitValue != 0) {
//                throw new RuntimeException();
//            }
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

//        HTKInputStream in = new HTKInputStream(tempFile2);
//        HTKHeader header = in.readHeader();
//        in.reset();
//        float[][] mfcc = in.readMFCC();
        // if other channel is avaiable, do that too

        // read HTK header of mfcc and do some sanity checks

        // if cross channel squelch is enabled, ensure that E flag is set in HTK MFCCs
        // convert from log energy to energy... remember +1 factor

        // get sample rate from HTK header
        // gaussianization window size must be some multiple of this rate?
        // 150, 301 for 10ms features and 3 seconds window.

//        JMatrix features = new JMatrix(mfcc);
//        GaussWarp.warp(features);
//        float[][] warpedFeatures = features.toFloatArray();
    }
}
