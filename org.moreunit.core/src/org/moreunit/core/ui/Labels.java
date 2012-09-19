package org.moreunit.core.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class Labels
{
    public static Label wrappingLabel(String text, int widthHint, Composite parent)
    {
        Label l = new Label(parent, SWT.WRAP);
        l.setText(text);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = widthHint;
        l.setLayoutData(gd);
        return l;
    }

    public static Label placeHolder(Composite parent)
    {
        return new Label(parent, SWT.NONE);
    }

    public static Label placeHolder(Composite parent, int numColumns)
    {
        Label l = new Label(parent, SWT.NONE);
        GridData gridData = new GridData();
        gridData.horizontalSpan = numColumns;
        l.setLayoutData(gridData);
        return l;
    }
}
