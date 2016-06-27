package com.hetang.cache.policy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hetashah on 6/26/16.
 */
public class LRUCacheTest {
    LRUCache<Integer, Integer> cache;

    @Before
    public void setUp() throws Exception {
        cache = new LRUCache<Integer, Integer>(-1) {
            @Override
            protected long sizeOf(Integer value) {
                return Integer.SIZE;
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        cache = null;
    }

    @Test
    public void testCacheEmpty() throws Exception {
        assertNull(cache.get(1));
    }

    @Test
    public void testCacheCapacity() throws Exception {
        cache.setCacheLimit(2);

        cache.put(1, 1);
        assertEquals(1, (int) cache.get(1));
        assertNull(cache.get(2));

        cache.put(2, 2);
        assertEquals(1, (int) cache.get(1));
        assertEquals(2, (int) cache.get(2));
    }

    @Test
    public void testOldItemRemoval() throws Exception {
        cache.setCacheLimit(2);

        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);

        assertNull(cache.get(1));
        assertEquals(2, (int) cache.get(2));
        assertEquals(3, (int) cache.get(3));
    }

    @Test
    public void testLRUAlgo() throws Exception {
        cache.setCacheLimit(2);

        cache.put(1, 1);
        cache.put(2, 2);
        assertEquals(1, (int) cache.get(1));
        cache.put(3, 3);
        assertNull(cache.get(2));
        assertEquals(3, (int) cache.get(3));
    }

    @Test
    public void testCacheSizeMinimumLimit() throws Exception {
        cache.setCacheLimit(1);

        cache.put(1, 1);
        cache.put(2, 2);

        assertNull(cache.get(1));

        cache.put(3, 3);
        assertNull(cache.get(2));
        assertEquals(3, (int) cache.get(3));
    }

    @Test
    public void testEdgeCases() throws Exception {
        cache.setCacheLimit(2);

        cache.put(null , null);
        assertNull(cache.get(null));
        cache.put(null, 1);
        assertEquals(1, (int) cache.get(null));
    }

    @Test
    public void testCacheFullLimit() throws Exception {
        for(int i=1;i< 65536;i++) {
            cache.put(i ,i);
        }

        assertEquals(cache.getDefaultCacheLimit(), cache.size());
    }
}