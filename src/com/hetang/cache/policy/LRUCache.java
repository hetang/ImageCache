package com.hetang.cache.policy;

import com.hetang.cache.CachePolicy;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hetashah on 6/25/16.
 */
public abstract class LRUCache<K, V> implements CachePolicy<K, V> {
    private Map<K, Node<K, V>> mappings;
    private Node<K, V> start;
    private Node<K, V> end;

    private int cacheLimit;
    //Considering max limit of Value is 4MB
    private long valueLimit = 4 * 1024 * 1024; // 4MB

    /**
     * Creating Doubly linked list node to make update, removal & addition in O(1) time.
     * @param <K>
     * @param <V>
     */
    private class Node<K, V> {
        private K key;
        private V value;

        private Node<K, V> next;
        private Node<K, V> previous;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
            this.previous = null;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public Node<K, V> getNext() {
            return next;
        }

        public void setNext(Node<K, V> next) {
            this.next = next;
        }

        public Node<K, V> getPrevious() {
            return previous;
        }

        public void setPrevious(Node<K, V> previous) {
            this.previous = previous;
        }
    }

    /**
     * @param cacheLimit
     */
    public LRUCache(int cacheLimit) {
        setCacheLimit(cacheLimit);
        mappings = new HashMap<K, Node<K, V>>();
    }

    /**
     * Setting cache max size at runtime. Recommended way to call this function only once.
     * Setting multiple time limit will have side effect on cache values.
     * If you do not know whats optimal value then pass {@code cacheSize} as 0 or -1.
     * @param cacheLimit
     */
    @Override
    public void setCacheLimit(int cacheLimit) {
        if (cacheLimit > 0) {
            this.cacheLimit = cacheLimit;
        } else {
            setDefaultCacheLimit();

        }
    }

    /**
     * Get default or specified CacheLimit.
     * @return
     */
    @Override
    public int getCacheLimit() {
        return this.cacheLimit;
    }

    /**
     * Get Size of Cache. This will hold the count of current active cache objects.
     * @return cacheSize
     */
    @Override
    public int size() {
        return mappings.size();
    }

    private void setDefaultCacheLimit() {
        this.cacheLimit = getDefaultCacheLimit();
    }

    /**
     * This method purpose to supply default optimal cache limit.
     * Currently it calculates based on JVM size and max allowed cache object size.
     * @return cacheLimit
     */
    @Override
    public int getDefaultCacheLimit() {
        // Get current size of heap in bytes
        final long maxMemory = Runtime.getRuntime().maxMemory();
        // Use 1/8th of the available memory for this memory cache / max object size.
        return (int) ((maxMemory / 8) / valueLimit);
    }

    /**
     * This method returns value and maintains LRU cache. Most frequently used item will be appended at the end.
     *
     * @param key
     * @return value
     */
    @Override
    public V get(K key) {
        Node<K, V> item = mappings.get(key);
        if (item == null) {
            return null;
        } else {
            updateCacheItem(item);
        }
        return item.getValue();
    }

    /**
     * This method updates the cache with newly added item to end.
     * If cache reaches it limit then it removes item from front of the list also.
     *
     * @param key
     * @param value
     * @throws OutOfMemoryError
     */
    @Override
    public void put(K key, V value) throws OutOfMemoryError {
        long size = sizeOf(value);
        if (size > valueLimit) {
            throw new OutOfMemoryError("Exceeded limit of " + valueLimit + " Bytes by " + key);
        }
        if(mappings.containsKey(key)) {
            Node<K, V> node = mappings.get(key);
            node.setValue(value);

            return;
        } else {
            Node<K, V> item = new Node<>(key, value);
            addItemInCache(key, item);
        }
    }

    private void updateCacheItem(Node<K, V> item) {
        Node<K, V> prevNode = item.getPrevious();
        Node<K, V> nextNode = item.getNext();

        if(nextNode != null) {
            if (prevNode != null) {
                prevNode.setNext(nextNode);
            } else {
                start = nextNode;
            }

            nextNode.setPrevious(prevNode);
            item.setPrevious(null);
            item.setNext(null);

            end.setNext(item);
            item.setPrevious(end);
            end = item;
        }

    }

    private void addItemInCache(K key, Node<K, V> item) {
        mappings.put(key, item);

        if (start == null) {
            start = end = item;
        } else {
            end.setNext(item);
            item.setPrevious(end);
            end = item;

            if (mappings.size() > cacheLimit) {
                mappings.remove(start.getKey());

                Node<K, V> firstItem = start.getNext();
                firstItem.setPrevious(null);
                start.setNext(null);

                start = firstItem;
            }
        }
    }

    /**
     * This method helps to calculate size of cache value.
     * If it exceed the threshold value then {@code put} will throw exception
     *
     * @param value
     * @return
     */
    protected abstract long sizeOf(V value); // This needs to return value in bytes
}
