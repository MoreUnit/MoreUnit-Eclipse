package org.moreunit.mock.wizard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LayoutUtilTest
{
    private Display display;
    private Shell shell;

    @BeforeEach
    public void setUp()
    {
        display = Display.getDefault();
        shell = new Shell(display);
    }

    @AfterEach
    public void tearDown()
    {
        if(shell != null && ! shell.isDisposed())
        {
            shell.dispose();
        }
    }

    @Test
    public void getButtonWidthHint_should_calculate_width()
    {
        Button button = new Button(shell, SWT.PUSH);
        button.setText("Ok");

        int hint = LayoutUtil.getButtonWidthHint(button);

        button.setFont(JFaceResources.getDialogFont());
        PixelConverter converter = new PixelConverter(button);
        int expectedHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        expectedHint = Math.max(expectedHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);

        assertEquals(expectedHint, hint);
    }

    @Test
    public void setButtonDimensionHint_should_update_grid_data()
    {
        Button button = new Button(shell, SWT.PUSH);
        button.setText("Cancel");
        GridData layoutData = new GridData();
        button.setLayoutData(layoutData);

        LayoutUtil.setButtonDimensionHint(button);

        int expectedHint = LayoutUtil.getButtonWidthHint(button);
        assertEquals(expectedHint, layoutData.widthHint);
        assertEquals(GridData.FILL, layoutData.horizontalAlignment);
    }

    @Test
    public void setButtonDimensionHint_should_do_nothing_if_not_grid_data()
    {
        Button button = new Button(shell, SWT.PUSH);
        button.setLayoutData(new Object());

        LayoutUtil.setButtonDimensionHint(button);

        // no exception thrown
    }
}
