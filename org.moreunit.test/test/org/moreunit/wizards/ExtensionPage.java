package org.moreunit.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.moreunit.extensionpoints.INewTestCaseWizardPage;
import org.moreunit.extensionpoints.NewTestCaseWizardPagePosition;

public class ExtensionPage implements INewTestCaseWizardPage
{
    private final String id;
    private final IWizardPage page;
    private final NewTestCaseWizardPagePosition position;

    public ExtensionPage(String id)
    {
        this(id, null, null);
    }

    public ExtensionPage(String id, IWizardPage page, NewTestCaseWizardPagePosition position)
    {
        this.id = id;
        this.page = page;
        this.position = position;
    }

    public String getId()
    {
        return id;
    }

    public IWizardPage getPage()
    {
        return page;
    }

    public NewTestCaseWizardPagePosition getPosition()
    {
        return position;
    }

    @Override
    public int hashCode()
    {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof INewTestCaseWizardPage && getId().equals(((INewTestCaseWizardPage) obj).getId());
    }
}
