package com.hetang.cache.factories;

import com.hetang.cache.CachePolicy;
import com.hetang.cache.policy.LRUCache;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hetashah on 6/26/16.
 */
public class ImageCacheFactory {
    public enum CacheTypes {
        LRU;
    }

    private final static Map<CacheTypes, CachePolicy> cachingMap =
            new HashMap<>(CacheTypes.values().length, 1.0f);

    static {
        /**
         * Currently we are using {@link LRUCache}.
         * You can add more caching policy implementation here.
         */
        cachingMap.put(CacheTypes.LRU, new LRUCache<String, byte[]>(0) {
            @Override
            public long sizeOf(byte[] value) {
                if (value != null) {
                    return value.length;
                }

                return 1;
            }
        });
    }

    public static CachePolicy create(CacheTypes cacheType) throws IllegalArgumentException {
        CachePolicy cachePolicy = cachingMap.get(cacheType);
        if (cachePolicy == null){
            throw new IllegalArgumentException("Not able to find " + cacheType);
        }

        return cachePolicy;
    }

    private ImageCacheFactory() {
        // avoid instantiation
    }
}
