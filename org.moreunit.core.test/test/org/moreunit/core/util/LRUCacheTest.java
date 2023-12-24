package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.moreunit.core.util.LRUCache;

public class LRUCacheTest
{
    @Test
    public void should_remove_last_read_entry_when_adding_entry_after_max_size_is_reached()
    {
        LRUCache<String, String> cache = new LRUCache<String, String>(2);
        assertThat(cache.isEmpty()).isTrue();

        cache.put("key1", "val1");
        assertThat(cache).hasSize(1);
        cache.put("key2", "val2");
        assertThat(cache).hasSize(2);
        assertThat(cache.get("key1")).isEqualTo("val1");
        assertThat(cache.get("key2")).isEqualTo("val2");

        cache.put("key3", "val3");
        assertThat(cache).hasSize(2);
        assertThat(cache.containsKey("key1")).isFalse();
        assertThat(cache.get("key2")).isEqualTo("val2");
        assertThat(cache.get("key3")).isEqualTo("val3");

        cache.put("key2", "val2B");
        assertThat(cache).hasSize(2);
        assertThat(cache.get("key3")).isEqualTo("val3");
        assertThat(cache.get("key2")).isEqualTo("val2B");

        cache.put("key4", "val4");
        assertThat(cache).hasSize(2);
        assertThat(cache.containsKey("key3")).isFalse();
        assertThat(cache.get("key2")).isEqualTo("val2B");
        assertThat(cache.get("key4")).isEqualTo("val4");

        cache.put("key5", "val5");
        assertThat(cache).hasSize(2);
        assertThat(cache.containsKey("key2")).isFalse();
        assertThat(cache.get("key4")).isEqualTo("val4");
        assertThat(cache.get("key5")).isEqualTo("val5");
    }
}
