package org.moreunit.mock.wizard;

import static org.assertj.core.api.Assertions.assertThat;

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
    private boolean headless;

    @BeforeEach
    public void setUp()
    {
        display = Display.getDefault();
        headless = display == null;
        if (!headless) {
            display.syncExec(new Runnable() {
                public void run() {
                    shell = new Shell(display);
                }
            });
        }
    }

    @AfterEach
    public void tearDown()
    {
        if (shell != null && !shell.isDisposed()) {
            display.syncExec(new Runnable() {
                public void run() {
                    shell.dispose();
                }
            });
        }
    }

    @Test
    public void getButtonWidthHint_should_calculate_width()
    {
        if (headless) {
            return; // Skip when there is no display
        }
        display.syncExec(new Runnable() {
            public void run() {
                Button button = new Button(shell, SWT.PUSH);
                button.setText("Ok");

                int hint = LayoutUtil.getButtonWidthHint(button);

                assertThat(hint).isGreaterThan(0);
            }
        });
    }

    @Test
    public void setButtonDimensionHint_should_update_grid_data()
    {
        if (headless) {
            return;
        }
        display.syncExec(new Runnable() {
            public void run() {
                Button button = new Button(shell, SWT.PUSH);
                button.setText("Cancel");
                GridData layoutData = new GridData();
                button.setLayoutData(layoutData);

                LayoutUtil.setButtonDimensionHint(button);

                int expectedHint = LayoutUtil.getButtonWidthHint(button);
                assertThat(layoutData.widthHint).isEqualTo(expectedHint);
                assertThat(layoutData.horizontalAlignment).isEqualTo(GridData.FILL);
            }
        });
    }

    @Test
    public void setButtonDimensionHint_should_do_nothing_if_not_grid_data()
    {
        if (headless) {
            return;
        }
        display.syncExec(new Runnable() {
            public void run() {
                Button button = new Button(shell, SWT.PUSH);
                button.setLayoutData(new Object());

                try {
                    LayoutUtil.setButtonDimensionHint(button);
                } catch (Exception e) {
                    throw new AssertionError("Should not throw exception", e);
                }
            }
        });
    }
}
