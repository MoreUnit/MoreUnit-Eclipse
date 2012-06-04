package org.moreunit.core.ui;

import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;

public class ExpandableCompositeContainer
{
    private final ScrolledPageContent scrolledPageContent;
    private final int numColumns;

    public ExpandableCompositeContainer(Composite parent, int numColumns)
    {
        this.numColumns = numColumns;
        PixelConverter pixelConverter = new PixelConverter(parent);

        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        parent.setLayout(layout);

        parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        scrolledPageContent = new ScrolledPageContent(parent);
        scrolledPageContent.addControlListener(new ControlAdapter()
        {
            @Override
            public void controlResized(final ControlEvent e)
            {
                scrolledPageContent.getVerticalBar().setVisible(true);
            }
        });

        getBody().setFont(parent.getFont());

        layout = new GridLayout(numColumns, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        getBody().setLayout(layout);

        getBody().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        getBody().setLayout(layout);

        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        gridData.heightHint = pixelConverter.convertHeightInCharsToPixels(10);
        scrolledPageContent.setLayoutData(gridData);
    }

    public ExpandableComposite addExpandableComposite(final String label, boolean expanded, CompositeBody body)
    {
        ExpandableComposite exComp = addExpandableComposite(label, expanded);
        exComp.setClient(body.createBody(exComp));
        return exComp;
    }

    private ExpandableComposite addExpandableComposite(final String label, boolean expanded)
    {
        ExpandableComposite exComp = new ExpandableComposite(getBody(), SWT.NONE, ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT);
        exComp.setText(label);
        exComp.setExpanded(expanded);
        exComp.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));

        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
        gridData.horizontalSpan = numColumns;
        exComp.setLayoutData(gridData);

        exComp.addExpansionListener(new ExpansionAdapter()
        {
            @Override
            public void expansionStateChanged(final ExpansionEvent e)
            {
                scrolledPageContent.reflow(true);
            }
        });
        return exComp;
    }

    public Composite getBody()
    {
        return (Composite) scrolledPageContent.getContent();
    }

    public GridLayout getBodyLayout()
    {
        return (GridLayout) ((Composite) scrolledPageContent.getContent()).getLayout();
    }

    public void reflow()
    {
        scrolledPageContent.reflow(true);
    }

    private static class ScrolledPageContent extends SharedScrolledComposite
    {
        public ScrolledPageContent(final Composite parent)
        {
            super(parent, SWT.V_SCROLL | SWT.H_SCROLL);

            setFont(parent.getFont());

            setExpandHorizontal(true);
            setExpandVertical(true);

            Composite body = new Composite(this, SWT.NONE);
            body.setFont(parent.getFont());
            setContent(body);
        }
    }

    public static interface CompositeBody
    {
        Control createBody(ExpandableComposite expandableComposite);
    }
}
