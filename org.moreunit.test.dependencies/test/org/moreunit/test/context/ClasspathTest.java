package org.moreunit.test.context;

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;
import org.moreunit.test.context.configs.SimpleJUnit3Project;
import org.moreunit.test.context.configs.SimpleJUnit4Project;
import org.moreunit.test.context.configs.SimpleTestNGProject;

public class ClasspathTest extends ContextTestCase
{
    @Project(SimpleJUnit4Project.class)
    @Test
    public void should_add_junit4_lib_to_classpath_when_junit4_is_used() throws JavaModelException
    {
        IPackageFragmentRoot[] packageFragmentRoots = context.getProjectHandler().get().getPackageFragmentRoots();
        assertThat(packageFragmentRoots).onProperty("elementName").contains("junit.jar");
    }
    
    @Project(SimpleJUnit3Project.class)
    @Test
    public void should_add_junit3_lib_to_classpath_when_junit3_is_used() throws JavaModelException
    {
        IPackageFragmentRoot[] packageFragmentRoots = context.getProjectHandler().get().getPackageFragmentRoots();
        assertThat(packageFragmentRoots).onProperty("elementName").contains("junit.jar");
    }
    
    @Project(SimpleTestNGProject.class)
    @Test
    public void should_add_testng_lib_to_classpath_when_testng_is_used() throws JavaModelException
    {
        IPackageFragmentRoot[] packageFragmentRoots = context.getProjectHandler().get().getPackageFragmentRoots();
        for(IPackageFragmentRoot root : packageFragmentRoots)
            System.out.println(root.getElementName());
        assertThat(packageFragmentRoots).onProperty("elementName").contains("testng.jar");
    }
}
