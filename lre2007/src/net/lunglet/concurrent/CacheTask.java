package net.lunglet.concurrent;

import net.sf.ehcache.Ehcache;

// TODO rename to HadoopCacheTask

public abstract class CacheTask<V> extends JMSXGroupTask<V> {
    private final String cacheName;

    private final String id;

    public CacheTask(final String id, final String cacheName) {
        super(id);
        this.id = id;
        this.cacheName = cacheName;
    }

    @Override
    public abstract V call() throws Exception;

    protected final Ehcache getCache() {
        return TaskCacheManager.getHadoopCache(cacheName);
    }

    protected final Object getFromCache() {
        return getCache().get((Object) id).getObjectValue();
    }
}
