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
        File[] files = FileUtils.listFiles("C:/home/albert/LRE2007/keysetc/albert/output", filter);
        Set<String> names = new HashSet<String>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split("\\s+");
                String[] idparts = parts[1].split(",", 2);
                String name = "/" + idparts[0] + "/" + idparts[1];
                names.add(name);
                line = reader.readLine();
            }
            reader.close();
        }
        return names;
    }

    public static void main(final String[] args) throws IOException {
        LOG.info("starting kernel calculator");
        H5File datah5 = new H5File("F:/ngrams.h5", H5File.H5F_ACC_RDONLY);
        H5File kernelh5 = new H5File("F:/ngrams_kernel.h5");
        LOG.info("reading frontend names");
        Set<String> frontendNames = readFrontendNames();
        LOG.info("got " + frontendNames.size() + " frontend names");
        LinearKernelPrecomputer kernelComputer = new LinearKernelPrecomputer(datah5, kernelh5, 2400);
        kernelComputer.compute(frontendNames);
        kernelh5.close();
        datah5.close();
        LOG.info("kernel calculator done");
    }
}
