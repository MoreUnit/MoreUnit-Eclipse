package org.moreunit.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import org.mockito.stubbing.Answer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IViewSite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.properties.SwtPageTestCase;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit3Project;

/**
 * Tests {@link MissingTestsViewPart} with real SWT widgets and mocked
 * resource change events.
 */
@Context(SimpleJUnit3Project.class)
public class MissingTestsViewPartTest extends SwtPageTestCase
{
    private TestableViewPart view;

    private static class TestableViewPart extends MissingTestsViewPart
    {
        TestableViewPart(IViewSite site)
        {
            setSite(site);
        }
    }

    @BeforeEach
    public void createView()
    {
        view = new TestableViewPart(mock(IViewSite.class));
    }

    @AfterEach
    public void disposeView()
    {
        view.dispose();
    }

    private void createViewControl()
    {
        view.createPartControl(shell);
    }

    private Combo projectComboBox()
    {
        return (Combo) getField(view, "projectComboBox");
    }

    private void flushDisplayEvents()
    {
        while (display != null && display.readAndDispatch())
        {
        }
    }

    private TreeViewer spyTreeView()
    {
        TreeViewer original = (TreeViewer) getField(view, "treeViewer");
        TreeViewer spy = spy(original);
        setField(view, "treeViewer", spy);
        return spy;
    }

    private void selectProject()
    {
        Combo comboBox = projectComboBox();
        comboBox.setText(context.getProjectHandler().get().getElementName());
        Event event = new Event();
        event.widget = comboBox;
        view.widgetSelected(new SelectionEvent(event));
    }

    @Test
    public void should_create_combo_box_and_tree_with_all_java_projects()
    {
        createViewControl();

        Combo comboBox = projectComboBox();
        assertNotNull(comboBox);
        boolean containsProject = false;
        for (String item : comboBox.getItems())
        {
            containsProject |= item.equals(context.getProjectHandler().get().getElementName());
        }
        assertTrue(containsProject);

        TreeViewer treeViewer = (TreeViewer) getField(view, "treeViewer");
        assertNotNull(treeViewer.getContentProvider());
    }

    @Test
    public void should_have_no_selected_project_initially()
    {
        assertNull(view.getSelectedJavaProject());
    }

    @Test
    public void should_select_project_when_combo_box_selection_changes()
    {
        createViewControl();

        selectProject();

        assertEquals(context.getProjectHandler().get(), view.getSelectedJavaProject());
    }

    @Test
    public void should_update_combo_box_when_a_project_is_pre_deleted()
    {
        createViewControl();

        view.resourceChanged(event(IResourceChangeEvent.PRE_DELETE, null));
        flushDisplayEvents();

        boolean containsProject = false;
        for (String item : projectComboBox().getItems())
        {
            containsProject |= item.equals(context.getProjectHandler().get().getElementName());
        }
        assertTrue(containsProject);
    }

    @Test
    public void should_ignore_post_change_events_when_no_project_is_selected()
    {
        createViewControl();

        TreeViewer treeViewer = spyTreeView();
        view.resourceChanged(event(IResourceChangeEvent.POST_CHANGE, mock(IResourceDelta.class)));
        flushDisplayEvents();

        verify(treeViewer, never()).refresh();
    }

    @Test
    public void should_refresh_tree_when_a_java_file_is_added_to_selected_project()
    {
        createViewControl();
        selectProject();
        TreeViewer treeViewer = spyTreeView();

        IResourceDelta projectDelta = deltaVisiting(delta(IResourceDelta.ADDED, resource(IResource.FILE, "java")));
        IResourceDelta rootDelta = mock(IResourceDelta.class);
        when(rootDelta.findMember(context.getProjectHandler().get().getPath())).thenReturn(projectDelta);

        view.resourceChanged(event(IResourceChangeEvent.POST_CHANGE, rootDelta));
        flushDisplayEvents();

        verify(treeViewer, atLeastOnce()).refresh();
    }

    @Test
    public void should_not_refresh_tree_when_a_non_java_file_is_added_to_selected_project()
    {
        createViewControl();
        selectProject();
        TreeViewer treeViewer = spyTreeView();

        IResourceDelta projectDelta = deltaVisiting(delta(IResourceDelta.ADDED, resource(IResource.FILE, "txt")));
        IResourceDelta rootDelta = mock(IResourceDelta.class);
        when(rootDelta.findMember(context.getProjectHandler().get().getPath())).thenReturn(projectDelta);

        view.resourceChanged(event(IResourceChangeEvent.POST_CHANGE, rootDelta));
        flushDisplayEvents();

        verify(treeViewer, never()).refresh();
    }

    @Test
    public void should_not_refresh_tree_when_only_changed_files_are_reported()
    {
        createViewControl();
        selectProject();
        TreeViewer treeViewer = spyTreeView();

        IResourceDelta projectDelta = deltaVisiting(delta(IResourceDelta.CHANGED, resource(IResource.FILE, "java")));
        IResourceDelta rootDelta = mock(IResourceDelta.class);
        when(rootDelta.findMember(context.getProjectHandler().get().getPath())).thenReturn(projectDelta);

        view.resourceChanged(event(IResourceChangeEvent.POST_CHANGE, rootDelta));
        flushDisplayEvents();

        verify(treeViewer, never()).refresh();
    }

    @Test
    public void should_update_combo_box_when_a_new_project_appears_in_workspace()
    {
        createViewControl();
        selectProject();

        IResourceDelta rootDelta = mock(IResourceDelta.class);
        when(rootDelta.findMember(context.getProjectHandler().get().getPath())).thenReturn(null);
        Answer<Object> answer = invocation -> {
            IResourceDeltaVisitor visitor = (IResourceDeltaVisitor) invocation.getArgument(0);
            try
            {
                visitor.visit(delta(IResourceDelta.ADDED, resource(IResource.PROJECT, null)));
            }
            catch (org.eclipse.core.runtime.CoreException e)
            {
                throw new RuntimeException(e);
            }
            return null;
        };
        try
        {
            doAnswer(answer).when(rootDelta).accept(any(IResourceDeltaVisitor.class));
        }
        catch (org.eclipse.core.runtime.CoreException e)
        {
            throw new RuntimeException(e);
        }

        view.resourceChanged(event(IResourceChangeEvent.POST_CHANGE, rootDelta));
        flushDisplayEvents();

        boolean containsProject = false;
        for (String item : projectComboBox().getItems())
        {
            containsProject |= item.equals(context.getProjectHandler().get().getElementName());
        }
        assertTrue(containsProject);
    }

    @Test
    public void should_not_consider_project_changes_as_file_changes()
    {
        createViewControl();
        selectProject();
        TreeViewer treeViewer = spyTreeView();

        IResourceDelta projectDelta = deltaVisiting(delta(IResourceDelta.ADDED, resource(IResource.PROJECT, "java")));
        IResourceDelta rootDelta = mock(IResourceDelta.class);
        when(rootDelta.findMember(context.getProjectHandler().get().getPath())).thenReturn(projectDelta);

        view.resourceChanged(event(IResourceChangeEvent.POST_CHANGE, rootDelta));
        flushDisplayEvents();

        verify(treeViewer, never()).refresh();
    }

    @Test
    public void should_not_update_combo_box_when_no_project_changed()
    {
        createViewControl();
        selectProject();

        IResourceDelta rootDelta = mock(IResourceDelta.class);
        when(rootDelta.findMember(context.getProjectHandler().get().getPath())).thenReturn(null);
        Answer<Object> answer = invocation -> {
            IResourceDeltaVisitor visitor = (IResourceDeltaVisitor) invocation.getArgument(0);
            try
            {
                visitor.visit(delta(IResourceDelta.ADDED, resource(IResource.FILE, "java")));
                visitor.visit(delta(IResourceDelta.CHANGED, resource(IResource.PROJECT, null)));
            }
            catch (org.eclipse.core.runtime.CoreException e)
            {
                throw new RuntimeException(e);
            }
            return null;
        };
        try
        {
            doAnswer(answer).when(rootDelta).accept(any(IResourceDeltaVisitor.class));
        }
        catch (org.eclipse.core.runtime.CoreException e)
        {
            throw new RuntimeException(e);
        }

        view.resourceChanged(event(IResourceChangeEvent.POST_CHANGE, rootDelta));
        flushDisplayEvents();

        assertFalse(view.getSelectedJavaProject() == null);
    }

    private IResource resource(int type, String extension)
    {
        IResource resource = mock(IResource.class);
        when(resource.getType()).thenReturn(type);
        when(resource.getFileExtension()).thenReturn(extension);
        return resource;
    }

    private IResourceDelta delta(int kind, IResource resource)
    {
        IResourceDelta delta = mock(IResourceDelta.class);
        when(delta.getKind()).thenReturn(kind);
        when(delta.getResource()).thenReturn(resource);
        return delta;
    }

    private IResourceDelta deltaVisiting(IResourceDelta... deltas)
    {
        IResourceDelta delta = mock(IResourceDelta.class);
        Answer<Object> answer = invocation -> {
            IResourceDeltaVisitor visitor = (IResourceDeltaVisitor) invocation.getArgument(0);
            try
            {
                for (IResourceDelta child : deltas)
                {
                    visitor.visit(child);
                }
            }
            catch (org.eclipse.core.runtime.CoreException e)
            {
                throw new RuntimeException(e);
            }
            return null;
        };
        try
        {
            doAnswer(answer).when(delta).accept(any(IResourceDeltaVisitor.class));
        }
        catch (org.eclipse.core.runtime.CoreException e)
        {
            throw new RuntimeException(e);
        }
        return delta;
    }

    private IResourceChangeEvent event(int type, IResourceDelta delta)
    {
        IResourceChangeEvent event = mock(IResourceChangeEvent.class);
        when(event.getType()).thenReturn(type);
        when(event.getDelta()).thenReturn(delta);
        return event;
    }
}
