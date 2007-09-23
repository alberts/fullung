package net.lunglet.concurrent;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import net.sf.ehcache.management.ManagementService;

public final class TaskCacheManager {
    private static class HadoopCacheEntryFactory implements CacheEntryFactory {
        @Override
        public Object createEntry(final Object key) throws Exception {
            System.out.println("accessing hadoop to get " + key);
            return "DATA I READ FROM HADOOP = " + key.toString();
        }
    }

    private static final CacheManager MANAGER;

    static {
        MANAGER = CacheManager.create();
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ManagementService.registerMBeans(MANAGER, mBeanServer, false, false, false, true);
    }

    public static Ehcache getHadoopCache(final String cacheName) {
        synchronized (TaskCacheManager.class) {
            if (!MANAGER.cacheExists(cacheName)) {
                Cache cache = new Cache(cacheName, 5000, false, false, 5, 2);
                MANAGER.addCache(cache);
                Ehcache decoratedCache = new SelfPopulatingCache(cache, new HadoopCacheEntryFactory());
                MANAGER.replaceCacheWithDecoratedCache(cache, decoratedCache);
            }
            return MANAGER.getEhcache(cacheName);
        }
    }

    private TaskCacheManager() {
    }
}
