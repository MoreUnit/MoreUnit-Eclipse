package org.moreunit.core.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A cache implementing the "Least Recently Used" (LRU) algorithm: it discards
 * the least recently used items first when room is required for new entries.
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V>
{
    private static final long serialVersionUID = - 4042974769905186315L;

    private final int maxSize;

    /**
     * Constructs a new LRUCache.
     * 
     * @param maxSize The maximum number of entries this cache can contain
     */
    public LRUCache(int maxSize)
    {
        super(maxSize, 0.75f, true);
        this.maxSize = maxSize;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
    {
        return size() > this.maxSize;
    }
}
