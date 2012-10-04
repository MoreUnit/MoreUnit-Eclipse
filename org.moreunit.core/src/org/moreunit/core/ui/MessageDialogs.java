package org.moreunit.core.ui;

import static org.moreunit.core.config.Module.$;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class MessageDialogs
{
    private static final String TITLE = "MoreUnit";

    public static void openInformation(Shell shell, String message)
    {
        if($().shouldUseMessageDialogs())
        {
            MessageDialog.openInformation(shell, TITLE, message);
        }
    }

    public static void openError(Shell shell, String message)
    {
        if($().shouldUseMessageDialogs())
        {
            MessageDialog.openError(shell, TITLE, message);
        }
    }
}
