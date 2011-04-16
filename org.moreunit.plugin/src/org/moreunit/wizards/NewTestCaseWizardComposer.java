package org.moreunit.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.jface.wizard.IWizardPage;
import org.moreunit.extensionpoints.INewTestCaseWizardPage;

public class NewTestCaseWizardComposer
{
    private final Map<String, IWizardPage> pagesById = new HashMap<String, IWizardPage>();
    private final List<INewTestCaseWizardPage> extensionPages = new ArrayList<INewTestCaseWizardPage>();
    private final List<String> orderedPageIds = new LinkedList<String>();

    public void registerExtensionPages(Collection<INewTestCaseWizardPage> pagesToAdd)
    {
        if(pagesToAdd == null)
        {
            return;
        }

        extensionPages.addAll(pagesToAdd);
        for (INewTestCaseWizardPage page : pagesToAdd)
        {
            pagesById.put(page.getId(), page.getPage());
        }
    }

    public void registerBasePage(String pageId, IWizardPage page)
    {
        pagesById.put(pageId, page);
        orderedPageIds.add(pageId);
    }

    public void compose(NewTestCaseWizard wizard)
    {
        orderPages();
        for (String pageId : orderedPageIds)
        {
            IWizardPage page = pagesById.get(pageId);
            page.setWizard(wizard);
            wizard.addPage(page);
        }
    }

    private void orderPages()
    {
        while (insertExtensionPages())
        {
            ; // iterate
        }

        if(! extensionPages.isEmpty())
        {
            for (INewTestCaseWizardPage extensionPage : extensionPages)
            {
                orderedPageIds.add(extensionPage.getId());
            }
            extensionPages.clear();
        }
    }

    private boolean insertExtensionPages()
    {
        int startSize = extensionPages.size();

        for (Iterator<INewTestCaseWizardPage> extensionPageIt = extensionPages.iterator(); extensionPageIt.hasNext();)
        {
            INewTestCaseWizardPage extensionPage = extensionPageIt.next();
            for (ListIterator<String> orderedPageIdIt = orderedPageIds.listIterator(); orderedPageIdIt.hasNext();)
            {
                String orderedPageId = orderedPageIdIt.next();
                if(extensionPage.getPosition().isAfter(orderedPageId))
                {
                    orderedPageIds.add(orderedPageIdIt.nextIndex(), extensionPage.getId());
                    extensionPageIt.remove();
                    break;
                }
                else if(extensionPage.getPosition().isBefore(orderedPageId))
                {
                    orderedPageIds.add(orderedPageIdIt.nextIndex() - 1, extensionPage.getId());
                    extensionPageIt.remove();
                    break;
                }
            }
        }

        return extensionPages.size() != startSize;
    }

    public List<INewTestCaseWizardPage> getExtensionPages()
    {
        return extensionPages;
    }
}
