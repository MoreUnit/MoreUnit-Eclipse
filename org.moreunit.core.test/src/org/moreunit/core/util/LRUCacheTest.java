package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LRUCacheTest {

    @Test
    void testEvictsEldestEntry() {
        LRUCache<String, String> cache = new LRUCache<>(2);

        cache.put("1", "one");
        cache.put("2", "two");
        cache.put("3", "three");

        assertThat(cache.size()).isEqualTo(2);
        assertThat(cache.containsKey("1")).isFalse();
        assertThat(cache.containsKey("2")).isTrue();
        assertThat(cache.containsKey("3")).isTrue();
    }

    @Test
    void testAccessOrder() {
        LRUCache<String, String> cache = new LRUCache<>(3);

        cache.put("1", "one");
        cache.put("2", "two");
        cache.put("3", "three");

        // Access "1" to make it the most recently used
        cache.get("1");

        // Put "4", should evict "2" which is now the least recently used
        cache.put("4", "four");

        assertThat(cache.size()).isEqualTo(3);
        assertThat(cache.containsKey("2")).isFalse();
        assertThat(cache.containsKey("1")).isTrue();
        assertThat(cache.containsKey("3")).isTrue();
        assertThat(cache.containsKey("4")).isTrue();
    }
}
