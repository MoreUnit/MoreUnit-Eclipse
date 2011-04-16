package org.moreunit.mock.wizard;

import java.util.Collection;

import org.moreunit.extensionpoints.INewTestCaseWizardContext;
import org.moreunit.extensionpoints.INewTestCaseWizardPage;
import org.moreunit.extensionpoints.INewTestCaseWizardParticipator;
import org.moreunit.mock.MoreUnitMockPlugin;
import org.moreunit.mock.utils.Features;

import com.google.inject.Inject;

import static java.util.Arrays.asList;

public class NewTestCaseWizardParticipator implements INewTestCaseWizardParticipator
{
    private static final String PAGE_KEY = MoreUnitMockPlugin.PLUGIN_ID + ".mockDependenciesWizardPage";

    private final MockDependenciesPageManager pageManager;

    @Inject
    public NewTestCaseWizardParticipator(MockDependenciesPageManager pageManager)
    {
        this.pageManager = pageManager;
    }

    public Collection<INewTestCaseWizardPage> getPages(INewTestCaseWizardContext context)
    {
        if(! Features.isActive(Features.NEW_TEST_CASE_WIZARD_DEPENDENCIES_PAGE))
        {
            return null;
        }

        if(context.getClassUnderTest() == null || context.getTestCasePackage() == null)
        {
            return null;
        }

        INewTestCaseWizardPage page = pageManager.createPage(context.getClassUnderTest(), context.getTestCasePackage());
        context.put(PAGE_KEY, page);

        return page == null ? null : asList(page);
    }

    public void testCaseCreated(INewTestCaseWizardContext context)
    {
        if(! Features.isActive(Features.NEW_TEST_CASE_WIZARD_DEPENDENCIES_PAGE))
        {
            return;
        }

        MockDependenciesWizardPage page = context.get(PAGE_KEY);
        if(page == null || context.getCreatedTestCase() == null)
        {
            return;
        }

        pageManager.pageValidated(page, context.getCreatedTestCase());
    }

    public void testCaseCreationAborted(INewTestCaseWizardContext context)
    {
        // ignored
    }

    public void testCaseCreationCanceled(INewTestCaseWizardContext context)
    {
        // ignored
    }
}
