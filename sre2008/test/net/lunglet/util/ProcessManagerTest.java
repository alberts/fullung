package net.lunglet.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

public final class ProcessManagerTest {
    @Test
    public void test() throws IOException {
//        ProcessManager procManager = new ProcessManager("HCopy.exe");
//        URL url = this.getClass().getResource("HCopy.exe");
//        System.out.println(url.getProtocol());
//        new ProcessManager(new String[]{"/uk/ac/cam/eng/htk/HCopy.exe", "/uk/ac/cam/eng/htk/config.mfcc"}, "C:\\temp");
        ProcessManager procManager = new ProcessManager(new String[]{"C:\\temp\\HCopy.exe",
                "/uk/ac/cam/eng/htk/config.mfcc", "/cz/vutbr/fit/speech/phnrec/PHN_CZ_SPDAT_LCRC_N1500.zip"},
                new File("C:\\temp"));
        List<String> output = procManager.run("-V");
        for (String line : output) {
            System.out.println("X" + line);
        }
    }
}
