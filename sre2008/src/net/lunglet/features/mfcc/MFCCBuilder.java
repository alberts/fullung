package net.lunglet.features.mfcc;

import com.dvsoft.sv.toolbox.matrix.GaussWarp;
import com.dvsoft.sv.toolbox.matrix.JMatrix;
import cz.vutbr.fit.speech.phnrec.MasterLabel;
import cz.vutbr.fit.speech.phnrec.PhonemeUtil;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.lunglet.htk.HTKHeader;
import net.lunglet.htk.HTKInputStream;
import net.lunglet.htk.HTKOutputStream;
import net.lunglet.sound.sampled.SphereAudioFileReader;
import net.lunglet.sound.util.SoundUtils;

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
    public MFCCBuilder(final int deltaWindow, final int accWindow, final boolean gaussianize,
            final boolean crossChannelSquelch) {
    }

    public static void main(final String[] args) throws IOException, UnsupportedAudioFileException {
        AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(new File("jaaa.sph"));
        AudioFormat audioFormat = audioFileFormat.getFormat();
        int sampleRate = (int) audioFormat.getSampleRate();
        if (sampleRate != 8000) {
            throw new AssertionError();
        }
        int channels = (Integer) audioFormat.getProperty(SphereAudioFileReader.CHANNELS_PROPERTY);
        if (channels == 1) {
            System.out.println("no squelch for you");
        } else if (channels == 2) {
            System.out.println("SQUELCH!");
        } else {
            throw new AssertionError();
        }

        List<MasterLabel> mlf = PhonemeUtil.readMasterLabels(new FileReader("jaaa.sph.0.mlf"));
        System.out.println(mlf.get(0));

        byte[] buf = SoundUtils.readChannel(new File("jaaa.sph"), 0);
        SoundUtils.readChannel(new File("jaaa.sph"), 1);

        File tempFile = File.createTempFile("waveform", ".htk");
        HTKOutputStream out = new HTKOutputStream(tempFile);
        out.writeWaveform(buf, 1250);
        out.close();

        File tempFile2 = File.createTempFile("mfcc", ".htk");

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

        HTKInputStream in = new HTKInputStream(tempFile2);
        HTKHeader header = in.readHeader();
        in.reset();
        float[][] mfcc = in.readMFCC();

        // if other channel is avaiable, do that too

        // read HTK header of mfcc and do some sanity checks

        // if cross channel squelch is enabled, ensure that E flag is set in HTK MFCCs
        // convert from log energy to energy... remember +1 factor

        // get sample rate from HTK header
        // gaussianization window size must be some multiple of this rate?
        // 150, 301 for 10ms features and 3 seconds window.

        JMatrix features = new JMatrix(mfcc);
        GaussWarp.warp(features);
        float[][] warpedFeatures = features.toFloatArray();
    }
}
