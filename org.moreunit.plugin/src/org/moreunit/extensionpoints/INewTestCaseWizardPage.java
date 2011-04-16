package org.moreunit.extensionpoints;

import org.eclipse.jface.wizard.IWizardPage;
import org.moreunit.MoreUnitPlugin;

/**
 * A page for the "New Test Case" wizard.
 */
public interface INewTestCaseWizardPage
{
    /**
     * The ID of the "Test Case" page of the wizard.
     */
    String TEST_CASE_PAGE = MoreUnitPlugin.PLUGIN_ID + ".newTestCaseWizard.testCasePage";

    /**
     * The ID of the "Test Methods" page of the wizard.
     */
    String TEST_METHODS_PAGE = MoreUnitPlugin.PLUGIN_ID + ".newTestCaseWizard.testMethodsPage";

    /**
     * Returns an ID that will uniquely represent this page in the platform.
     * <p>
     * For instance: <tt>org.moreunit.newTestCaseWizard.testCasePage</tt> is the
     * ID of the first page of the wizard.
     * </p>
     */
    String getId();

    /**
     * The {@link IWizardPage} that will be included in the wizard.
     */
    IWizardPage getPage();

    /**
     * The position at which the page should be placed.
     */
    NewTestCaseWizardPagePosition getPosition();
}
