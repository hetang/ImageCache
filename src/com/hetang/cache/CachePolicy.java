package com.hetang.cache;

/**
 * Created by hetashah on 6/26/16.
 */
public interface CachePolicy<K, V> {
    public V get(K key);
    public void put(K key, V value) throws OutOfMemoryError;

    public void setCacheLimit(int cacheLimit);
    public int getCacheLimit();
    public int getDefaultCacheLimit();
    public int size();

}
