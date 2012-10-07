package org.moreunit.core.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class DialogFactory
{
    private static final String TITLE = "MoreUnit";

    private final Shell activeShell;

    public DialogFactory(Shell activeShell)
    {
        this.activeShell = activeShell;
    }

    public Dialog createInfoDialog(final String message)
    {
        return new Dialog()
        {
            public void open()
            {
                MessageDialog.openInformation(activeShell, TITLE, message);
            }
        };
    }

    public Dialog createErrorDialog(final String message)
    {
        return new Dialog()
        {
            public void open()
            {
                MessageDialog.openError(activeShell, TITLE, message);
            }
        };
    }
}
