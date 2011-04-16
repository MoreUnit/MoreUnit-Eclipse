package org.moreunit.extensionpoints;

import java.util.Collection;

/**
 * A participator to the "New Test Case" wizard, that may contribute to the
 * wizard pages and/or listen to events related the creation of the test case.
 */
public interface INewTestCaseWizardParticipator
{
    /**
     * Returns pages to add to the wizard, or <tt>null</tt> if this participator
     * does not want to provide pages.
     * 
     * @param context the context of the test case creation
     * @return a collection of pages to add to the wizard, or <tt>null</tt>
     */
    Collection<INewTestCaseWizardPage> getPages(INewTestCaseWizardContext context);

    /**
     * Called once the test case has been created.
     * 
     * @param context the context of the test case creation
     */
    void testCaseCreated(INewTestCaseWizardContext context);

    /**
     * Called if the test case creation has aborted because of some failure;
     * 
     * @param context the context of the test case creation
     */
    void testCaseCreationAborted(INewTestCaseWizardContext context);

    /**
     * Called if the test case creation has been canceled by the user
     * 
     * @param context the context of the test case creation
     */
    void testCaseCreationCanceled(INewTestCaseWizardContext context);
}
