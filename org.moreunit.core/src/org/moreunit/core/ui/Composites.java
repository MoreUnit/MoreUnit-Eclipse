package org.moreunit.core.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

public class Composites
{
    public static Composite fillWidth(Composite parent)
    {
        Composite c = new Composite(parent, SWT.NONE);
        c.setLayout(GridLayouts.noMargin());
        c.setLayoutData(LayoutData.fillRow());
        return c;
    }

    public static Composite gridGroup(Composite parent, String text, int numColumns, int margins)
    {
        Group group = new Group(parent, SWT.NONE);
        group.setText(text);

        GridLayout layout = new GridLayout(numColumns, false);
        layout.marginHeight = margins;
        layout.marginWidth = margins;
        group.setLayout(layout);

        group.setLayoutData(LayoutData.fillRow());

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
        GridLayout l = GridLayouts.noMargin();
        l.numColumns = 2;
        c.setLayout(l);
        c.setLayoutData(LayoutData.fillRow());
        return c;
    }

    public static Link link(Composite parent, String text)
    {
        Link link = new Link(parent, SWT.NONE);
        link.setFont(parent.getFont());
        link.setText("<A>" + text + "</A>"); //$NON-NLS-1$//$NON-NLS-2$
        return link;
    }

    public static Link link(Composite parent, String text, SelectionListener selectionListener)
    {
        Link link = link(parent, text);
        link.addSelectionListener(selectionListener);
        return link;
    }
}
