package org.moreunit.core.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.eclipse.ui.forms.widgets.ExpandableComposite;

public class ExpandableCompositeContainerTest
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

    private ExpandableComposite newExpandable(ExpandableCompositeContainer container, boolean expanded)
    {
        return container.newExpandableComposite(container, "Section", expanded, parent -> new Label(parent, SWT.NONE));
    }

    @Test
    public void should_create_container_inside_a_scrolled_composite()
    {
        final ExpandableCompositeContainer container = new ExpandableCompositeContainer(shell);

        // the container is wrapped into a scrolled composite, itself child of the shell
        assertTrue(container.getParent() != shell);
        assertEquals(shell, container.getParent().getParent());
    }

    @Test
    public void should_create_expandable_composite_with_label_and_client()
    {
        final ExpandableCompositeContainer container = new ExpandableCompositeContainer(shell);

        final ExpandableComposite exComp = newExpandable(container, true);

        assertEquals("Section", exComp.getText());
        assertTrue(exComp.isExpanded());
        assertNotNull(exComp.getClient());
    }

    @Test
    public void should_create_collapsed_expandable_composite_when_requested()
    {
        final ExpandableCompositeContainer container = new ExpandableCompositeContainer(shell);

        final ExpandableComposite exComp = newExpandable(container, false);

        assertFalse(exComp.isExpanded());
    }

    @Test
    public void should_expand_and_collapse_all_contained_composites()
    {
        final ExpandableCompositeContainer container = new ExpandableCompositeContainer(shell);
        final ExpandableComposite exComp1 = newExpandable(container, false);
        final ExpandableComposite exComp2 = newExpandable(container, false);

        container.setExpanded(true);

        assertTrue(exComp1.isExpanded());
        assertTrue(exComp2.isExpanded());

        container.setExpanded(false);

        assertFalse(exComp1.isExpanded());
        assertFalse(exComp2.isExpanded());
    }

    @Test
    public void should_disable_and_collapse_all_contained_composites_when_not_expandable()
    {
        final ExpandableCompositeContainer container = new ExpandableCompositeContainer(shell);
        final ExpandableComposite exComp1 = newExpandable(container, true);
        final ExpandableComposite exComp2 = newExpandable(container, true);

        container.setExpandable(false);

        assertFalse(exComp1.getEnabled());
        assertFalse(exComp2.getEnabled());
        assertFalse(exComp1.isExpanded());
        assertFalse(exComp2.isExpanded());
    }

    @Test
    public void should_leave_contained_composites_enabled_when_expandable()
    {
        final ExpandableCompositeContainer container = new ExpandableCompositeContainer(shell);
        final ExpandableComposite exComp = newExpandable(container, true);

        container.setExpandable(true);

        assertTrue(exComp.getEnabled());
    }

    @Test
    public void should_fire_expansion_listener_and_reflow_without_error()
    {
        final ExpandableCompositeContainer container = new ExpandableCompositeContainer(shell);
        final ExpandableComposite exComp = newExpandable(container, false);

        // expanding fires the ExpansionAdapter registered by the container,
        // which triggers a reflow of the scrolled composite
        exComp.setExpanded(true);

        assertTrue(exComp.isExpanded());

        container.reflow();
    }
}
