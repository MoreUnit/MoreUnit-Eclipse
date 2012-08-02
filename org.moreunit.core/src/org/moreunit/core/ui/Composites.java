package org.moreunit.core.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class Composites
{
    public static Composite fillBothNoMargin(Composite parent)
    {
        Composite c = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        c.setLayout(layout);
        c.setLayoutData(new GridData(GridData.FILL_BOTH));
        return c;
    }
}
