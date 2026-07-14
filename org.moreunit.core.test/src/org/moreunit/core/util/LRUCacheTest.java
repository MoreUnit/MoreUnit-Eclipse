package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LRUCacheTest {

    @Test
    void testEvictsEldestEntry() {
        LRUCache<String, String> cache = new LRUCache<>(3);

        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");

        assertThat(cache.size()).isEqualTo(3);
        assertThat(cache.containsKey("key1")).isTrue();
        assertThat(cache.containsKey("key2")).isTrue();
        assertThat(cache.containsKey("key3")).isTrue();

        // This should cause key1 to be evicted
        cache.put("key4", "value4");

        assertThat(cache.size()).isEqualTo(3);
        assertThat(cache.containsKey("key1")).isFalse();
        assertThat(cache.containsKey("key2")).isTrue();
        assertThat(cache.containsKey("key3")).isTrue();
        assertThat(cache.containsKey("key4")).isTrue();

        // Access key2 so it becomes the most recently used
        cache.get("key2");

        // This should cause key3 to be evicted (as it's now the least recently used)
        cache.put("key5", "value5");

        assertThat(cache.size()).isEqualTo(3);
        assertThat(cache.containsKey("key3")).isFalse();
        assertThat(cache.containsKey("key2")).isTrue();
        assertThat(cache.containsKey("key4")).isTrue();
        assertThat(cache.containsKey("key5")).isTrue();
    }
}
