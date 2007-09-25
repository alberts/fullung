package net.lunglet.svm.jacksvm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import net.lunglet.svm.jacksvm.AbstractHandle2;
import net.lunglet.svm.jacksvm.Handle2;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.junit.Test;

import com.googlecode.array4j.FloatVector;
import com.googlecode.array4j.dense.FloatDenseVector;

public final class Handle2Test {
    @Test
    public void test() {
        CacheManager manager = CacheManager.create();
        final Cache cache = new Cache("cache", 3000, MemoryStoreEvictionPolicy.LRU, false, null, false, 10, 5, false,
                Cache.DEFAULT_EXPIRY_THREAD_INTERVAL_SECONDS, null, null);
        manager.addCache(cache);

        Handle2 handle = new AbstractHandle2("name", 123, "label") {
            @Override
            public FloatVector<?> getData() {
                Element cacheElem = cache.get(this);
                if (cacheElem != null) {
                    return (FloatVector<?>) cacheElem.getObjectValue();
                }
                cache.put(new Element(this, new FloatDenseVector(0)));
                return null;
            }
        };

        assertNull(handle.getData());
        assertNotNull(handle.getData());
        assertNotNull(handle.getData());

        Handle2 anotherHandle = new AbstractHandle2("name", 123, "label") {
            @Override
            public FloatVector<?> getData() {
                Element cacheElem = cache.get(this);
                if (cacheElem != null) {
                    return (FloatVector<?>) cacheElem.getObjectValue();
                }
                cache.put(new Element(this, new FloatDenseVector(0)));
                return null;
            }
        };

        assertNotNull(anotherHandle.getData());
        assertNotNull(anotherHandle.getData());
        assertNotNull(anotherHandle.getData());

        manager.removeCache("cache");
    }
}
