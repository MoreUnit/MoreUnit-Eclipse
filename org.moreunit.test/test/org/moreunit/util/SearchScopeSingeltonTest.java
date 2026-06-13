package org.moreunit.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SearchScopeSingeltonTest {

    @Test
    public void getInstance_should_return_same_instance() {
        SearchScopeSingelton instance1 = SearchScopeSingelton.getInstance();
        SearchScopeSingelton instance2 = SearchScopeSingelton.getInstance();

        assertThat(instance1).isNotNull();
        assertThat(instance1).isSameAs(instance2);
    }

    @Test
    public void resetCachedSearchScopes_should_clear_cache() {
        SearchScopeSingelton instance = SearchScopeSingelton.getInstance();
        instance.resetCachedSearchScopes();
    }
}
