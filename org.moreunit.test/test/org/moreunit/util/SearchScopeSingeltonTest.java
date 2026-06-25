package org.moreunit.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class SearchScopeSingeltonTest {

    @Test
    public void getInstance_should_return_same_instance() {
        SearchScopeSingelton instance1 = SearchScopeSingelton.getInstance();
        SearchScopeSingelton instance2 = SearchScopeSingelton.getInstance();

        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }

    @Test
    public void resetCachedSearchScopes_should_clear_cache() {
        SearchScopeSingelton instance = SearchScopeSingelton.getInstance();
        instance.resetCachedSearchScopes();
    }
}
