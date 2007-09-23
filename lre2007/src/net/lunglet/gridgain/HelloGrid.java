package net.lunglet.gridgain;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gridgain.grid.spi.communication.GridCommunicationSpi;
import org.gridgain.grid.spi.communication.jms.GridJmsCommunicationSpi;
import org.gridgain.grid.spi.discovery.GridDiscoverySpi;
import org.gridgain.grid.spi.discovery.jms.GridJmsDiscoverySpi;
import org.gridgain.grid.spi.topology.GridTopologySpi;
import org.gridgain.grid.spi.topology.basic.GridBasicTopologySpi;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;

public final class HelloGrid {
    private static final String INITIAL_CONTEXT_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

    private static final String PROVIDER_URL = "tcp://localhost:39210";

    private static GridDiscoverySpi createDiscoverySpi() {
        GridJmsDiscoverySpi discoSpi = new GridJmsDiscoverySpi();
        discoSpi.setConnectionFactoryName("connectionFactory");
        Map<Object, Object> env = new HashMap<Object, Object>(3);
        env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put(javax.naming.Context.PROVIDER_URL, PROVIDER_URL);
        discoSpi.setJndiEnvironment(env);
        discoSpi.setTopicName("topic/gridgain.discovery");
        return discoSpi;
    }

    private static GridCommunicationSpi createCommunicationSpi() {
        GridJmsCommunicationSpi commSpi = new GridJmsCommunicationSpi();
        commSpi.setConnectionFactoryName("connectionFactory");
        Map<Object, Object> env = new HashMap<Object, Object>(3);
        env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put(javax.naming.Context.PROVIDER_URL, PROVIDER_URL);
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

    public static class MyServlet extends HttpServlet {
        protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
                throws ServletException, IOException {
            doGet(request, response);
        }

        protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
                throws ServletException, IOException {
            System.out.println(request.getRequestURI());
            System.out.println(request.getQueryString());
            response.setContentType("text/html");
            response.getWriter().print("<h1>Hello Servlet</h1>");
        }
    }

    public static void main(final String[] args) throws Exception {
        Server server = new Server(31491);
        Context context = new Context(Context.SESSIONS | Context.SECURITY);
        context.setContextPath("/");
        context.addServlet(MyServlet.class, "/hello");
        server.addHandler(context);
        server.start();

        // GridConfigurationAdapter cfg = new GridConfigurationAdapter();
        // cfg.setDiscoverySpi(createDiscoverySpi());
        // cfg.setCommunicationSpi(createCommunicationSpi());
        // cfg.setTopologySpi(createTopologySpi());
        // final Grid grid = GridFactory.start(cfg);
        // try {
        // // TODO set up data distributor JMS thing here
        // GridTaskFuture future =
        // grid.execute(GridHelloWorldTask.class.getName(), "hello");
        // Object result = future.get();
        // System.out.println(result);
        // } finally {
        // GridFactory.stop(true);
        // }

        // server.stop();
        server.join();
    }
}
