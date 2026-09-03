package org.moreunit.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.properties.SwtPageTestCase;
import org.moreunit.test.support.DialogHelper;

import java.util.Set;

/**
 * Tests {@link ChooseDialog} with a real (non-modal) popup: the constructor
 * calls {@code create()}, so the shell and its controls can be inspected and
 * manipulated without ever calling the blocking {@code open()}.
 */
public class ChooseDialogTest extends SwtPageTestCase
{
    private IType type1;
    private IType type2;
    private ChooseDialog<Object> dialog;

    @BeforeEach
    public void createDialog()
    {
        type1 = mockType("Type1", "com.example");
        type2 = mockType("Type2", "com.example");
    }

    @AfterEach
    public void closeDialog()
    {
        if(dialog != null && dialog.getShell() != null && ! dialog.getShell().isDisposed())
        {
            dialog.close();
        }
    }

    private IType mockType(String name, String packageName)
    {
        final IType type = mock(IType.class);
        when(type.getElementName()).thenReturn(name);
        when(type.getFullyQualifiedName()).thenReturn(packageName + "." + name);
        when(type.getFullyQualifiedName('.')).thenReturn(packageName + "." + name);
        final IPackageFragment packageFragment = mock(IPackageFragment.class);
        when(packageFragment.getElementName()).thenReturn(packageName);
        when(type.getPackageFragment()).thenReturn(packageFragment);
        return type;
    }

    private ChooseDialog<Object> createDialog(IType... types)
    {
        final MemberContentProvider provider = new MemberContentProvider(Arrays.asList(types), Collections.<IMethod> emptySet(), null);
        dialog = new ChooseDialog<>("Choose a type", provider);
        return dialog;
    }

    @Test
    public void should_create_shell_and_display_all_elements()
    {
        dialog = createDialog(type1, type2);

        assertNotNull(dialog.getShell());
        assertFalse(dialog.getShell().isDisposed());

        final TreeViewer treeViewer = (TreeViewer) getField(dialog, "treeViewer");
        assertNotNull(treeViewer);
        assertEquals(2, treeViewer.getTree().getItemCount());
    }

    @Test
    public void should_select_first_element_by_default()
    {
        dialog = createDialog(type1, type2);

        assertEquals(type1, dialog.getSelectedElement());
    }

    @Test
    public void should_close_when_escape_is_pressed()
    {
        dialog = createDialog(type1, type2);
        final Shell shell = dialog.getShell();
        final Tree tree = treeOf(dialog);

        final Event event = new Event();
        event.widget = tree;
        event.character = SWT.ESC;
        tree.notifyListeners(SWT.KeyDown, event);

        assertTrue(shell.isDisposed());
        assertNull(dialog.getSelectedElement());
    }

    @Test
    public void should_close_and_keep_selection_when_element_is_confirmed()
    {
        dialog = createDialog(type1, type2);
        final Shell shell = dialog.getShell();
        final Tree tree = treeOf(dialog);

        tree.setSelection(tree.getItem(1));
        tree.notifyListeners(SWT.DefaultSelection, new Event());

        assertTrue(shell.isDisposed());
        assertEquals(type2, getField(dialog, "selectedElement"));
        assertNull(dialog.getSelectedElement()); // viewer is gone after disposal
    }

    @Test
    public void should_execute_action_element_when_confirmed()
    {
        final Object result = mock(IType.class);
        @SuppressWarnings("unchecked")
        final
        TreeActionElement<Object> action = mock(TreeActionElement.class);
        when(action.provideElement()).thenReturn(true);
        when(action.execute()).thenReturn(result);
        when(action.getText()).thenReturn("New Class...");

        final MemberContentProvider provider = new MemberContentProvider(Arrays.asList(type1), Collections.<IMethod> emptySet(), null);
        provider.withAction(action);
        dialog = new ChooseDialog<>("Choose", provider);
        final Shell shell = dialog.getShell();
        final Tree tree = treeOf(dialog);

        // last item is the action element
        tree.setSelection(tree.getItem(tree.getItemCount() - 1));
        tree.notifyListeners(SWT.DefaultSelection, new Event());

        assertTrue(shell.isDisposed());
        assertEquals(result, getField(dialog, "selectedElement"));
    }

    @Test
    public void should_not_execute_nor_close_when_action_does_not_provide_element()
    {
        @SuppressWarnings("unchecked")
        final
        TreeActionElement<Object> action = mock(TreeActionElement.class);
        when(action.provideElement()).thenReturn(false);
        when(action.getText()).thenReturn("New Class...");

        final MemberContentProvider provider = new MemberContentProvider(Arrays.asList(type1), Collections.<IMethod> emptySet(), null);
        provider.withAction(action);
        dialog = new ChooseDialog<>("Choose", provider);
        final Shell shell = dialog.getShell();
        final Tree tree = treeOf(dialog);

        tree.setSelection(tree.getItem(tree.getItemCount() - 1));
        tree.notifyListeners(SWT.DefaultSelection, new Event());

        assertFalse(shell.isDisposed());
        assertNull(getField(dialog, "selectedElement"));
        verify(action, never()).execute();
    }

    @Test
    public void should_select_item_under_mouse_when_mouse_moves()
    {
        dialog = createDialog(type1, type2);
        final Tree tree = treeOf(dialog);
        shell.open();
        while (display.readAndDispatch())
        {
        }

        final TreeItem item2 = tree.getItem(1);
        item2.setData(type2);
        tree.setSelection(tree.getItem(0));

        final Rectangle bounds = item2.getBounds();
        final Event event = new Event();
        event.widget = tree;
        event.x = bounds.x + bounds.width / 2;
        event.y = bounds.y + bounds.height / 2;
        tree.notifyListeners(SWT.MouseMove, event);

        final TreeItem[] selection = tree.getSelection();
        assertEquals(1, selection.length);
        assertEquals(item2, selection[0]);
    }

    @Test
    public void should_confirm_selection_when_mouse_is_released_on_selected_item()
    {
        dialog = createDialog(type1, type2);
        final Shell dialogShell = dialog.getShell();
        final Tree tree = treeOf(dialog);
        shell.open();
        while (display.readAndDispatch())
        {
        }

        final TreeItem item1 = tree.getItem(0);
        tree.setSelection(item1);

        final Rectangle bounds = item1.getBounds();
        final Event event = new Event();
        event.widget = tree;
        event.button = 1;
        event.x = bounds.x + bounds.width / 2;
        event.y = bounds.y + bounds.height / 2;
        tree.notifyListeners(SWT.MouseUp, event);

        assertTrue(dialogShell.isDisposed());
        assertEquals(type1, getField(dialog, "selectedElement"));
    }

    @Test
    public void should_not_close_when_mouse_is_released_on_a_different_item()
    {
        dialog = createDialog(type1, type2);
        final Shell shell = dialog.getShell();
        final Tree tree = treeOf(dialog);
        shell.open();
        while (display.readAndDispatch())
        {
        }

        final TreeItem item1 = tree.getItem(0);
        final TreeItem item2 = tree.getItem(1);
        tree.setSelection(item1);

        final Rectangle bounds = item2.getBounds();
        final Event event = new Event();
        event.widget = tree;
        event.button = 1;
        event.x = bounds.x + bounds.width / 2;
        event.y = bounds.y + bounds.height / 2;
        tree.notifyListeners(SWT.MouseUp, event);

        assertFalse(shell.isDisposed());
    }

    @Test
    public void should_scroll_when_mouse_moves_repeatedly_to_the_edges_of_the_tree()
    {
        dialog = createDialog(type1, type2);
        final Shell dialogShell = dialog.getShell();
        final Tree tree = treeOf(dialog);
        dialogShell.open();
        while (display.readAndDispatch())
        {
        }

        final TreeItem item1 = tree.getItem(0);
        final TreeItem item2 = tree.getItem(1);
        tree.setSelection(item1);

        final int itemHeight = tree.getItemHeight();
        final Rectangle bounds = item1.getBounds();
        final int x = bounds.x + Math.max(bounds.width / 2, 5);

        // two identical moves near the top edge trigger the "scroll up" branch
        tree.setSelection(item2);
        fireMouseMove(tree, x, Math.max(bounds.y + 1, 1));
        fireMouseMove(tree, x, Math.max(bounds.y + 1, 1));

        // two identical moves near the bottom edge trigger the "scroll down" branch
        final org.eclipse.swt.graphics.Rectangle treeBounds = tree.getBounds();
        final Rectangle bottomBounds = item2.getBounds();
        final int bottomY = Math.min(treeBounds.height - itemHeight / 4 + 1, bottomBounds.y + bottomBounds.height - 1);
        tree.setSelection(item2);
        fireMouseMove(tree, x, Math.max(bottomY, 1));
        fireMouseMove(tree, x, Math.max(bottomY, 1));

        assertFalse(dialogShell.isDisposed());
    }

    private void fireMouseMove(Tree tree, int x, int y)
    {
        final Event event = new Event();
        event.widget = tree;
        event.x = x;
        event.y = y;
        tree.notifyListeners(SWT.MouseMove, event);
    }

    private Tree treeOf(ChooseDialog<?> dialog)
    {
        final TreeViewer treeViewer = (TreeViewer) getField(dialog, "treeViewer");
        return treeViewer.getTree();
    }

    @Test
    public void getChoice_should_block_until_a_selection_is_confirmed_and_return_it() throws Exception
    {
        dialog = createDialog(type1, type2);

        final Set<Shell> knownShells = DialogHelper.knownShells(display);
        knownShells.remove(dialog.getShell()); // the popup shell already exists
        display.asyncExec(DialogHelper.closerFor(display, knownShells, shell -> DialogHelper.confirmItem(shell, "Type2"), 2000));

        final java.util.concurrent.atomic.AtomicReference<Object> choice = new java.util.concurrent.atomic.AtomicReference<>();
        final Thread background = new Thread(() -> choice.set(Display.getDefault().syncCall(dialog::getChoice)));
        background.start();

        final long deadline = System.currentTimeMillis() + 30_000;
        while (background.isAlive() && System.currentTimeMillis() < deadline)
        {
            while (display.readAndDispatch())
            {
            }
            Thread.sleep(10);
        }
        background.join(5_000);

        assertFalse(background.isAlive());
        assertEquals(type2, choice.get());
    }

    @Test
    public void getChoice_should_return_null_when_the_dialog_is_cancelled() throws Exception
    {
        dialog = createDialog(type1, type2);

        final Set<Shell> knownShells = DialogHelper.knownShells(display);
        knownShells.remove(dialog.getShell());
        display.asyncExec(DialogHelper.closerFor(display, knownShells, Shell::close, 2000));

        final java.util.concurrent.atomic.AtomicReference<Object> choice = new java.util.concurrent.atomic.AtomicReference<>();
        final Thread background = new Thread(() -> choice.set(Display.getDefault().syncCall(dialog::getChoice)));
        background.start();

        final long deadline = System.currentTimeMillis() + 30_000;
        while (background.isAlive() && System.currentTimeMillis() < deadline)
        {
            while (display.readAndDispatch())
            {
            }
            Thread.sleep(10);
        }
        background.join(5_000);

        assertFalse(background.isAlive());
        assertNull(choice.get());
    }
}
