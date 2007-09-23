package cz.vutbr.fit.speech.phnrec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class PhnRecWorkerMain {
    private static final Log LOG = LogFactory.getLog(PhnRecWorkerMain.class);

    private static File createTempDirectory(final String prefix, final String suffix) {
        return createTempDirectory(prefix, suffix, null);
    }

    private static File createTempDirectory(final String prefix, final String suffix, final File directory) {
        try {
            File file = File.createTempFile(prefix, suffix, directory);
            if (!file.delete()) {
                throw new RuntimeException();
            }
            if (!file.mkdirs()) {
                throw new RuntimeException();
            }
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File[] extractAll(final ZipInputStream zis, final File destinationDirectory) throws IOException {
        ArrayList<File> files = new ArrayList<File>();
        byte[] buf = new byte[131072];
        ZipEntry entry = zis.getNextEntry();
        while (entry != null) {
            if (entry.isDirectory()) {
                files.add(new File(destinationDirectory, entry.getName()));
            } else {
                File directory = new File(destinationDirectory, entry.getName()).getParentFile();
                if (!directory.exists() && !directory.mkdirs()) {
                    throw new RuntimeException();
                }
                File file = new File(destinationDirectory, entry.getName());
                FileOutputStream fos = new FileOutputStream(file);
                int n;
                while ((n = zis.read(buf, 0, buf.length)) > -1) {
                    fos.write(buf, 0, n);
                }
                fos.close();
                files.add(file);
            }
            zis.closeEntry();
            entry = zis.getNextEntry();
        }
        Collections.sort(files);
        return files.toArray(new File[0]);
    }

    public static void main(final String[] args) throws IOException {
        InputStream stream = PhnRecWorkerMain.class.getResourceAsStream("phnrec.zip");
        if (stream == null) {
            throw new RuntimeException("phnrec.zip not in classpath");
        }
        ZipInputStream zis = new ZipInputStream(stream);
        String phnRecTmpDir = System.getProperty("phnrec.tmpdir", null);
        File baseTempDir = phnRecTmpDir != null ? new File(phnRecTmpDir) : null;
        File tempDir = createTempDirectory("phnrec", "", baseTempDir);
        tempDir.deleteOnExit();
        LOG.info("Extracting phnrec to " + tempDir.getCanonicalPath());
        File[] files = extractAll(zis, tempDir);
        for (File file : files) {
            file.deleteOnExit();
        }
        System.setProperty("phnrec.dir", tempDir.getCanonicalPath());
        new File(tempDir, "phnrec").setExecutable(true);
        try {
            String brokerURL = System.getProperty("broker.url", "tcp://localhost:8080");
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
            Connection connection = connectionFactory.createConnection();
            PhnRecWorker worker = new PhnRecWorker(connection);
            connection.start();
            while (true) {
                worker.requestAndDoWork();
            }
        } catch (JMSException e) {
            LOG.error("JMS failed", e);
        }
    }

    private PhnRecWorkerMain() {
    }
}
