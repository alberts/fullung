package net.lunglet.sre2008.util;

import cz.vutbr.fit.speech.phnrec.MasterLabelFile;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;
import net.lunglet.sound.sampled.SphereAudioFileReader;
import net.lunglet.util.AssertUtils;

public final class CheckMLF {
    private static String md5sum(final InputStream in, final int size) throws IOException {
        DataInputStream dis = new DataInputStream(in);
        byte[] buf = new byte[size];
        dis.readFully(buf);
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md5.update(buf);
        StringBuilder md5StringBuilder = new StringBuilder();
        for (byte b : md5.digest()) {
            md5StringBuilder.append(String.format("%02x", b));
        }
        return md5StringBuilder.toString();
    }
    
    private static Map<String, String> readMD5sums() throws IOException {
        String[] files = {"F:\\md5sum_pt1.txt", "F:\\md5sum_pt2.txt"};
        Map<String, String> md5sums = new HashMap<String, String>();
        for (String file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split("\\s+", 2);
                AssertUtils.assertEquals(2, parts.length);
                md5sums.put(parts[1].trim(), parts[0].trim());
                line = reader.readLine();
            }
            reader.close();
        }
        return md5sums;
    }
    
    public static void main(final String[] args) throws UnsupportedAudioFileException, IOException {
        Map<String, String> md5sums = readMD5sums();
        boolean ignoreCase = true;
        boolean recurse = true;
        FilenameFilter filter = new FilenameSuffixFilter(".sph", ignoreCase);
        File[] files = FileUtils.listFiles("F:\\SRE04", filter, recurse);
        for (File file : files) {
            System.out.println("Checking " + file);
            String path = file.getAbsolutePath().replaceFirst(".*:", ".").replaceAll("\\\\", "/");
            AssertUtils.assertTrue(md5sums.containsKey(path));
            String expectedMD5sum = md5sums.get(path);
            String actualMD5sum = md5sum(new FileInputStream(file), (int) file.length());
            AssertUtils.assertTrue(expectedMD5sum.equals(actualMD5sum));
            AssertUtils.assertTrue(file.exists());
            AssertUtils.assertTrue(file.isFile());
            AssertUtils.assertTrue(file.length() > 0);
            AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
            AudioFormat audioFormat = aff.getFormat();
            int frameLength = (Integer) audioFormat.getProperty(SphereAudioFileReader.FRAME_LENGTH_PROPERTY);
            float lengthSeconds = frameLength / audioFormat.getFrameRate();
            int channels = audioFormat.getChannels();
            for (int i = 0; i < channels; i++) {
                File mlfFile = new File(file.getAbsolutePath() + "." + i + ".mlf");
                AssertUtils.assertTrue(mlfFile.exists());
                MasterLabelFile mlf = new MasterLabelFile(mlfFile);
                AssertUtils.assertTrue(mlf.containsTimestamp(0.0));
                double lastEndTime = mlf.getLastEndTime();
                AssertUtils.assertTrue(lengthSeconds >= lastEndTime);
                AssertUtils.assertTrue(lengthSeconds - lastEndTime < 32e-3);
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            ais.close();
        }
    }
}
