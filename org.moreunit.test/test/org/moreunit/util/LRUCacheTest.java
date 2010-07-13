package org.moreunit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LRUCacheTest
{
    @Test
    public void testLastReadEntryIsRemovedWhenAddingEntryAfterMaxSizeIsReached()
    {
        LRUCache<String, String> cache = new LRUCache<String, String>(2);
        assertTrue(cache.isEmpty());

        cache.put("key1", "val1");
        assertEquals(1, cache.size());
        cache.put("key2", "val2");
        assertEquals(2, cache.size());
        assertEquals("val1", cache.get("key1"));
        assertEquals("val2", cache.get("key2"));

        cache.put("key3", "val3");
        assertEquals(2, cache.size());
        assertFalse(cache.containsKey("key1"));
        assertEquals("val2", cache.get("key2"));
        assertEquals("val3", cache.get("key3"));

        cache.put("key2", "val2B");
        assertEquals(2, cache.size());
        assertEquals("val3", cache.get("key3"));
        assertEquals("val2B", cache.get("key2"));

        cache.put("key4", "val4");
        assertEquals(2, cache.size());
        assertFalse(cache.containsKey("key3"));
        assertEquals("val2B", cache.get("key2"));
        assertEquals("val4", cache.get("key4"));

        cache.put("key5", "val5");
        assertEquals(2, cache.size());
        assertFalse(cache.containsKey("key2"));
        assertEquals("val4", cache.get("key4"));
        assertEquals("val5", cache.get("key5"));
    }
}
