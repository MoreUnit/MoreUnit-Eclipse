package org.moreunit.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class LRUCacheTest
{
    @Test
    public void should_remove_last_read_entry_when_adding_entry_after_max_size_is_reached()
    {
        LRUCache<String, String> cache = new LRUCache<>(2);
        assertTrue(cache.isEmpty());

        cache.put("key1", "val1");
        assertEquals(1, cache.size());
        cache.put("key2", "val2");
        assertEquals(2, cache.size());
        assertEquals(cache.get("key1"), "val1");
        assertEquals(cache.get("key2"), "val2");

        cache.put("key3", "val3");
        assertEquals(2, cache.size());
        assertFalse(cache.containsKey("key1"));
        assertEquals(cache.get("key2"), "val2");
        assertEquals(cache.get("key3"), "val3");

        cache.put("key2", "val2B");
        assertEquals(2, cache.size());
        assertEquals(cache.get("key3"), "val3");
        assertEquals(cache.get("key2"), "val2B");

        cache.put("key4", "val4");
        assertEquals(2, cache.size());
        assertFalse(cache.containsKey("key3"));
        assertEquals(cache.get("key2"), "val2B");
        assertEquals(cache.get("key4"), "val4");

        cache.put("key5", "val5");
        assertEquals(2, cache.size());
        assertFalse(cache.containsKey("key2"));
        assertEquals(cache.get("key4"), "val4");
        assertEquals(cache.get("key5"), "val5");
    }
}
