package net.lunglet.htk;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public final class HTKInputStream extends DataInputStream {
    public HTKInputStream(final String name) throws FileNotFoundException {
        this(new BufferedInputStream(new FileInputStream(name)));
    }

    public HTKInputStream(final InputStream stream) {
        super(stream);
    }

    private void readHeader() throws IOException {
        int nSamples = readInt();
        int sampPeriod = readInt();
        short sampSize = readShort();
        short parmKind = readShort();

        int dataType = parmKind & 0x3f;

        if ((parmKind & HTKConstants.MODIFIER_E) != 0) {
            System.out.println("ENEGRY!");
        }
        if ((parmKind & HTKConstants.MODIFIER_N) != 0) {
            System.out.println("supress abs energy");
        }
        if ((parmKind & HTKConstants.MODIFIER_D) != 0) {
            System.out.println("has deltas");
        }
        if ((parmKind & HTKConstants.MODIFIER_Z) != 0) {
            System.out.println("zero mean!");
        }

        System.out.println(nSamples);
        System.out.println(sampPeriod);
        System.out.println(sampSize);
        System.out.println(parmKind);
    }

    public Object readMFCC() throws IOException {
        readHeader();
        return null;
    }
}
