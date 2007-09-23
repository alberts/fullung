package cz.vutbr.fit.speech.phnrec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFormat.Encoding;

import net.lunglet.sound.sampled.RawAudioFileWriter;

import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class PhnRecServer {
    private static final class WorkUnit {
        private final int channel;

        private final File file;

        public WorkUnit(final File file, final int channel) {
            this.file = file;
            this.channel = channel;
        }

        public boolean isDone() {
            try {
                File outputFile = new File(file.getCanonicalFile() + "_" + channel + ".phnrec.zip");
                return outputFile.exists();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static final Topic WORK_REQUEST_TOPIC = new ActiveMQTopic("workrequest");

    public static final Topic WORK_RESULT_TOPIC = new ActiveMQTopic("workresult");

    private final Log log = LogFactory.getLog(PhnRecServer.class);

    private final MessageProducer producer;

    private final MessageConsumer requestConsumer;

    private final MessageConsumer resultConsumer;

    private final Random rng;

    private final Session session;

    private final List<WorkUnit> workunits;

    public PhnRecServer(final Connection connection) throws JMSException {
        // use a single session so that the message listeners are never called
        // at the same time
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        this.requestConsumer = session.createConsumer(WORK_REQUEST_TOPIC);
        this.resultConsumer = session.createConsumer(WORK_RESULT_TOPIC);
        this.producer = session.createProducer(null);
        this.workunits = new ArrayList<WorkUnit>();
        this.rng = new Random(0);
        requestConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(final Message request) {
                try {
                    sendWork(request);
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        resultConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(final Message result) {
                try {
                    BytesMessage resultBytes = (BytesMessage) result;
                    byte[] buf = new byte[(int) resultBytes.getBodyLength()];
                    int bytesRead = resultBytes.readBytes(buf);
                    if (bytesRead != buf.length) {
                        // short read
                        throw new RuntimeException();
                    }
                    if (!result.propertyExists("file") || !result.propertyExists("channel")) {
                        // invalid message
                        throw new RuntimeException();
                    }
                    String file = result.getStringProperty("file");
                    int channel = result.getIntProperty("channel");
                    File outputFile = new File(file + "_" + channel + ".phnrec.zip");
                    try {
                        if (outputFile.exists()) {
                            // TODO could check that results are identical
                            log.info(outputFile.getCanonicalPath() + " already exists, not writing again");
                            return;
                        }
                        log.info("writing " + outputFile.getCanonicalPath());
                        FileOutputStream out = new FileOutputStream(outputFile);
                        out.write(buf);
                        out.close();
                    } catch (IOException e) {
                        outputFile.delete();
                        log.error("output write failed", e);
                        throw new RuntimeException(e);
                    }
                } catch (JMSException e) {
                    log.error("JMS failure", e);
                    throw new RuntimeException(e);
                }
            }
        });

        // TODO add a listener for classloading requests
    }

    public void close() throws JMSException {
        session.close();
    }

    public void generateWorkUnits(final File inputFile) {
        try {
            log.info("processing " + inputFile.getCanonicalPath());
            AudioFileFormat format = AudioSystem.getAudioFileFormat(inputFile);
            for (int channel = 0; channel < format.getFormat().getChannels(); channel++) {
                WorkUnit workunit = new WorkUnit(inputFile, channel);
                if (!workunit.isDone()) {
                    log.info("adding work unit for channel " + channel);
                    workunits.add(workunit);
                }
            }
        } catch (IOException e) {
            log.error("IOException while processing", e);
            throw new RuntimeException(e);
        } catch (UnsupportedAudioFileException e) {
            log.error("UnsupportedAudioFileException while processing", e);
            throw new RuntimeException(e);
        }
    }

    public void generateWorkUnits(final File[] files) {
        for (File file : files) {
            generateWorkUnits(file);
        }
        log.info("generated " + workunits.size() + " workunits");
    }

    private byte[] readChannelFromAudioFile(final File file, final int channel) {
        log.info("Reading channel " + channel + " from " + file);
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            AudioInputStream sourceStream = AudioSystem.getAudioInputStream(Encoding.PCM_SIGNED, stream);
            int channels = sourceStream.getFormat().getChannels();
            int sampleSizeInBits = sourceStream.getFormat().getSampleSizeInBits();
            if (sampleSizeInBits % 8 != 0) {
                throw new UnsupportedOperationException();
            }
            int sampleSizeInBytes = sampleSizeInBits >>> 3;
            if (sourceStream.getFrameLength() > Integer.MAX_VALUE) {
                throw new UnsupportedOperationException();
            }
            int frameLength = (int) sourceStream.getFrameLength();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            AudioSystem.write(sourceStream, RawAudioFileWriter.RAW, baos);
            byte[] samples = baos.toByteArray();
            byte[] channelData = new byte[sampleSizeInBytes * frameLength];
            for (int i = 0, sampleOffset = 0; i < frameLength; i++) {
                for (int j = 0; j < channels; j++) {
                    for (int k = 0; k < sampleSizeInBytes; k++, sampleOffset++) {
                        if (j == channel) {
                            int channelOffset = i * sampleSizeInBytes + k;
                            channelData[channelOffset] = samples[sampleOffset];
                        }
                    }
                }
            }
            return channelData;
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendWork(final Message request) throws JMSException {
        log.info("received work request");
        WorkUnit workunit = null;
        while (workunits.size() > 0) {
            workunit = workunits.remove(0);
            if (workunit.isDone()) {
                workunit = null;
            } else {
                break;
            }
        }
        if (workunit == null) {
            log.info("no workunits left. ignoring work request.");
            return;
        }
        // keep workunit available as long as it isn't done
        workunits.add(workunit);

        byte[] channelData = readChannelFromAudioFile(workunit.file, workunit.channel);
        BytesMessage reply = session.createBytesMessage();
        reply.writeBytes(channelData);
        try {
            reply.setStringProperty("file", workunit.file.getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        reply.setIntProperty("channel", workunit.channel);
        reply.setJMSCorrelationID(request.getJMSCorrelationID());
        producer.send(request.getJMSReplyTo(), reply);
    }
}
