package org.moreunit.test.support;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Helper to drive the (non-modal) {@code ChooseDialog} popups that production
 * code opens synchronously: the dialog blocks the calling thread in its own
 * event loop, so a task scheduled with {@link Display#asyncExec(Runnable)}
 * beforehand gets dispatched while the dialog is open and can select or close
 * it.
 */
public final class DialogHelper
{
    private DialogHelper()
    {
    }

    public static Set<Shell> knownShells(Display display)
    {
        return new HashSet<>(Arrays.asList(display.getShells()));
    }

    /**
     * Returns a task (to be scheduled with {@code display.asyncExec(...)}
     * before the dialog opens) that polls for a shell appearing after
     * {@code knownShells} was captured and then applies the given action. The
     * task gives up after {@code maxAttempts} polls and then closes all new
     * shells, so that the dialog call eventually returns.
     *
     * @param display the display on which the dialog will open
     * @param knownShells the shells existing before the dialog opens
     * @param action the action to apply to the new shell once detected
     * @param maxAttempts the number of polls before giving up
     * @return a task polling for the new dialog shell
     */
    public static Runnable closerFor(Display display, Set<Shell> knownShells, Consumer<Shell> action, int maxAttempts)
    {
        return () -> {
            final Shell target = findNewShell(display, knownShells);
            if(target != null)
            {
                action.accept(target);
                return;
            }
            if(maxAttempts <= 0)
            {
                // give up: close any shell that appeared so the blocked
                // dialog call returns and the test fails on its assertion
                for (final Shell shell : display.getShells())
                {
                    if(! knownShells.contains(shell) && ! shell.isDisposed())
                    {
                        shell.close();
                    }
                }
                return;
            }
            display.timerExec(20, DialogHelper.closerFor(display, knownShells, action, maxAttempts - 1));
        };
    }

    public static Shell findNewShell(Display display, Set<Shell> knownShells)
    {
        for (final Shell shell : display.getShells())
        {
            if(! knownShells.contains(shell) && ! shell.isDisposed() && shell.isVisible())
            {
                return shell;
            }
        }
        return null;
    }

    /**
     * Selects the first tree item whose text contains the given text and
     * confirms it (default selection). Closes the shell when no such item
     * exists.
     *
     * @param dialogShell the dialog shell containing the tree
     * @param itemText the (sub)string identifying the item to confirm
     */
    public static void confirmItem(Shell dialogShell, String itemText)
    {
        final Tree tree = findTree(dialogShell);
        if(tree == null)
        {
            dialogShell.close();
            return;
        }
        for (final TreeItem item : tree.getItems())
        {
            if(confirmItemOrChild(item, itemText))
            {
                return;
            }
        }
        dialogShell.close();
    }

    private static boolean confirmItemOrChild(TreeItem item, String itemText)
    {
        if(item.getText().contains(itemText))
        {
            item.getParent().setSelection(item);
            item.getParent().notifyListeners(org.eclipse.swt.SWT.DefaultSelection, new Event());
            return true;
        }
        for (final TreeItem child : item.getItems())
        {
            if(confirmItemOrChild(child, itemText))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Clicks the OK button of a {@link org.eclipse.jface.dialogs.Dialog} shell.
     *
     * @param dialogShell the dialog shell containing the OK button
     */
    public static void confirmOkButton(Shell dialogShell)
    {
        final org.eclipse.swt.widgets.Button okButton = findButtonWithText(dialogShell, "OK");
        if(okButton != null)
        {
            okButton.notifyListeners(org.eclipse.swt.SWT.Selection, new Event());
            return;
        }
        dialogShell.close();
    }

    private static org.eclipse.swt.widgets.Button findButtonWithText(org.eclipse.swt.widgets.Widget widget, String text)
    {
        if(widget.isDisposed())
        {
            return null;
        }
        if(widget instanceof final org.eclipse.swt.widgets.Button button && text.equals(button.getText()))
        {
            return button;
        }
        if(widget instanceof final org.eclipse.swt.widgets.Composite composite)
        {
            for (final org.eclipse.swt.widgets.Control child : composite.getChildren())
            {
                final org.eclipse.swt.widgets.Button button = findButtonWithText(child, text);
                if(button != null)
                {
                    return button;
                }
            }
        }
        return null;
    }

    public static Tree findTree(Control control)
    {
        if(control.isDisposed())
        {
            return null;
        }
        if(control instanceof final Tree tree)
        {
            return tree;
        }
        if(control instanceof final org.eclipse.swt.widgets.Composite composite)
        {
            for (final Control child : composite.getChildren())
            {
                final Tree tree = findTree(child);
                if(tree != null)
                {
                    return tree;
                }
            }
        }
        return null;
    }
}
