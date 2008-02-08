package net.lunglet.htk;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class HTKOutputStream extends DataOutputStream {
    public HTKOutputStream(final OutputStream out) {
        super(out);
    }

    public HTKOutputStream(final String name) throws FileNotFoundException {
        this(new BufferedOutputStream(new FileOutputStream(name)));
    }

    public void writeWave(final byte[] b, final int sampPeriod) throws IOException {
        if (b.length % 2 != 0) {
            throw new IllegalArgumentException();
        }
        // write number of samples
        writeInt(b.length / 2);
        writeInt(sampPeriod);
        // write sample size
        writeShort(2);
        // write parameter kind
        writeShort(HTKConstants.WAVEFORM);
        // write buffer in big-endian order
        for (int i = 0; i < b.length - 1; i += 2) {
            write(b[i + 1]);
            write(b[i]);
        }
    }
}
