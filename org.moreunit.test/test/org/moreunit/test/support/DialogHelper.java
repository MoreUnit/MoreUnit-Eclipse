package org.moreunit.test.support;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
     * Brings the test workbench window to the front. Call this right before
     * triggering an action that opens a dialog: on a shared desktop the
     * workbench may sit behind other windows, and opening a popup there can
     * deactivate (and thereby dismiss) it before the test driver even sees
     * it. Must be called on the UI thread.
     *
     * @param display the display on which the test workbench runs
     */
    public static void bringWorkbenchToFront(Display display)
    {
        display.syncExec(() -> {
            for (final Shell shell : display.getShells())
            {
                if(! shell.isDisposed() && shell.isVisible())
                {
                    shell.forceActive();
                }
            }
        });
    }

    /**
     * Returns a task (to be scheduled with {@code display.asyncExec(...)}
     * before the dialog opens) that polls for a shell appearing after
     * {@code knownShells} was captured and then applies the given action. The
     * task gives up after {@code maxAttempts} polls and then closes all new
     * shells, so that the dialog call eventually returns.
     * <p>
     * The action runs exactly once, on the first new shell seen. When the
     * handling depends on the shell content (which may lag behind shell
     * visibility on some platforms), prefer
     * {@link #closerUntilHandled(Display, Set, Predicate, int)}.
     *
     * @param display the display on which the dialog will open
     * @param knownShells the shells existing before the dialog opens
     * @param action the action to apply to the new shell once detected
     * @param maxAttempts the number of polls before giving up
     * @return a task polling for the new dialog shell
     */
    public static Runnable closerFor(Display display, Set<Shell> knownShells, Consumer<Shell> action, int maxAttempts)
    {
        return closerUntilHandled(display, knownShells, shell -> {
            action.accept(shell);
            return true;
        }, maxAttempts);
    }

    /**
     * Returns a task (to be scheduled with {@code display.asyncExec(...)}
     * before the dialog opens) that polls for a shell appearing after
     * {@code knownShells} was captured and then applies the given action. When
     * the action reports the shell as not handled yet (for instance its
     * content is still loading), polling continues until {@code maxAttempts}
     * polls; then all new shells are closed so that the dialog call
     * eventually returns.
     *
     * @param display the display on which the dialog will open
     * @param knownShells the shells existing before the dialog opens
     * @param handled tests a new shell, handling it when ready; {@code true}
     *            means handled (polling stops), {@code false} means not ready
     *            yet (polling continues)
     * @param maxAttempts the number of polls before giving up
     * @return a task polling for the new dialog shell
     */
    public static Runnable closerUntilHandled(Display display, Set<Shell> knownShells, Predicate<Shell> handled, int maxAttempts)
    {
        return () -> {
            final Shell target = findNewShell(display, knownShells);
            if(target != null)
            {
                // Keep the focus on the dialog while driving it: ChooseDialog
                // dismisses itself on deactivation, and on a shared desktop
                // any focus change in between would silently cancel the
                // dialog (flaky on GTK, where focus handling is async).
                if(! target.isDisposed())
                {
                    target.forceActive();
                }
                if(! target.isDisposed() && handled.test(target))
                {
                    return;
                }
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
            display.timerExec(20, DialogHelper.closerUntilHandled(display, knownShells, handled, maxAttempts - 1));
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
     * confirms it (default selection).
     *
     * @param dialogShell the dialog shell containing the tree
     * @param itemText the (sub)string identifying the item to confirm
     * @return {@code true} when an item was confirmed or the shell was closed
     *         because no item will ever match; {@code false} when the tree
     *         does not exist yet or its labels are not computed yet, meaning
     *         the caller should try again later
     */
    public static boolean confirmItem(Shell dialogShell, String itemText)
    {
        final Tree tree = findTree(dialogShell);
        if(tree == null || tree.isDisposed())
        {
            return false;
        }
        final TreeItem[] items = tree.getItems();
        if(items.length == 0)
        {
            return false;
        }
        boolean allLabeled = true;
        for (final TreeItem item : items)
        {
            if(confirmItemOrChild(item, itemText))
            {
                return true;
            }
            if(item.isDisposed() || item.getText().isEmpty())
            {
                allLabeled = false;
            }
        }
        if(! allLabeled)
        {
            return false;
        }
        dialogShell.close();
        return true;
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
     * @return {@code true} when the button was clicked; {@code false} when it
     *         does not exist yet, meaning the caller should try again later
     */
    public static boolean confirmOkButton(Shell dialogShell)
    {
        final org.eclipse.swt.widgets.Button okButton = findButtonWithText(dialogShell, "OK");
        if(okButton != null)
        {
            okButton.notifyListeners(org.eclipse.swt.SWT.Selection, new Event());
            return true;
        }
        return false;
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
