package org.moreunit.wizards;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.extensionpoints.INewTestCaseWizardContext;
import org.moreunit.extensionpoints.INewTestCaseWizardPage;
import org.moreunit.extensionpoints.INewTestCaseWizardParticipator;
import org.moreunit.extensionpoints.TestType;
import org.moreunit.log.LogHandler;
import org.moreunit.util.TestSafeRunner;

@RunWith(MockitoJUnitRunner.class)
public class NewTestCaseWizardParticipatorManagerTest
{
    @Mock
    private LogHandler logger;
    @Mock
    private INewTestCaseWizardParticipator participator1;
    @Mock
    private INewTestCaseWizardParticipator participator2;
    @Mock
    private INewTestCaseWizardParticipator participator3;

    private Collection<INewTestCaseWizardParticipator> participators;

    private NewTestCaseWizardParticipatorManager manager;

    @Before
    public void createParticipatorManager() throws Exception
    {
        manager = new NewTestCaseWizardParticipatorManager(logger, new TestSafeRunner());
    }

    @Before
    public void createParticipators() throws Exception
    {
        when(participator1.getPages(any(INewTestCaseWizardContext.class))).thenReturn(asList(page("1"), page("2")));
        when(participator2.getPages(any(INewTestCaseWizardContext.class))).thenReturn(asList(page("3")));
        when(participator3.getPages(any(INewTestCaseWizardContext.class))).thenReturn(asList(page("4"), page("5"), page("6")));

        participators = asList(participator1, participator2, participator3);
    }

    private INewTestCaseWizardPage page(final String pageId)
    {
        return new ExtensionPage(pageId);
    }

    @Test
    public void should_get_extension_pages_and_pass_them_to_composer_in_the_same_order()
    {
        NewTestCaseWizardComposer composer = new NewTestCaseWizardComposer();
        manager.addExtensionPagesToComposer(composer, new TestContext(), participators);

        assertThat(composer.getExtensionPages(), is(equalTo(asList(page("1"), page("2"), page("3"), page("4"), page("5"), page("6")))));
    }

    @Test
    public void should_add_pages_of_other_participators_when_one_throws_an_exception()
    {
        // given
        when(participator2.getPages(any(INewTestCaseWizardContext.class))).thenThrow(new RuntimeException("test exception"));

        // when
        NewTestCaseWizardComposer composer = new NewTestCaseWizardComposer();
        manager.addExtensionPagesToComposer(composer, new TestContext(), participators);

        // then
        assertThat(composer.getExtensionPages(), is(equalTo(asList(page("1"), page("2"), page("4"), page("5"), page("6")))));
    }

    @Test
    public void should_relay_test_case_creation_success_to_participators()
    {
        // given
        TestContext context = new TestContext();

        // when
        manager.testCaseCreated(context, participators);

        // then
        verify(participator1).testCaseCreated(context);
        verify(participator2).testCaseCreated(context);
        verify(participator3).testCaseCreated(context);
    }

    @Test
    public void should_relay_test_case_creation_success_to_other_participators_when_one_throws_an_exception()
    {
        // given
        doThrow(new RuntimeException("test exception")).when(participator2).testCaseCreated(any(INewTestCaseWizardContext.class));

        TestContext context = new TestContext();

        // when
        manager.testCaseCreated(context, participators);

        // then
        verify(participator1).testCaseCreated(context);
        verify(participator3).testCaseCreated(context);
        // ensures exception has been thrown
        verify(participator2).testCaseCreated(context);
    }

    @Test
    public void should_relay_test_case_creation_cancelation_to_participators()
    {
        // given
        TestContext context = new TestContext();

        // when
        manager.testCaseCreationCanceled(context, participators);

        // then
        verify(participator1).testCaseCreationCanceled(context);
        verify(participator2).testCaseCreationCanceled(context);
        verify(participator3).testCaseCreationCanceled(context);
    }

    @Test
    public void should_relay_test_case_creation_cancelation_to_other_participators_when_one_throws_an_exception()
    {
        // given
        doThrow(new RuntimeException("test exception")).when(participator2).testCaseCreationCanceled(any(INewTestCaseWizardContext.class));

        TestContext context = new TestContext();

        // when
        manager.testCaseCreationCanceled(context, participators);

        // then
        verify(participator1).testCaseCreationCanceled(context);
        verify(participator3).testCaseCreationCanceled(context);
        // ensures exception has been thrown
        verify(participator2).testCaseCreationCanceled(context);
    }

    @Test
    public void should_relay_test_case_creation_abortion_to_participators()
    {
        // given
        TestContext context = new TestContext();

        // when
        manager.testCaseCreationAborted(context, participators);

        // then
        verify(participator1).testCaseCreationAborted(context);
        verify(participator2).testCaseCreationAborted(context);
        verify(participator3).testCaseCreationAborted(context);
    }

    @Test
    public void should_relay_test_case_creation_abortion_to_other_participators_when_one_throws_an_exception()
    {
        // given
        doThrow(new RuntimeException("test exception")).when(participator2).testCaseCreationAborted(any(INewTestCaseWizardContext.class));

        TestContext context = new TestContext();

        // when
        manager.testCaseCreationAborted(context, participators);

        // then
        verify(participator1).testCaseCreationAborted(context);
        verify(participator3).testCaseCreationAborted(context);
        // ensures exception has been thrown
        verify(participator2).testCaseCreationAborted(context);
    }

    private static class TestContext implements INewTestCaseWizardContext
    {
        public IType getClassUnderTest()
        {
            throw new UnsupportedOperationException("Just a fake object...");
        }

        public IType getCreatedTestCase()
        {
            throw new UnsupportedOperationException("Just a fake object...");
        }

        public IPackageFragment getTestCasePackage()
        {
            throw new UnsupportedOperationException("Just a fake object...");
        }

        @Override
        public TestType getTestType()
        {
            throw new UnsupportedOperationException("Just a fake object...");
        }

        public void put(String key, Object value)
        {
            throw new UnsupportedOperationException("Just a fake object...");
        }

        public <T> T get(String key)
        {
            throw new UnsupportedOperationException("Just a fake object...");
        }
    }
}
