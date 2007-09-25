package net.lunglet.svm.jacksvm;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfigurationAdapter;
import org.gridgain.grid.GridFactory;
import org.gridgain.grid.spi.communication.GridCommunicationSpi;
import org.gridgain.grid.spi.communication.jms.GridJmsCommunicationSpi;
import org.gridgain.grid.spi.discovery.GridDiscoverySpi;
import org.gridgain.grid.spi.discovery.jms.GridJmsDiscoverySpi;
import org.gridgain.grid.spi.topology.GridTopologySpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;

public final class SvmTrainGrid {
    static final Log LOG = LogFactory.getLog(SvmTrainGrid.class);

    private static GridTopologySpi createTopologySpi() {
        GridBasicTopologySpi topSpi = new GridBasicTopologySpi();
        topSpi.setLocalNode(false);
        topSpi.setRemoteNodes(true);
        return topSpi;
    }
    
    private static GridDiscoverySpi createDiscoverySpi() {
        GridJmsDiscoverySpi discoSpi = new GridJmsDiscoverySpi();
        discoSpi.setConnectionFactoryName("connectionFactory");
        Map<Object, Object> env = new HashMap<Object, Object>(3);
//        env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
//        env.put(javax.naming.Context.PROVIDER_URL, PROVIDER_URL);
        discoSpi.setJndiEnvironment(env);
        discoSpi.setTopicName("topic/gridgain.discovery");
        return discoSpi;
    }

    private static GridCommunicationSpi createCommunicationSpi() {
        GridJmsCommunicationSpi commSpi = new GridJmsCommunicationSpi();
        commSpi.setConnectionFactoryName("connectionFactory");
        Map<Object, Object> env = new HashMap<Object, Object>(3);
//        env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
//        env.put(javax.naming.Context.PROVIDER_URL, PROVIDER_URL);
        commSpi.setJndiEnvironment(env);
        commSpi.setTopicName("topic/gridgain.communication");
        return commSpi;
    }
    
//  static final List<String> readNames(final String splitName) throws IOException {
//  String fileName = "C:/home/albert/LRE2007/keysetc/albert/output/" + splitName + ".txt";
//  List<String> names = new ArrayList<String>();
//  BufferedReader reader = new BufferedReader(new FileReader(fileName));
//  String line = reader.readLine();
//  while (line != null) {
//      String[] parts = line.split("\\s+");
//      String[] idparts = parts[1].split(",");
//      final String name = "/" + idparts[0] + "/" + idparts[1];
//      names.add(name);
//      line = reader.readLine();
//  }
//  reader.close();
//  return names;
//}
//
//static final List<Handle2> readSplit(final List<String> names, final H5File datah5) throws IOException {
//  return readData(names, datah5);
//}
//
//@Override
//public Collection<? extends GridJob> split(final int gridSize, final String arg) throws GridException {
//  List<GridJob> jobs = new ArrayList<GridJob>();
//  for (int i = 0; i < TEST_SPLITS; i++) {
//      for (int j = 0; j < BACKEND_SPLITS; j++) {
//          try {
//              String modelName = "frontend_" + i + "_" + j;
//              List<String> names = readNames(modelName);
//              jobs.add(new TrainJob(modelName, names));
//          } catch (IOException e) {
//              throw new GridException(null, e);
//          }
//      }
//  }
//  return jobs;
//}

    public static void main(final String[] args) throws Exception {
        // TODO use a gridtasklistener to queue more tasks after the initial batch
        try {
            GridConfigurationAdapter cfg = new GridConfigurationAdapter();
            cfg.setTopologySpi(createTopologySpi());
            final Grid grid = GridFactory.start(cfg);
        } finally {
            GridFactory.stop(true);
        }
    }
}
