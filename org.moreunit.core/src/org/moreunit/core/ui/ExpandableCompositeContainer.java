package org.moreunit.core.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;

public class ExpandableCompositeContainer extends Composite
{
    private final ScrolledComposite scrolledComposite;
    private final Set<ExpandableComposite> expandableComposites = new HashSet<ExpandableComposite>();

    public ExpandableCompositeContainer(Composite parent)
    {
        super(new ScrolledComposite(parent), SWT.NONE);

        setFont(parent.getFont());
        setBackground(parent.getBackground());
        applyStretchedGridLayout(this);

        ((GridLayout) getLayout()).marginRight = 10;

        scrolledComposite = (ScrolledComposite) getParent();
        scrolledComposite.setContent(this);
    }

    private static Composite applyStretchedGridLayout(Composite c)
    {
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        c.setLayout(layout);

        c.setLayoutData(LayoutData.fillRow());

        return c;
    }

    /**
     * Important: {@code parent} must be a child of this container
     */
    public ExpandableComposite newExpandableComposite(Composite parent, String label, boolean expanded, ExpandableContent content)
    {
        ExpandableComposite exComp = new ExpandableComposite(parent, SWT.NONE, ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT);
        exComp.setText(label);
        exComp.setExpanded(expanded);
        exComp.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));

        exComp.setLayoutData(LayoutData.fillRow());

        exComp.addExpansionListener(new ExpansionAdapter()
        {
            @Override
            public void expansionStateChanged(final ExpansionEvent e)
            {
                reflow();
            }
        });

        exComp.setClient(content.createBody(exComp));

        expandableComposites.add(exComp);

        return exComp;
    }

    public void reflow()
    {
        scrolledComposite.reflow();
    }

    public void setExpandable(boolean expandable)
    {
        for (ExpandableComposite ec : expandableComposites)
        {
            ec.setEnabled(expandable);
        }

        if(! expandable)
        {
            setExpanded(false);
        }
    }

    public void setExpanded(boolean expanded)
    {
        for (ExpandableComposite ec : expandableComposites)
        {
            ec.setExpanded(expanded);
        }
        reflow();
    }

    private static class ScrolledComposite extends SharedScrolledComposite
    {
        public ScrolledComposite(Composite parent)
        {
            super(applyStretchedGridLayout(parent), SWT.V_SCROLL | SWT.H_SCROLL);

            setFont(parent.getFont());

            setExpandHorizontal(true);
            setExpandVertical(true);

            setLayoutData(LayoutData.fillGrid());
        }

        public void reflow()
        {
            reflow(true);
        }
    }

    public static interface ExpandableContent
    {
        Control createBody(ExpandableComposite expandableComposite);
    }
}
