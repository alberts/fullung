package cz.vutbr.fit.speech.phnrec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.lunglet.io.FileUtils;
import net.lunglet.io.FilenameSuffixFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.GridJob;
import org.gridgain.grid.GridJobAdapter;
import org.gridgain.grid.GridJobResult;
import org.gridgain.grid.GridTaskFuture;
import org.gridgain.grid.GridTaskSplitAdapter;
import org.gridgain.grid.spi.communication.GridCommunicationSpi;
import org.gridgain.grid.spi.communication.jms.GridJmsCommunicationSpi;
import org.gridgain.grid.spi.discovery.GridDiscoverySpi;
import org.gridgain.grid.spi.discovery.jms.GridJmsDiscoverySpi;
import org.gridgain.grid.spi.topology.GridTopologySpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class OldPhnRecGrid {
    private static final Log LOG = LogFactory.getLog(OldPhnRecGrid.class);

    private static final int HTTP_PORT = 31491;

    private static final String INITIAL_CONTEXT_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

//     private static final String PROVIDER_URL = "tcp://localhost:39210";

    public static final class PhnRecWorkUnit implements Serializable {
        private static final long serialVersionUID = 1L;

        private String file;

        private int channel;

        private byte[] buf;

        public PhnRecWorkUnit(final String file, final int channel, final byte[] buf) {
            this.file = file;
            this.channel = channel;
            this.buf = buf;
        }

        public PhnRecWorkUnit(final File file, final int channel) {
            try {
                this.file = file.getCanonicalPath();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.channel = channel;
        }

        public boolean isDone() {
            return new File(file + "_" + channel + ".phnrec.zip").exists();
        }
    }

    public static final class PhnRecTask extends GridTaskSplitAdapter<List<PhnRecWorkUnit>> {
        private static final long serialVersionUID = 1L;

        @Override
        public Collection<? extends GridJob> split(final int gridSize, final List<PhnRecWorkUnit> workunits)
                throws GridException {
            List<GridJob> jobs = new ArrayList<GridJob>();
            for (PhnRecWorkUnit workunit : workunits) {
                jobs.add(new GridJobAdapter<PhnRecWorkUnit>(workunit) {
                    private static final long serialVersionUID = 1L;

                    public Serializable execute() throws GridException {
                        PhnRecWorkUnit workunit = getArgument();
//                        HttpClient httpclient = new HttpClient();
//                        HttpMethod method = new GetMethod("http://localhost:" + HTTP_PORT + "/phnrec");
//                        method.setQueryString(new NameValuePair[]{new NameValuePair("file", workunit.file),
//                                new NameValuePair("channel", Integer.toString(workunit.channel))});
//                        final byte[] buf;
//                        try {
//                            httpclient.executeMethod(method);
//                            if (method.getStatusCode() != 200) {
//                                throw new RuntimeException("HTTP GET failed");
//                            }
//                            buf = method.getResponseBody();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        } finally {
//                            method.releaseConnection();
//                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ZipOutputStream out = new ZipOutputStream(baos);
                        out.setLevel(9);
                        try {
                            for (PhnRecSystem system : PhnRec.PHNREC_SYSTEMS) {
//                                PhnRec.processChannel(buf, system, out);
                            }
//                            out.close();
                            throw new IOException();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
//                        String outputFile = workunit.file + "_" + workunit.channel + ".phnrec.zip";
//                        PhnRecWorkUnit result = new PhnRecWorkUnit(outputFile, 0, baos.toByteArray());
//                        return result;
                    }
                });
            }
            return jobs;
        }

        public Serializable reduce(final List<GridJobResult> results) throws GridException {
            ArrayList<PhnRecWorkUnit> validResults = new ArrayList<PhnRecWorkUnit>();
            for (GridJobResult result : results) {
                if (result.getException() != null) {
                    LOG.info("job failed", result.getException());
                }
                validResults.add((PhnRecWorkUnit) result.getData());
            }
            return validResults;
        }
    }

    private static GridDiscoverySpi createDiscoverySpi() {
        GridJmsDiscoverySpi discoSpi = new GridJmsDiscoverySpi();
        discoSpi.setConnectionFactoryName("connectionFactory");
        Map<Object, Object> env = new HashMap<Object, Object>();
        env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        // env.put(javax.naming.Context.PROVIDER_URL, PROVIDER_URL);
        discoSpi.setJndiEnvironment(env);
        discoSpi.setTopicName("topic/gridgain.discovery");
        return discoSpi;
    }

    private static GridCommunicationSpi createCommunicationSpi() {
        GridJmsCommunicationSpi commSpi = new GridJmsCommunicationSpi();
        commSpi.setConnectionFactoryName("connectionFactory");
        Map<Object, Object> env = new HashMap<Object, Object>();
        env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        // env.put(javax.naming.Context.PROVIDER_URL, PROVIDER_URL);
        commSpi.setJndiEnvironment(env);
        commSpi.setTopicName("topic/gridgain.communication");
        return commSpi;
    }

    private static GridTopologySpi createTopologySpi() {
        GridBasicTopologySpi topSpi = new GridBasicTopologySpi();
        // Exclude local node from topology.
        topSpi.setLocalNode(false);
        return topSpi;
    }
//
//    public static class PhnRecServlet extends HttpServlet {
//        protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
//                throws ServletException, IOException {
//            doGet(request, response);
//        }
//
//        protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
//                throws ServletException, IOException {
//            LOG.info("got request: " + request.getQueryString());
//            Map<?, ?> params = request.getParameterMap();
//            String file = ((String[]) params.get("file"))[0];
//            int channel = Integer.parseInt(((String[]) params.get("channel"))[0]);
//            byte[] buf = SoundUtils.readChannel(new File(file), channel);
//            response.setContentType("application/octet-stream");
//            response.getOutputStream().write(buf);
//        }
//    }

    private static List<PhnRecWorkUnit> createWorkUnits() {
        List<PhnRecWorkUnit> workunits = new ArrayList<PhnRecWorkUnit>();
        for (File inputFile : FileUtils.listFiles("G:/temp", new FilenameSuffixFilter(".sph", true))) {
            try {
                LOG.info("processing " + inputFile.getCanonicalPath());
                AudioFileFormat format = AudioSystem.getAudioFileFormat(inputFile);
                for (int channel = 0; channel < format.getFormat().getChannels(); channel++) {
                    PhnRecWorkUnit workunit = new PhnRecWorkUnit(inputFile, channel);
//                     if (!workunit.isDone()) {
//                         LOG.info("adding work unit for channel " + channel);
                         workunits.add(workunit);
//                     }
                }
            } catch (IOException e) {
                LOG.error("IOException while processing", e);
                throw new RuntimeException(e);
            } catch (UnsupportedAudioFileException e) {
                LOG.error("UnsupportedAudioFileException while processing", e);
                throw new RuntimeException(e);
            }
        }
        return workunits;
    }

    public static void main(final String[] args) throws Exception {
//        Server server = new Server(HTTP_PORT);
//        Context context = new Context(Context.SESSIONS | Context.SECURITY);
//        context.setContextPath("/");
//        context.addServlet(PhnRecServlet.class, "/phnrec");
//        server.addHandler(context);
//        server.start();
        try {
            GridConfigurationAdapter cfg = new GridConfigurationAdapter();
            cfg.setDiscoverySpi(createDiscoverySpi());
            cfg.setCommunicationSpi(createCommunicationSpi());
            cfg.setTopologySpi(createTopologySpi());
            final Grid grid = GridFactory.start(cfg);
            List<PhnRecWorkUnit> workunits = createWorkUnits();
            GridTaskFuture future = grid.execute(PhnRecTask.class.getName(), workunits);
            List<?> results = (List<?>) future.get();
            for (Object result : results) {
                PhnRecWorkUnit actualResult = (PhnRecWorkUnit) result;
                String outputFile = actualResult.file;
                if (actualResult.buf != null) {
                    LOG.info("writing " + outputFile);
                    OutputStream out = new FileOutputStream(outputFile);
                    out.write(actualResult.buf);
                    out.close();
                } else {
                    LOG.error("result for " + outputFile + " contained no data");
                }
            }
        } finally {
            GridFactory.stop(true);
//            server.stop();
//            server.join();
        }
    }
}
