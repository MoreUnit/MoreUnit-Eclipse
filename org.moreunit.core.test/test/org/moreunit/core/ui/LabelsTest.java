package org.moreunit.core.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LabelsTest
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
    public void should_create_wrapping_label_with_width_hint()
    {
        final Label label = Labels.wrappingLabel("Some long text", 123, shell);

        assertEquals("Some long text", label.getText());

        final GridData data = (GridData) label.getLayoutData();
        assertEquals(123, data.widthHint);
        assertEquals(GridData.FILL, data.horizontalAlignment);
        assertTrue(data.grabExcessHorizontalSpace);
    }

    @Test
    public void should_create_place_holder_without_layout_data()
    {
        final Label label = Labels.placeHolder(shell);

        assertNotNull(label);
        assertEquals(null, label.getLayoutData());
    }

    @Test
    public void should_create_place_holder_spanning_several_columns()
    {
        final Label label = Labels.placeHolder(shell, 3);

        final GridData data = (GridData) label.getLayoutData();
        assertEquals(3, data.horizontalSpan);
    }
}
