package org.moreunit.test.context;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.configs.SimpleJUnit3Project;
import org.moreunit.test.context.configs.SimpleJUnit4Project;
import org.moreunit.test.context.configs.SimpleJUnit5Project;
import org.moreunit.test.context.configs.SimpleTestNGProject;

@Disabled
public class ClasspathTest extends ContextTestCase
{
    @Project(SimpleJUnit4Project.class)
    @Test
    public void should_add_junit4_lib_to_classpath_when_junit4_is_used() throws JavaModelException
    {
        IPackageFragmentRoot[] packageFragmentRoots = context.getProjectHandler().get().getPackageFragmentRoots();
        assertTrue(containsElementNamed(packageFragmentRoots, "junit.jar"));
    }

    @Project(SimpleJUnit5Project.class)
    @Test
    public void should_add_junit5_lib_to_classpath_when_junit5_is_used() throws JavaModelException
    {
        IPackageFragmentRoot[] packageFragmentRoots = context.getProjectHandler().get().getPackageFragmentRoots();
        assertTrue(anyMatchOnPattern(packageFragmentRoots, "org\\.junit\\.jupiter\\.api.*\\.jar"));
    }

    @Project(SimpleJUnit3Project.class)
    @Test
    public void should_add_junit3_lib_to_classpath_when_junit3_is_used() throws JavaModelException
    {
        IPackageFragmentRoot[] packageFragmentRoots = context.getProjectHandler().get().getPackageFragmentRoots();
        assertTrue(containsElementNamed(packageFragmentRoots, "junit.jar"));
    }

    @Project(SimpleTestNGProject.class)
    @Test
    public void should_add_testng_lib_to_classpath_when_testng_is_used() throws JavaModelException
    {
        IPackageFragmentRoot[] packageFragmentRoots = context.getProjectHandler().get().getPackageFragmentRoots();
        for(IPackageFragmentRoot root : packageFragmentRoots)
        {
            System.out.println(root.getElementName());
        }
        assertTrue(containsElementNamed(packageFragmentRoots, "testng.jar"));
    }

    private static boolean containsElementNamed(IPackageFragmentRoot[] roots, String name)
    {
        for (IPackageFragmentRoot root : roots) {
            if (name.equals(root.getElementName())) {
                return true;
            }
        }
        return false;
    }

    private static boolean anyMatchOnPattern(IPackageFragmentRoot[] roots, final String pattern)
    {
        for (IPackageFragmentRoot root : roots) {
            if (root.getElementName().matches(pattern)) {
                return true;
            }
        }
        return false;
    }
}
