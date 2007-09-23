package net.lunglet.concurrent;

import java.lang.management.ManagementFactory;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.ManagementContext;

public final class Master {
    public static void main(String[] args) throws Exception {
        BrokerService broker = new BrokerService();
        broker.setManagementContext(new ManagementContext(ManagementFactory.getPlatformMBeanServer()));
        broker.setPersistent(false);
        broker.setUseLocalHostBrokerName(true);
        broker.setUseJmx(true);
        broker.setUseShutdownHook(false);
        broker.addConnector("tcp://localhost:61616");
        broker.start();

        // do your thing
        Thread.sleep(1000000);

        broker.stop();
    }
}
