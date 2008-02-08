package net.lunglet.htk;

import java.io.File;
import java.io.IOException;
import java.util.List;
import net.lunglet.sound.util.SoundUtils;
import net.lunglet.util.ProcessManager;
import org.junit.Test;

/*
 * <CODE>
 * close all;
 * clear all;
 * [d,fp,dt,tc,t]=readhtk('C:\temp\data\a.sph.1.mfcc1');
 * n=1:1:size(d, 1);
 * plot(n, d(:,1), 'b', n, d(:,13), 'r')
 * </CODE>
 */

public final class HTKInputStreamTest {
    @Test
    public void test() throws IOException {
        String sphereFilename = "C:\\temp\\data\\a.sph";
        int channel = 1;

        String configResource =  "/uk/ac/cam/eng/htk/config.mfcc1";
        File tmpdir = new File("C:\\temp");
        ProcessManager hcopy = new ProcessManager(new String[]{"C:\\temp\\HCopy.exe", configResource}, tmpdir);
        File waveFile = new File(hcopy.getWorkingDirectory(), "wave.htk");
        waveFile.deleteOnExit();
        String configName = configResource.substring(configResource.lastIndexOf("/") + 1);
        File configFile = new File(hcopy.getWorkingDirectory(), configName);
        String htkMFCCFilename = sphereFilename + "." + channel + ".mfcc1";

        byte[] buf = SoundUtils.readChannel(new File(sphereFilename), channel);
        HTKOutputStream out = new HTKOutputStream(waveFile.getAbsolutePath());
        // sample period of 8 kHz in 100ns units = 1250
        out.writeWave(buf, 1250);
        out.close();

        String[] arguments = {"-V", "-D", "-A", "-C", configFile.getAbsolutePath(), waveFile.getAbsolutePath(),
                htkMFCCFilename};
        List<String> output = hcopy.run(arguments);

//        for (String line : output) {
//            System.out.println(output);
//        }

//        InputStream stream = getClass().getResourceAsStream("mfcc.htk");
//        assertNotNull(stream);
//        HTKInputStream in = new HTKInputStream(stream);
//        in.readMFCC();
//        in.close();
    }
}
