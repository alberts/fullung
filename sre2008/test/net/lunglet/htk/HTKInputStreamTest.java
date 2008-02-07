package net.lunglet.htk;

import static org.junit.Assert.assertNotNull;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;

public final class HTKInputStreamTest {
    @Test
    public void test() throws IOException {
        InputStream stream = getClass().getResourceAsStream("mfcc.htk");
        assertNotNull(stream);
        HTKInputStream in = new HTKInputStream(stream);
        in.readMFCC();
        in.close();
    }
}
