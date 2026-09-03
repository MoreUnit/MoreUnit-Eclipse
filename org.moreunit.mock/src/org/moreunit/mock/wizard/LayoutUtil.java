package org.moreunit.mock.wizard;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

/**
 * Copied from org.eclipse.jdt.internal.junit.util.LayoutUtil.
 */
public class LayoutUtil
{
    /**
     * Returns a width hint for a button control.
     *
     * @param button the button for which to set the dimension hint
     * @return the width hint
     */
    public static int getButtonWidthHint(Button button)
    {
        button.setFont(JFaceResources.getDialogFont());
        final PixelConverter converter = new PixelConverter(button);
        final int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
    }

    /**
     * Sets width and height hint for the button control. <b>Note:</b> This is a
     * NOP if the button's layout data is not an instance of
     * <code>GridData</code>.
     *
     * @param button the button for which to set the dimension hint
     */
    public static void setButtonDimensionHint(Button button)
    {
        Assert.isNotNull(button);
        final Object gd = button.getLayoutData();
        if(gd instanceof final GridData data)
        {
            data.widthHint = getButtonWidthHint(button);
            data.horizontalAlignment = GridData.FILL;
        }
    }
}
