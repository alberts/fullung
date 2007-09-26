package cz.vutbr.fit.speech.phnrec;

import java.io.IOException;

import org.junit.Test;

import cz.vutbr.fit.speech.phnrec.PhnRecSystem.PhnRecSystemId;

public final class PhnRecSystemTest {
    @Test
    public void test() throws IOException {
        PhnRecSystem sys = new PhnRecSystem(PhnRecSystemId.PHN_CZ_SPDAT_LCRC_N1500);
    }
}
