package org.moreunit.core.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CompositesTest
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
        catch (Throwable t)
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
    public void should_create_composite_with_no_margin_layout_and_fill_row_data()
    {
        Composite c = Composites.fillWidth(shell);

        assertEquals(0, ((org.eclipse.swt.layout.GridLayout) c.getLayout()).marginHeight);
        assertEquals(0, ((org.eclipse.swt.layout.GridLayout) c.getLayout()).marginWidth);

        GridData data = (GridData) c.getLayoutData();
        assertEquals(GridData.FILL, data.horizontalAlignment);
        assertTrue(data.grabExcessHorizontalSpace);
    }

    @Test
    public void should_create_group_with_title_and_layout()
    {
        Group group = (Group) Composites.gridGroup(shell, "My group", 3, 7);

        assertEquals("My group", group.getText());
        assertEquals(3, ((org.eclipse.swt.layout.GridLayout) group.getLayout()).numColumns);
        assertEquals(7, ((org.eclipse.swt.layout.GridLayout) group.getLayout()).marginHeight);
        assertEquals(7, ((org.eclipse.swt.layout.GridLayout) group.getLayout()).marginWidth);

        GridData data = (GridData) group.getLayoutData();
        assertTrue(data.grabExcessHorizontalSpace);
    }

    @Test
    public void should_create_place_holder_without_layout_data()
    {
        Label label = Composites.placeHolder(shell);

        assertNotNull(label);
        assertEquals(null, label.getLayoutData());
    }

    @Test
    public void should_create_place_holder_spanning_several_columns()
    {
        Label label = Composites.placeHolder(shell, 4);

        GridData data = (GridData) label.getLayoutData();
        assertEquals(4, data.horizontalSpan);
    }

    @Test
    public void should_create_grid_composite_with_two_columns()
    {
        Composite c = Composites.grid(shell, 2);

        assertEquals(2, ((org.eclipse.swt.layout.GridLayout) c.getLayout()).numColumns);
        assertEquals(0, ((org.eclipse.swt.layout.GridLayout) c.getLayout()).marginHeight);
        assertEquals(0, ((org.eclipse.swt.layout.GridLayout) c.getLayout()).marginWidth);
    }

    @Test
    public void should_create_link_with_text()
    {
        Link link = Composites.link(shell, "Some text");

        assertEquals("<A>Some text</A>", link.getText());
    }

    @Test
    public void should_add_selection_listener_to_link()
    {
        boolean[] fired = new boolean[1];
        Link link = Composites.link(shell, "Some text", org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter(e -> fired[0] = true));

        link.notifyListeners(SWT.Selection, new org.eclipse.swt.widgets.Event());

        assertTrue(fired[0]);
    }

    @Test
    public void should_not_fire_listener_when_not_selected()
    {
        boolean[] fired = new boolean[1];
        Composites.link(shell, "Some text", org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter(e -> fired[0] = true));

        Control[] children = shell.getChildren();

        assertTrue(children.length > 0);
        assertTrue(! fired[0]);
    }
}
