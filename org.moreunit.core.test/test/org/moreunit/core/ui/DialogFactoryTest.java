package org.moreunit.core.ui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DialogFactoryTest
{
    private Display display;
    private Shell shell;

    @BeforeEach
    public void createShell()
    {
        try
        {
            display = Display.getDefault();
        }
        catch (final Throwable t)
        {
            display = null;
        }
        assumeTrue(display != null, "No SWT display available");
        shell = new Shell(display);
    }

    @AfterEach
    public void disposeShell()
    {
        if(shell != null && ! shell.isDisposed())
        {
            shell.dispose();
        }
    }

    @Test
    public void should_create_info_dialog()
    {
        final DialogFactory factory = new DialogFactory(shell);

        assertNotNull(factory.createInfoDialog("Some information"));
    }

    @Test
    public void should_create_error_dialog()
    {
        final DialogFactory factory = new DialogFactory(shell);

        assertNotNull(factory.createErrorDialog("Some error"));
    }
}
