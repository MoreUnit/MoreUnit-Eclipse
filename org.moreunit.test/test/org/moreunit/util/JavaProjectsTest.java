package org.moreunit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.StructuredSelection;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;

@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "com:Foo", testCls = "com:FooTest")
public class JavaProjectsTest extends ContextTestCase
{
    @Test
    public void of_should_return_the_java_project_of_a_java_element()
    {
        final IJavaProject project = context.getProjectHandler().get();

        final Collection<IJavaProject> projects = JavaProjects.of(new StructuredSelection(context.getCompilationUnit("com.Foo")));

        assertEquals(List.of(project), List.copyOf(projects));
    }

    @Test
    public void of_should_return_the_project_of_a_non_java_selection()
    {
        final IJavaProject project = context.getProjectHandler().get();

        final Collection<IJavaProject> projects = JavaProjects.of(new StructuredSelection(project.getProject()));

        assertEquals(List.of(project), List.copyOf(projects));
    }

    @Test
    public void of_should_return_each_project_only_once()
    {
        final IJavaProject project = context.getProjectHandler().get();

        final Collection<IJavaProject> projects = JavaProjects.of(new StructuredSelection(List.of(project, context.getCompilationUnit("com.Foo"), context.getCompilationUnit("com.FooTest"))));

        assertEquals(List.of(project), List.copyOf(projects));
    }

    @Test
    public void of_should_ignore_objects_which_are_not_related_to_a_java_project()
    {
        assertTrue(JavaProjects.of(new StructuredSelection("not a project")).isEmpty());
        assertTrue(JavaProjects.of(StructuredSelection.EMPTY).isEmpty());
    }
}
