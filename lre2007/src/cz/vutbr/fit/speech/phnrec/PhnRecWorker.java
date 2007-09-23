package cz.vutbr.fit.speech.phnrec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class PhnRecWorker {
    private static final String[] PROPERTY_NAMES = {"java.vm.version", "java.vm.name", "os.arch", "user.name",
            "sun.cpu.isalist", "sun.cpu.endian", "user.country", "sun.os.patch.level"};

    private final Log log = LogFactory.getLog(PhnRecWorker.class);

    private final MessageProducer producer;

    private final Session session;

    private final String workerId;

    public PhnRecWorker(final Connection connection) throws JMSException {
        this.workerId = UUID.randomUUID().toString();
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        this.producer = session.createProducer(null);
    }

    public void close() throws JMSException {
        session.close();
    }

    // TODO when message is an ObjectMessage and the object inside is
    // deserialized, a remote classloader could be provided to load code
    // from the server
    private void doWork(final Message message) throws JMSException {
        BytesMessage bytesMessage = (BytesMessage) message;
        byte[] buf = new byte[(int) bytesMessage.getBodyLength()];
        int bytesRead = bytesMessage.readBytes(buf);
        if (bytesRead != buf.length) {
            throw new RuntimeException();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(baos);
        out.setLevel(9);
        try {
            for (PhnRecSystem system : PhnRec.PHNREC_SYSTEMS) {
                PhnRec.processChannel(buf, system, out);
            }
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BytesMessage result = session.createBytesMessage();
        result.setStringProperty("file", message.getStringProperty("file"));
        result.setIntProperty("channel", message.getIntProperty("channel"));
        result.writeBytes(baos.toByteArray());
        producer.send(PhnRecServer.WORK_RESULT_TOPIC, result);
    }

    // TODO make this an abstract method in a worker base class
    private boolean isWorkMessage(final Message message, final String correlationID) throws JMSException {
        if (!(message instanceof BytesMessage)) {
            return false;
        }
        if (!correlationID.equals(message.getJMSCorrelationID())) {
            return false;
        }
        return true;
    }

    public void requestAndDoWork() throws JMSException {
        Destination workTopic = session.createTemporaryTopic();
        MessageConsumer consumer = session.createConsumer(workTopic);
        // create request for new work
        Message request = session.createMessage();
        request.setStringProperty("WorkerId", workerId);
        setSystemPropertiesOnMessage(request);
        request.setJMSReplyTo(workTopic);
        request.setJMSCorrelationID(UUID.randomUUID().toString());
        log.info("sending work request");
        long timeout = 60000L;
        producer.send(PhnRecServer.WORK_REQUEST_TOPIC, request, DeliveryMode.NON_PERSISTENT, 0, timeout);
        Message reply = consumer.receive(timeout);
        // close the consumer so that any other work replies are discarded until
        // worker is ready to work again
        consumer.close();
        if (isWorkMessage(reply, request.getJMSCorrelationID())) {
            doWork(reply);
        } else {
            if (reply != null) {
                log.warn("invalid message: " + reply);
            } else {
                log.info("receive timed out");
            }
        }
    }

    private void setSystemPropertiesOnMessage(final Message message) throws JMSException {
        for (String key : PROPERTY_NAMES) {
            message.setStringProperty(key, System.getProperty(key));
        }
    }
}
