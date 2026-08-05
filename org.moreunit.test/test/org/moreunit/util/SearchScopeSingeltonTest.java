package org.moreunit.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class SearchScopeSingeltonTest {

    @BeforeEach
    public void setUp() {
        SearchScopeSingelton.getInstance().resetCachedSearchScopes();
    }

    @Test
    public void getSearchScope_should_cache_and_return_scope() throws CoreException {
        SearchScopeSingelton instance = SearchScopeSingelton.getInstance();

        IPackageFragmentRoot sourceFolder = mock(IPackageFragmentRoot.class);
        IJavaProject project = mock(IJavaProject.class);
        when(sourceFolder.getJavaProject()).thenReturn(project);
        when(project.getPackageFragmentRoots()).thenReturn(new IPackageFragmentRoot[] { sourceFolder });
        when(sourceFolder.isArchive()).thenReturn(false);

        // First call should create scope and cache it (goes through else branch)
        IJavaSearchScope scope1 = instance.getSearchScope(sourceFolder);
        assertNotNull(scope1);

        // Second call should return cached scope (goes through if branch)
        IJavaSearchScope scope2 = instance.getSearchScope(sourceFolder);
        assertNotNull(scope2);
        assertSame(scope1, scope2);

        // A different folder should get a new scope
        IPackageFragmentRoot sourceFolder2 = mock(IPackageFragmentRoot.class);
        when(sourceFolder2.getJavaProject()).thenReturn(project);
        when(sourceFolder2.isArchive()).thenReturn(false);

        IJavaSearchScope scope3 = instance.getSearchScope(sourceFolder2);
        assertNotNull(scope3);
        assertNotSame(scope1, scope3);
    }

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
