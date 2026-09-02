package org.moreunit.core.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.moreunit.core.log.Logger;

/**
 * Tests the {@link FileMatchSelectionDialog} without opening it modally: the
 * dialog is created non-modally (JFace {@code create()}), its tree is
 * manipulated and events are simulated, then the popup shell is closed.
 */
public class FileMatchSelectionDialogTest
{
    private FileMatchSelectionDialog<IFile> dialog;

    @AfterEach
    public void closeDialog()
    {
        if(dialog != null && dialog.getShell() != null && ! dialog.getShell().isDisposed())
        {
            dialog.close();
        }
    }

    private Shell workbenchShell()
    {
        assumeTrue(PlatformUI.isWorkbenchRunning(), "Workbench is not running");
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        assumeTrue(shell != null, "No active workbench window");
        return shell;
    }

    private IFile newFile(String name, String path)
    {
        IFile file = mock(IFile.class);
        when(file.getName()).thenReturn(name);
        when(file.getFullPath()).thenReturn(new Path(path));
        return file;
    }

    private TestContentProvider newContentProvider(Object[] elements, ISelection defaultSelection)
    {
        return new TestContentProvider(elements, defaultSelection);
    }

    private FileMatchSelectionDialog<IFile> createDialog(Object[] elements, ISelection defaultSelection)
    {
        dialog = new FileMatchSelectionDialog<>("Matching files", "Some info", //
                                                   newContentProvider(elements, defaultSelection), mock(Logger.class));
        return dialog;
    }

    private Tree tree()
    {
        TreeViewer viewer = (TreeViewer) getField(dialog, "treeViewer");
        assertNotNull(viewer);
        return viewer.getTree();
    }

    private void drainEvents()
    {
        while (dialog.getShell().getDisplay().readAndDispatch())
        {
            // process all pending events
        }
    }

    @Test
    public void should_show_default_selection_after_creation()
    {
        IFile file1 = newFile("Foo.java", "/prj/src/Foo.java");
        IFile file2 = newFile("Bar.java", "/prj/src/Bar.java");
        createDialog(new Object[] { file1, file2 }, new StructuredSelection(file1));

        assertSame(file1, dialog.getSelectedElement());
        assertFalse(dialog.getShell().isDisposed());
    }

    @Test
    public void should_return_no_element_when_nothing_is_selected()
    {
        createDialog(new Object[] { newFile("Foo.java", "/prj/src/Foo.java") }, StructuredSelection.EMPTY);

        assertNull(dialog.getSelectedElement());
    }

    @Test
    public void should_close_when_escape_is_pressed()
    {
        IFile file1 = newFile("Foo.java", "/prj/src/Foo.java");
        createDialog(new Object[] { file1 }, new StructuredSelection(file1));
        Shell popup = dialog.getShell();

        Event esc = new Event();
        esc.character = SWT.ESC;
        tree().notifyListeners(SWT.KeyDown, esc);

        assertTrue(popup.isDisposed());
        // after disposal, the tree viewer is gone and no element can be returned
        assertNull(dialog.getSelectedElement());
    }

    @Test
    public void should_close_and_keep_selected_element_when_element_is_default_selected()
    {
        IFile file1 = newFile("Foo.java", "/prj/src/Foo.java");
        createDialog(new Object[] { file1 }, new StructuredSelection(file1));
        Shell popup = dialog.getShell();

        tree().notifyListeners(SWT.DefaultSelection, new Event());

        assertTrue(popup.isDisposed());
        assertSame(file1, getField(dialog, "selectedElement"));
    }

    @Test
    public void should_not_close_when_tree_action_element_provides_no_element()
    {
        boolean[] provideElementCalled = new boolean[1];
        TreeActionElement<IFile> action = new TreeActionElement<IFile>()
        {
            public boolean provideElement()
            {
                provideElementCalled[0] = true;
                return false;
            }

            public IFile execute()
            {
                throw new AssertionError("should not be called");
            }

            public String getText()
            {
                return "Create new test";
            }

            public Image getImage()
            {
                return null;
            }
        };

        IFile file1 = newFile("Foo.java", "/prj/src/Foo.java");
        createDialog(new Object[] { file1, action }, new StructuredSelection(action));

        tree().notifyListeners(SWT.DefaultSelection, new Event());

        assertTrue(provideElementCalled[0]);
        assertFalse(dialog.getShell().isDisposed(), "dialog should stay open when the action provides no element");
    }

    @Test
    public void should_close_and_keep_element_returned_by_tree_action_element()
    {
        IFile createdFile = newFile("FooTest.java", "/prj/test/FooTest.java");
        TreeActionElement<IFile> action = new TreeActionElement<IFile>()
        {
            public boolean provideElement()
            {
                return true;
            }

            public IFile execute()
            {
                return createdFile;
            }

            public String getText()
            {
                return "Create new test";
            }

            public Image getImage()
            {
                return null;
            }
        };

        IFile file1 = newFile("Foo.java", "/prj/src/Foo.java");
        createDialog(new Object[] { file1, action }, new StructuredSelection(action));
        Shell popup = dialog.getShell();

        tree().notifyListeners(SWT.DefaultSelection, new Event());

        assertTrue(popup.isDisposed());
        assertSame(createdFile, getField(dialog, "selectedElement"));
    }

    @Test
    public void should_update_tree_selection_when_mouse_moves_over_an_item()
    {
        IFile file1 = newFile("Foo.java", "/prj/src/Foo.java");
        IFile file2 = newFile("Bar.java", "/prj/src/Bar.java");
        createDialog(new Object[] { file1, file2 }, new StructuredSelection(file1));

        dialog.getShell().open();
        drainEvents();

        Tree tree = tree();
        assumeTrue(tree.getItemHeight() > 0, "Tree items are not realized");

        TreeItem item2 = tree.getItem(1);
        Rectangle bounds = item2.getBounds();
        assumeTrue(bounds.width > 0, "Tree item bounds are not available");

        Event mouseMove = new Event();
        mouseMove.x = bounds.x + 2;
        mouseMove.y = bounds.y + 2;
        tree.notifyListeners(SWT.MouseMove, mouseMove);

        assertEquals(1, tree.getSelectionCount());
        assertSame(item2, tree.getSelection()[0]);
    }

    @Test
    public void should_scroll_up_when_mouse_moves_at_top_of_tree()
    {
        IFile file1 = newFile("Foo.java", "/prj/src/Foo.java");
        IFile file2 = newFile("Bar.java", "/prj/src/Bar.java");
        createDialog(new Object[] { file1, file2 }, new StructuredSelection(file1));

        dialog.getShell().open();
        drainEvents();

        Tree tree = tree();
        assumeTrue(tree.getItemHeight() > 0, "Tree items are not realized");

        Rectangle bounds = tree.getItem(0).getBounds();
        assumeTrue(bounds.width > 0, "Tree item bounds are not available");

        // first move selects the topmost item; second move at the very top
        // triggers the scroll-up attempt (nothing to scroll: no-op)
        for (int i = 0; i < 2; i++)
        {
            Event mouseMove = new Event();
            mouseMove.x = bounds.x + 2;
            mouseMove.y = bounds.y + 1;
            tree.notifyListeners(SWT.MouseMove, mouseMove);
        }

        assertEquals(1, tree.getSelectionCount());
        assertSame(tree.getItem(0), tree.getSelection()[0]);
    }

    @Test
    public void should_scroll_down_when_mouse_moves_at_bottom_of_tree()
    {
        IFile file1 = newFile("Foo.java", "/prj/src/Foo.java");
        IFile file2 = newFile("Bar.java", "/prj/src/Bar.java");
        createDialog(new Object[] { file1, file2 }, new StructuredSelection(file1));

        dialog.getShell().open();
        drainEvents();

        Tree tree = tree();
        assumeTrue(tree.getItemCount() > 1, "Tree should contain several items");
        assumeTrue(tree.getItemHeight() > 0, "Tree items are not realized");

        Rectangle bounds = tree.getItem(1).getBounds();
        assumeTrue(bounds.width > 0, "Tree item bounds are not available");

        // first move selects the bottom item; second move triggers the
        // scroll-down attempt (nothing to scroll: no-op)
        for (int i = 0; i < 2; i++)
        {
            Event mouseMove = new Event();
            mouseMove.x = bounds.x + 2;
            mouseMove.y = bounds.y + bounds.height - 1;
            tree.notifyListeners(SWT.MouseMove, mouseMove);
        }

        assertEquals(1, tree.getSelectionCount());
        assertSame(tree.getItem(1), tree.getSelection()[0]);
    }

    @Test
    public void should_do_nothing_when_mouse_is_released_with_no_selection()
    {
        createDialog(new Object[] { newFile("Foo.java", "/prj/src/Foo.java") }, StructuredSelection.EMPTY);

        dialog.getShell().open();
        drainEvents();

        Event mouseUp = new Event();
        mouseUp.button = 1;
        mouseUp.x = 5;
        mouseUp.y = 5;
        tree().notifyListeners(SWT.MouseUp, mouseUp);

        assertFalse(dialog.getShell().isDisposed(), "dialog should stay open when nothing was selected");
    }

    @Test
    public void should_do_nothing_when_mouse_is_released_with_non_primary_button()
    {
        IFile file1 = newFile("Foo.java", "/prj/src/Foo.java");
        createDialog(new Object[] { file1 }, new StructuredSelection(file1));

        dialog.getShell().open();
        drainEvents();

        Event mouseUp = new Event();
        mouseUp.button = 2;
        mouseUp.x = 5;
        mouseUp.y = 5;
        tree().notifyListeners(SWT.MouseUp, mouseUp);

        assertFalse(dialog.getShell().isDisposed(), "dialog should stay open when another button was used");
    }

    @Test
    public void should_close_and_select_element_when_selected_item_is_clicked()
    {
        IFile file1 = newFile("Foo.java", "/prj/src/Foo.java");
        IFile file2 = newFile("Bar.java", "/prj/src/Bar.java");
        createDialog(new Object[] { file1, file2 }, new StructuredSelection(file1));

        dialog.getShell().open();
        drainEvents();

        Tree tree = tree();
        assumeTrue(tree.getItemHeight() > 0, "Tree items are not realized");

        TreeItem selectedItem = tree.getSelection()[0];
        Rectangle bounds = selectedItem.getBounds();
        assumeTrue(bounds.width > 0, "Tree item bounds are not available");

        Shell popup = dialog.getShell();

        Event mouseUp = new Event();
        mouseUp.button = 1;
        mouseUp.x = bounds.x + 2;
        mouseUp.y = bounds.y + 2;
        tree.notifyListeners(SWT.MouseUp, mouseUp);

        assertTrue(popup.isDisposed());
        assertSame(file1, getField(dialog, "selectedElement"));
    }

    @Test
    public void should_not_close_when_clicked_item_is_not_the_selected_one()
    {
        IFile file1 = newFile("Foo.java", "/prj/src/Foo.java");
        IFile file2 = newFile("Bar.java", "/prj/src/Bar.java");
        createDialog(new Object[] { file1, file2 }, new StructuredSelection(file1));

        dialog.getShell().open();
        drainEvents();

        Tree tree = tree();
        assumeTrue(tree.getItemCount() > 1, "Tree should contain several items");
        assumeTrue(tree.getItemHeight() > 0, "Tree items are not realized");

        TreeItem otherItem = tree.getItem(1);
        Rectangle bounds = otherItem.getBounds();
        assumeTrue(bounds.width > 0, "Tree item bounds are not available");

        Event mouseUp = new Event();
        mouseUp.button = 1;
        mouseUp.x = bounds.x + 2;
        mouseUp.y = bounds.y + 2;
        tree.notifyListeners(SWT.MouseUp, mouseUp);

        assertFalse(dialog.getShell().isDisposed(), "dialog should stay open when a non-selected item was clicked");
    }

    @Test
    public void should_close_after_choice_and_return_no_element_when_cancelled()
    {
        IFile file1 = newFile("Foo.java", "/prj/src/Foo.java");
        FileMatchSelectionDialog<IFile> d = createDialog(new Object[] { file1 }, new StructuredSelection(file1));
        Shell popup = d.getShell();

        // the popup is non-modal: the event loop started by getChoice() will
        // dispatch the queued close request
        d.getShell().getDisplay().asyncExec(() -> {
            if(popup != null && ! popup.isDisposed())
            {
                d.close();
            }
        });

        assertNull(d.getChoice());
        assertTrue(popup.isDisposed());
    }

    @Test
    public void should_survive_exceptions_thrown_during_event_loop()
    {
        IFile file1 = newFile("Foo.java", "/prj/src/Foo.java");
        FileMatchSelectionDialog<IFile> d = createDialog(new Object[] { file1 }, new StructuredSelection(file1));
        dialog = d;

        // the event loop is driven manually on a controlled shell: the queued
        // throwing runnable must not break the loop, which then ends when the
        // shell gets disposed by the second runnable
        Shell loopShell = new Shell(workbenchShell().getDisplay());
        loopShell.getDisplay().asyncExec(() -> {
            throw new RuntimeException("boom");
        });
        loopShell.getDisplay().asyncExec(() -> {
            if(! loopShell.isDisposed())
            {
                loopShell.dispose();
            }
        });

        invokeRunEventLoop(d, loopShell);

        assertTrue(loopShell.isDisposed(), "event loop should have ended when the shell was disposed");
    }

    private static void invokeRunEventLoop(FileMatchSelectionDialog< ? > d, Shell loopShell)
    {
        try
        {
            Method method = FileMatchSelectionDialog.class.getDeclaredMethod("runEventLoop", Shell.class);
            method.setAccessible(true);
            method.invoke(d, loopShell);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void should_use_action_element_text_and_image_in_label_provider()
    {
        Image image = workbenchShell().getDisplay().getSystemImage(SWT.ICON_INFORMATION);
        TreeActionElement<IFile> action = new TreeActionElement<IFile>()
        {
            public boolean provideElement()
            {
                return true;
            }

            public IFile execute()
            {
                return null;
            }

            public String getText()
            {
                return "Create new test";
            }

            public Image getImage()
            {
                return image;
            }
        };

        Object labelProvider = newLabelProvider();

        assertEquals("Create new test", invokeText(labelProvider, action));
        assertSame(image, invokeImage(labelProvider, action));
    }

    @Test
    public void should_display_file_name_and_folder_in_label_provider()
    {
        IFile file = newFile("Foo.java", "/prj/src/Foo.java");

        Object labelProvider = newLabelProvider();

        assertEquals("Foo.java - /prj/src", invokeText(labelProvider, file));
        assertNotNull(invokeImage(labelProvider, file));
    }

    @Test
    public void should_fall_back_to_default_text_and_image_for_unknown_elements()
    {
        Object labelProvider = newLabelProvider();
        Object unknownElement = new Object();

        // must not throw and must fall back to the default label provider
        invokeText(labelProvider, unknownElement);
        invokeImage(labelProvider, unknownElement);
    }

    private Object newLabelProvider()
    {
        try
        {
            Class<?> c = Class.forName("org.moreunit.core.ui.FileMatchSelectionDialog$FileLabelProvider");
            Constructor<?> constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private String invokeText(Object labelProvider, Object element)
    {
        return (String) invoke(labelProvider, "getText", element);
    }

    private Image invokeImage(Object labelProvider, Object element)
    {
        return (Image) invoke(labelProvider, "getImage", element);
    }

    private static Object invoke(Object target, String methodName, Object arg)
    {
        try
        {
            Method method = target.getClass().getDeclaredMethod(methodName, Object.class);
            method.setAccessible(true);
            return method.invoke(target, arg);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Object getField(Object owner, String name)
    {
        try
        {
            Field field = FileMatchSelectionDialog.class.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(owner);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static class TestContentProvider implements ITreeContentAndDefaultSelectionProvider
    {
        private final Object[] elements;
        private final ISelection defaultSelection;

        TestContentProvider(Object[] elements, ISelection defaultSelection)
        {
            this.elements = elements;
            this.defaultSelection = defaultSelection;
        }

        public Object[] getElements(Object inputElement)
        {
            return elements;
        }

        public Object[] getChildren(Object parentElement)
        {
            return new Object[0];
        }

        public Object getParent(Object element)
        {
            return null;
        }

        public boolean hasChildren(Object element)
        {
            return false;
        }

        public ISelection getDefaultSelection()
        {
            return defaultSelection;
        }

        public void dispose()
        {
        }

        public void inputChanged(IContentProvider viewer, Object oldInput, Object newInput)
        {
        }
    }
}
