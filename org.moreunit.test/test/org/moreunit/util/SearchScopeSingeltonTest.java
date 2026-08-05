package org.moreunit.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.TestContextRule;
import org.moreunit.test.workspace.ProjectHandler;

public class SearchScopeSingeltonTest {

    @RegisterExtension
    public TestContextRule context = new TestContextRule();

    @BeforeEach
    public void setUp() {
        SearchScopeSingelton.getInstance().resetCachedSearchScopes();
    }

    @Project(mainSrcFolder = "src/main/java", testSrcFolder = "src/test/java")
    @Test
    public void getSearchScope_should_cache_and_return_scope() throws CoreException {
        SearchScopeSingelton instance = SearchScopeSingelton.getInstance();

        ProjectHandler testProject = context.getProjectHandler();
        IPackageFragmentRoot sourceFolder = testProject.getMainSrcFolderHandler().get();
        IPackageFragmentRoot sourceFolder2 = testProject.getTestSrcFolderHandler().get();

        // First call should create scope and cache it (goes through else branch)
        IJavaSearchScope scope1 = instance.getSearchScope(sourceFolder);
        assertNotNull(scope1);

        // Second call should return cached scope (goes through if branch)
        IJavaSearchScope scope2 = instance.getSearchScope(sourceFolder);
        assertNotNull(scope2);
        assertSame(scope1, scope2);

        // A different folder should get a new scope
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
