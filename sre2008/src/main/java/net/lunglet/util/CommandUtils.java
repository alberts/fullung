package net.lunglet.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class CommandUtils {
    public static List<String> getInput(final String[] args, final InputStream in, final Class<?> clazz)
            throws IOException {
        List<String> lines = new ArrayList<String>();
        final BufferedReader reader;
        if (args.length > 0) {
            if (args.length != 1 || !new File(args[0]).exists()) {
                throw new RuntimeException("usage: " + clazz.getName() + " [input || < input]");
            }
            reader = new BufferedReader(new FileReader(new File(args[0])));
        } else {
            System.err.println("Reading from standard input");
            reader = new BufferedReader(new InputStreamReader(in));
        }
        try {
            String line = reader.readLine();
            while (line != null && line.trim().length() > 0) {
                lines.add(line.trim());
                line = reader.readLine();
            }
        } finally {
            reader.close();
        }
        return lines;
    }

    private CommandUtils() {
    }
}
