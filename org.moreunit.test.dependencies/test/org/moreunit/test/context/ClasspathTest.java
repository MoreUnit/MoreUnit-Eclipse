package org.moreunit.test.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.api.Condition;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;
import org.moreunit.test.context.configs.SimpleJUnit3Project;
import org.moreunit.test.context.configs.SimpleJUnit4Project;
import org.moreunit.test.context.configs.SimpleJUnit5Project;
import org.moreunit.test.context.configs.SimpleTestNGProject;

public class ClasspathTest extends ContextTestCase
{
    @Project(SimpleJUnit4Project.class)
    @Test
    public void should_add_junit4_lib_to_classpath_when_junit4_is_used() throws JavaModelException
    {
        IPackageFragmentRoot[] packageFragmentRoots = context.getProjectHandler().get().getPackageFragmentRoots();
        assertThat(packageFragmentRoots).extracting("elementName").contains("junit.jar");
    }

    @Project(SimpleJUnit5Project.class)
    @Test
    public void should_add_junit5_lib_to_classpath_when_junit5_is_used() throws JavaModelException
    {
        IPackageFragmentRoot[] packageFragmentRoots = context.getProjectHandler().get().getPackageFragmentRoots();
        assertThat(packageFragmentRoots).extracting("elementName").satisfies(anyMatchOnPattern("org\\.junit\\.jupiter\\.api.*\\.jar"));
    }

    @Project(SimpleJUnit3Project.class)
    @Test
    public void should_add_junit3_lib_to_classpath_when_junit3_is_used() throws JavaModelException
    {
        IPackageFragmentRoot[] packageFragmentRoots = context.getProjectHandler().get().getPackageFragmentRoots();
        assertThat(packageFragmentRoots).extracting("elementName").contains("junit.jar");
    }

    @Project(SimpleTestNGProject.class)
    @Test
    public void should_add_testng_lib_to_classpath_when_testng_is_used() throws JavaModelException
    {
        IPackageFragmentRoot[] packageFragmentRoots = context.getProjectHandler().get().getPackageFragmentRoots();
        for(IPackageFragmentRoot root : packageFragmentRoots)
            System.out.println(root.getElementName());
        assertThat(packageFragmentRoots).extracting("elementName").contains("testng.jar");
    }

    private Condition<List<? extends Object>> anyMatchOnPattern(final String pattern)
    {
        return new Condition<List<? extends Object>>()
        {
            @Override
            public boolean matches(List<? extends Object> elements)
            {
                for (Object element : elements) {
                    if (String.valueOf(element).matches(pattern)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
