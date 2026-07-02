package org.moreunit.mock.wizard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.moreunit.extensionpoints.INewTestCaseWizardContext;
import org.moreunit.extensionpoints.TestType;

public class NewTestCaseWizardParticipatorTest
{
    @Test
    public void should_return_null_when_no_class_under_test()
    {
        MockDependenciesPageManager pageManager = mock(MockDependenciesPageManager.class);
        INewTestCaseWizardContext context = mock(INewTestCaseWizardContext.class);
        when(context.getClassUnderTest()).thenReturn(null);

        NewTestCaseWizardParticipator participator = new NewTestCaseWizardParticipator(pageManager);

        assertNull(participator.getPages(context));
    }

    @Test
    public void should_ignore_page_creation_aborted()
    {
        NewTestCaseWizardParticipator participator = new NewTestCaseWizardParticipator(mock(MockDependenciesPageManager.class));

        participator.testCaseCreationAborted(null);
        participator.testCaseCreationCanceled(null);
    }

    @Test
    public void should_do_nothing_when_created_test_case_is_null()
    {
        INewTestCaseWizardContext context = mock(INewTestCaseWizardContext.class);
        when(context.getTestType()).thenReturn(TestType.JUNIT_5);

        NewTestCaseWizardParticipator participator = new NewTestCaseWizardParticipator(mock(MockDependenciesPageManager.class));

        // when: createdTestCase is null (the context.get(PAGE_KEY) returns null too)
        participator.testCaseCreated(context);
        // then: no interaction with pageManager; no exception
    }
}
