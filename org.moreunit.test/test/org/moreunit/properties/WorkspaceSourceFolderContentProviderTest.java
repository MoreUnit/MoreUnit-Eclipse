package org.moreunit.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.jupiter.api.Test;
import org.moreunit.core.log.Logger;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.log.LogHandler;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit4Project;
import org.moreunit.test.workspace.ProjectHandler;
import org.moreunit.test.workspace.WorkspaceHelper;



/**
 * @author gianasista
 */
@Context(SimpleJUnit4Project.class)
public class WorkspaceSourceFolderContentProviderTest extends ContextTestCase
{

    /*
     * Test for Bug 3590427 (Exception was logged on closed projects)
     */
    @Test
    public void getElements_should_not_throw_exception_when_workspace_contains_closed_projects() throws Exception
    {
        closeProjectAndPrepareMockedLoggerToThrowExcpetionWhenErrorGetsLogged();

        final ArrayList<SourceFolderMapping> list = new ArrayList<>(0);
        final WorkspaceSourceFolderContentProvider provider = new WorkspaceSourceFolderContentProvider(list);
        provider.getElements(null);
    }

    private void closeProjectAndPrepareMockedLoggerToThrowExcpetionWhenErrorGetsLogged() throws CoreException, NoSuchFieldException, IllegalAccessException
    {
        context.getProjectHandler().get().getProject().close(null);

        final Field loggerField = LogHandler.getInstance().getClass().getDeclaredField("logger");
        loggerField.setAccessible(true);

        final Logger mockedLogger = mock(Logger.class);
        doThrow(new RuntimeException("error must not get thrown on closed projects")).when(mockedLogger).error(notNull());
        loggerField.set(LogHandler.getInstance(), mockedLogger);
    }

    @Test
    public void getElements_should_return_source_folders_when_input_is_a_java_project() throws Exception
    {
        final WorkspaceSourceFolderContentProvider provider = new WorkspaceSourceFolderContentProvider(
                org.moreunit.preferences.Preferences.getInstance().getSourceMappingList(context.getProjectHandler().get()));

        final Object[] elements = provider.getElements(context.getProjectHandler().get());

        // the default test folder ("test") is filtered out, only "src" remains
        assertEquals(1, elements.length);
        assertEquals(context.getProjectHandler().getMainSrcFolderHandler().get(), elements[0]);
    }

    @Test
    public void getChildren_should_return_no_folders_for_non_project_elements() throws Exception
    {
        final WorkspaceSourceFolderContentProvider provider = new WorkspaceSourceFolderContentProvider(new ArrayList<>(0));

        assertEquals(0, provider.getChildren("not a project").length);
    }

    @Test
    public void hasChildren_should_return_false_for_non_project_elements() throws Exception
    {
        final WorkspaceSourceFolderContentProvider provider = new WorkspaceSourceFolderContentProvider(new ArrayList<>(0));

        assertFalse(provider.hasChildren("not a project"));
        assertTrue(provider.hasChildren(context.getProjectHandler().get()));
    }

    @Test
    public void getParent_should_return_java_project_of_source_folder() throws Exception
    {
        final WorkspaceSourceFolderContentProvider provider = new WorkspaceSourceFolderContentProvider(new ArrayList<>(0));

        final IJavaProject javaProject = context.getProjectHandler().get();
        assertEquals(javaProject, provider.getParent(context.getProjectHandler().getMainSrcFolderHandler().get()));
        assertNull(provider.getParent("not a source folder"));
    }

    @Test
    public void getElements_should_return_all_accessible_java_projects_of_workspace_sorted_case_insensitively() throws Exception
    {
        final ProjectHandler otherProjectHandler = context.getWorkspaceHandler().addProject("zzz-other");
        final IJavaProject otherProject = otherProjectHandler.get();
        WorkspaceHelper.createSourceFolderInProject(otherProject, "other-src");

        final WorkspaceSourceFolderContentProvider provider = new WorkspaceSourceFolderContentProvider(new ArrayList<>(0));

        final Object[] elements = provider.getElements(null);

        assertTrue(elements.length >= 2, () -> "expected at least 2 projects but got " + java.util.Arrays.toString(elements));

        final String firstProjectName = ((IJavaProject) elements[0]).getElementName();
        final String secondProjectName = ((IJavaProject) elements[1]).getElementName();
        assertTrue(firstProjectName.compareToIgnoreCase(secondProjectName) <= 0,
                () -> firstProjectName + " should sort before " + secondProjectName);
    }

    @Test
    public void getElements_should_ignore_projects_without_java_nature() throws Exception
    {
        final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        final IProject plainProject = workspaceRoot.getProject("plain-project");
        plainProject.create(null);
        plainProject.open(null);
        try
        {
            final WorkspaceSourceFolderContentProvider provider = new WorkspaceSourceFolderContentProvider(new ArrayList<>(0));

            for (final Object element : provider.getElements(null))
            {
                assertNotEquals(plainProject.getName(), ((IJavaProject) element).getElementName());
            }
        }
        finally
        {
            plainProject.delete(true, true, null);
        }
    }
}
