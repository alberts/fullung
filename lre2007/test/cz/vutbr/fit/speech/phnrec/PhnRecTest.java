package cz.vutbr.fit.speech.phnrec;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public final class PhnRecTest {
    @Test
    public void test() throws JMSException, InterruptedException, IOException, UnsupportedAudioFileException {
        String brokerURL = "vm://localhost";
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
        Connection connection = connectionFactory.createConnection();

        PhnRecServer server = new PhnRecServer(connection);
        File inputDirectory = new File("F:/test");
        FilenameFilter filter = new FilenameSuffixFilter(".sph", true);
        File[] inputFiles =  FileUtils.listFiles(inputDirectory, filter, true);
        server.generateWorkUnits(inputFiles);

        PhnRecWorker worker = new PhnRecWorker(connection);

        connection.start();

        for (int i = 0; i < 20; i++) {
            worker.requestAndDoWork();
        }

        worker.close();
        server.close();
        connection.close();
    }
}
