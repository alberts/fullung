package net.lunglet.lre.lre07;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.lunglet.hdf.H5File;
import net.lunglet.io.FileUtils;
import net.lunglet.svm.jacksvm.LinearKernelPrecomputer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class CalculateKernel {
    private static final Log LOG = LogFactory.getLog(CalculateKernel.class);

    public static Set<String> readFrontendNames() throws IOException {
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("frontend_") && name.endsWith(".txt");
            }
        };
        File[] files = FileUtils.listFiles("C:/home/albert/LRE2007/keysetc/albert/mitpart2", filter);
        Set<String> names = new HashSet<String>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            try {
                String line = reader.readLine();
                while (line != null) {
                    String[] parts = line.split("\\s+");
                    String corpus = parts[0].toLowerCase();
                    String filename = parts[2];
                    String name = "/" + corpus + "/" + filename;
                    // TODO get rid of this hack
                    if (!corpus.equals("callfriend") && !filename.equals("tgtd.sph.2.30s.sph")) {
                        names.add(name);
                    }
                    line = reader.readLine();
                }
            } finally {
                reader.close();
            }
        }
        return names;
    }

    public static void main(final String[] args) throws IOException {
        LOG.info("starting kernel calculator");
        H5File datah5 = new H5File("G:/rungrams.h5", H5File.H5F_ACC_RDONLY);
        H5File kernelh5 = new H5File("G:/rungrams_kernel.h5");
        LOG.info("reading frontend names");
        Set<String> frontendNames = readFrontendNames();
        LOG.info("read " + frontendNames.size() + " frontend names");
//        int bufferSize = 2400;
        int bufferSize = 2000;
        LinearKernelPrecomputer kernelComputer = new LinearKernelPrecomputer(datah5, kernelh5, bufferSize);
        kernelComputer.compute(frontendNames);
        kernelh5.close();
        datah5.close();
        LOG.info("kernel calculator done");
    }
}
