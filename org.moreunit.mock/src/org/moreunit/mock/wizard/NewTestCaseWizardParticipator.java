package org.moreunit.mock.wizard;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.moreunit.extensionpoints.INewTestCaseWizardContext;
import org.moreunit.extensionpoints.INewTestCaseWizardPage;
import org.moreunit.extensionpoints.INewTestCaseWizardParticipator;
import org.moreunit.extensionpoints.TestType;
import org.moreunit.mock.MoreUnitMockPlugin;
import org.moreunit.preferences.PreferenceConstants;

import static java.util.Arrays.asList;
import static org.moreunit.mock.config.MockModule.$;

public class NewTestCaseWizardParticipator implements INewTestCaseWizardParticipator
{
    // maps a TestType to a preference value (the value internal to TestType
    // should not be made public in order not to expose it to other extensions)
    private static final Map<TestType, String> MOREUNIT_TEST_TYPES = new HashMap<TestType, String>();
    static
    {
        MOREUNIT_TEST_TYPES.put(TestType.JUNIT_3, PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3);
        MOREUNIT_TEST_TYPES.put(TestType.JUNIT_4, PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4);
        MOREUNIT_TEST_TYPES.put(TestType.TESTNG, PreferenceConstants.TEST_TYPE_VALUE_TESTNG);
    }

    private static final String PAGE_KEY = MoreUnitMockPlugin.PLUGIN_ID + ".mockDependenciesWizardPage";

    private final MockDependenciesPageManager pageManager;

    public NewTestCaseWizardParticipator()
    {
        this($().getMockDependenciesPageManager());
    }

    public NewTestCaseWizardParticipator(MockDependenciesPageManager pageManager)
    {
        this.pageManager = pageManager;
    }

    @Override
    public Collection<INewTestCaseWizardPage> getPages(INewTestCaseWizardContext context)
    {
        if(context.getClassUnderTest() == null)
        {
            return null;
        }

        INewTestCaseWizardPage page = pageManager.createPage(context);
        context.put(PAGE_KEY, page);

        return page == null ? null : asList(page);
    }

    @Override
    public void testCaseCreated(INewTestCaseWizardContext context)
    {
        MockDependenciesWizardPage page = context.get(PAGE_KEY);
        if(page == null || context.getCreatedTestCase() == null)
        {
            return;
        }

        pageManager.pageValidated(page, context.getCreatedTestCase(), asPrefValue(context.getTestType()));
    }

    private String asPrefValue(TestType type)
    {
        return MOREUNIT_TEST_TYPES.containsKey(type) ? MOREUNIT_TEST_TYPES.get(type) : PreferenceConstants.DEFAULT_TEST_TYPE;
    }

    @Override
    public void testCaseCreationAborted(INewTestCaseWizardContext context)
    {
        // ignored
    }

    @Override
    public void testCaseCreationCanceled(INewTestCaseWizardContext context)
    {
        // ignored
    }
}
