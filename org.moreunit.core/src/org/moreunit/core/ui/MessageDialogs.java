package org.moreunit.core.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.moreunit.core.config.Config;

public class MessageDialogs
{
    private static final String TITLE = "MoreUnit";

    public static void openInformation(Shell shell, String message)
    {
        if(Config.messageDialogsActivated)
        {
            MessageDialog.openInformation(shell, TITLE, message);
        }
    }
}
