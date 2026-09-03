package org.moreunit.core.ui;

import org.eclipse.swt.layout.GridLayout;

public class GridLayouts
{
    public static GridLayout noMargin()
    {
        final GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        return layout;
    }
}
