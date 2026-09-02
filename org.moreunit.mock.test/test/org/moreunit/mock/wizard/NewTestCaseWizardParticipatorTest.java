package org.moreunit.mock.wizard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.eclipse.jdt.core.IType;
import org.junit.jupiter.api.Test;
import org.moreunit.extensionpoints.INewTestCaseWizardContext;
import org.moreunit.extensionpoints.INewTestCaseWizardPage;
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

    @Test
    public void should_return_created_page_when_class_under_test_is_defined()
    {
        // given
        MockDependenciesPageManager pageManager = mock(MockDependenciesPageManager.class);
        INewTestCaseWizardContext context = mock(INewTestCaseWizardContext.class);
        when(context.getClassUnderTest()).thenReturn(mock(IType.class));
        MockDependenciesWizardPage page = mock(MockDependenciesWizardPage.class);
        when(pageManager.createPage(context)).thenReturn(page);

        NewTestCaseWizardParticipator participator = new NewTestCaseWizardParticipator(pageManager);

        // when
        Collection<INewTestCaseWizardPage> pages = participator.getPages(context);

        // then
        assertNotNull(pages);
        assertEquals(1, pages.size());
        assertSame(page, pages.iterator().next());
        verify(context).put(anyString(), same(page));
    }

    @Test
    public void should_return_null_when_page_could_not_be_created()
    {
        // given
        MockDependenciesPageManager pageManager = mock(MockDependenciesPageManager.class);
        INewTestCaseWizardContext context = mock(INewTestCaseWizardContext.class);
        when(context.getClassUnderTest()).thenReturn(mock(IType.class));
        when(pageManager.createPage(context)).thenReturn(null);

        NewTestCaseWizardParticipator participator = new NewTestCaseWizardParticipator(pageManager);

        // when + then
        assertNull(participator.getPages(context));
    }

    @Test
    public void should_validate_page_with_test_type_of_context_when_test_case_is_created()
    {
        // given
        MockDependenciesPageManager pageManager = mock(MockDependenciesPageManager.class);
        INewTestCaseWizardContext context = mock(INewTestCaseWizardContext.class);
        MockDependenciesWizardPage page = mock(MockDependenciesWizardPage.class);
        when(context.get(anyString())).thenReturn(page);
        when(context.getCreatedTestCase()).thenReturn(mock(IType.class));
        when(context.getTestType()).thenReturn(TestType.TESTNG);

        NewTestCaseWizardParticipator participator = new NewTestCaseWizardParticipator(pageManager);

        // when
        participator.testCaseCreated(context);

        // then
        verify(pageManager).pageValidated(eq(page), any(IType.class), eq("testng"));
    }

    @Test
    public void should_use_default_test_type_when_test_type_is_unknown()
    {
        // given
        MockDependenciesPageManager pageManager = mock(MockDependenciesPageManager.class);
        INewTestCaseWizardContext context = mock(INewTestCaseWizardContext.class);
        MockDependenciesWizardPage page = mock(MockDependenciesWizardPage.class);
        when(context.get(anyString())).thenReturn(page);
        when(context.getCreatedTestCase()).thenReturn(mock(IType.class));
        when(context.getTestType()).thenReturn(null);

        NewTestCaseWizardParticipator participator = new NewTestCaseWizardParticipator(pageManager);

        // when
        participator.testCaseCreated(context);

        // then
        verify(pageManager).pageValidated(eq(page), any(IType.class), eq("junit5"));
    }
}
