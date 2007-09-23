package cz.vutbr.fit.speech.phnrec;

import java.io.File;
import java.io.FilenameFilter;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class PhnRecServerMain {
    private static final Log LOG = LogFactory.getLog(PhnRecWorkerMain.class);

    public static void main(final String[] args) throws InterruptedException {
        try {
            File inputDirectory = new File("F:/MIT/data");
            FilenameFilter filter = new FilenameSuffixFilter(".sph", true);
            File[] inputFiles =  FileUtils.listFiles(inputDirectory, filter, true);
            String brokerURL = "failover:(tcp://localhost:8080)?useExponentialBackOff=false&maxReconnectDelay=10000";
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
            Connection connection = connectionFactory.createConnection();
            PhnRecServer server = new PhnRecServer(connection);
            server.generateWorkUnits(inputFiles);
            connection.start();
            Thread.currentThread().join();
        } catch (JMSException e) {
            LOG.error("JMS failed", e);
        }
    }

    private PhnRecServerMain() {
    }
}
