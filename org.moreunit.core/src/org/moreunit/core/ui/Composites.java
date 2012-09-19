package org.moreunit.core.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class Composites
{
    public static Composite fillBothNoMargin(Composite parent, int colSpan)
    {
        Composite c = new Composite(parent, SWT.NONE);
        c.setLayout(noMarginLayout());

        GridData gd = LayoutData.fillGrid();
        if(colSpan > 0)
        {
            gd.horizontalSpan = colSpan;
        }
        c.setLayoutData(gd);
        return c;
    }

    public static Composite fillBothNoMargin(Composite parent)
    {
        return fillBothNoMargin(parent, 0);
    }

    private static GridLayout noMarginLayout()
    {
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        return layout;
    }

    public static Composite gridGroup(Composite parent, String text, int numColumns)
    {
        Group group = new Group(parent, SWT.NONE);
        group.setText(text);

        GridLayout layout = new GridLayout(numColumns, false);
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        group.setLayout(layout);

        group.setLayoutData(LayoutData.fillGrid());

        return group;
    }

    public static Label placeHolder(Composite parent)
    {
        return Labels.placeHolder(parent);
    }

    public static Label placeHolder(Composite parent, int numColumns)
    {
        return Labels.placeHolder(parent, numColumns);
    }

    public static Composite grid(Composite parent, int numCol)
    {
        Composite c = new Composite(parent, SWT.NONE);
        GridLayout l = noMarginLayout();
        l.numColumns = 2;
        c.setLayout(l);
        c.setLayoutData(LayoutData.fillGrid());
        return c;
    }
}
