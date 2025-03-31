package org.moreunit.wizards;

import static java.util.Arrays.asList;

import static org.mockito.Mockito.*;
import static org.moreunit.extensionpoints.NewTestCaseWizardPagePosition.after;
import static org.moreunit.extensionpoints.NewTestCaseWizardPagePosition.before;

import org.eclipse.jface.wizard.IWizardPage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.moreunit.extensionpoints.INewTestCaseWizardPage;

@RunWith(MockitoJUnitRunner.class)
public class NewTestCaseWizardComposerTest
{
    @Mock
    private NewTestCaseWizard wizard;
    @Mock
    private IWizardPage basePageX;
    @Mock
    private IWizardPage basePageZz;

    private NewTestCaseWizardComposer composer = new NewTestCaseWizardComposer();

    @Before
    public void createBasePages() throws Exception
    {
        MockitoAnnotations.initMocks(this);
        composer.registerBasePage("page ZZ", basePageZz);
        composer.registerBasePage("page x", basePageX);
    }

    @Test
    public void should_add_base_pages_in_registration_order() throws Exception
    {
        // when
        composer.compose(wizard);

        // then
        InOrder inOrder = Mockito.inOrder(wizard);
        verifyThatPageHasBeenAdded(inOrder, basePageZz);
        verifyThatPageHasBeenAdded(inOrder, basePageX);
        inOrder.verifyNoMoreInteractions();
    }

    private void verifyThatPageHasBeenAdded(InOrder inOrder, IWizardPage page)
    {
        inOrder.verify(wizard).addPage(page);
        verify(page).setWizard(wizard);
    }

    @Test
    public void should_accept_null_pages() throws Exception
    {
        // when
        composer.registerExtensionPages(null);
        composer.compose(wizard);

        // then
        InOrder inOrder = Mockito.inOrder(wizard);
        verifyThatPageHasBeenAdded(inOrder, basePageZz);
        verifyThatPageHasBeenAdded(inOrder, basePageX);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void should_add_extension_page_after_requested_page() throws Exception
    {
        // given
        INewTestCaseWizardPage extensionPage = new ExtensionPage("test page", mock(IWizardPage.class), after("page ZZ"));
        composer.registerExtensionPages(asList(extensionPage));

        // when
        composer.compose(wizard);

        // then
        InOrder inOrder = Mockito.inOrder(wizard);
        verifyThatPageHasBeenAdded(inOrder, basePageZz);
        verifyThatPageHasBeenAdded(inOrder, extensionPage.getPage());
        verifyThatPageHasBeenAdded(inOrder, basePageX);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void should_add_extension_page_after_last_known_page() throws Exception
    {
        // given
        INewTestCaseWizardPage extensionPage = new ExtensionPage("test page", mock(IWizardPage.class), after("page x"));
        composer.registerExtensionPages(asList(extensionPage));

        // when
        composer.compose(wizard);

        // then
        InOrder inOrder = Mockito.inOrder(wizard);
        verifyThatPageHasBeenAdded(inOrder, basePageZz);
        verifyThatPageHasBeenAdded(inOrder, basePageX);
        verifyThatPageHasBeenAdded(inOrder, extensionPage.getPage());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void should_add_extension_page_after_another_extension_page() throws Exception
    {
        // given
        INewTestCaseWizardPage extensionPageA = new ExtensionPage("page A", mock(IWizardPage.class), after("page x"));
        INewTestCaseWizardPage extensionPageB = new ExtensionPage("page B", mock(IWizardPage.class), after("page ZZ"));
        composer.registerExtensionPages(asList(extensionPageA, extensionPageB));

        INewTestCaseWizardPage testedExtensionPage = new ExtensionPage("test page", mock(IWizardPage.class), after("page B"));
        composer.registerExtensionPages(asList(testedExtensionPage));

        // when
        composer.compose(wizard);

        // then
        InOrder inOrder = Mockito.inOrder(wizard);
        verifyThatPageHasBeenAdded(inOrder, basePageZz);
        verifyThatPageHasBeenAdded(inOrder, extensionPageB.getPage());
        verifyThatPageHasBeenAdded(inOrder, testedExtensionPage.getPage());
        verifyThatPageHasBeenAdded(inOrder, basePageX);
        verifyThatPageHasBeenAdded(inOrder, extensionPageA.getPage());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void should_add_extension_page_before_requested_page() throws Exception
    {
        // given
        INewTestCaseWizardPage extensionPage = new ExtensionPage("test page", mock(IWizardPage.class), before("page x"));
        composer.registerExtensionPages(asList(extensionPage));

        // when
        composer.compose(wizard);

        // then
        InOrder inOrder = Mockito.inOrder(wizard);
        verifyThatPageHasBeenAdded(inOrder, basePageZz);
        verifyThatPageHasBeenAdded(inOrder, extensionPage.getPage());
        verifyThatPageHasBeenAdded(inOrder, basePageX);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void should_add_extension_page_before_first_known_page() throws Exception
    {
        // given
        INewTestCaseWizardPage extensionPage = new ExtensionPage("test page", mock(IWizardPage.class), before("page ZZ"));
        composer.registerExtensionPages(asList(extensionPage));

        // when
        composer.compose(wizard);

        // then
        InOrder inOrder = Mockito.inOrder(wizard);
        verifyThatPageHasBeenAdded(inOrder, extensionPage.getPage());
        verifyThatPageHasBeenAdded(inOrder, basePageZz);
        verifyThatPageHasBeenAdded(inOrder, basePageX);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void should_add_extension_page_before_another_extension_page() throws Exception
    {
        // given
        INewTestCaseWizardPage extensionPageA = new ExtensionPage("page A", mock(IWizardPage.class), after("page x"));
        INewTestCaseWizardPage extensionPageB = new ExtensionPage("page B", mock(IWizardPage.class), after("page ZZ"));
        composer.registerExtensionPages(asList(extensionPageA, extensionPageB));

        INewTestCaseWizardPage testedExtensionPage = new ExtensionPage("test page", mock(IWizardPage.class), before("page B"));
        composer.registerExtensionPages(asList(testedExtensionPage));

        // when
        composer.compose(wizard);

        // then
        InOrder inOrder = Mockito.inOrder(wizard);
        verifyThatPageHasBeenAdded(inOrder, basePageZz);
        verifyThatPageHasBeenAdded(inOrder, testedExtensionPage.getPage());
        verifyThatPageHasBeenAdded(inOrder, extensionPageB.getPage());
        verifyThatPageHasBeenAdded(inOrder, basePageX);
        verifyThatPageHasBeenAdded(inOrder, extensionPageA.getPage());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void should_add_extension_page_at_last_position_when_after_unknown_page() throws Exception
    {
        // given
        INewTestCaseWizardPage extensionPage = new ExtensionPage("test page", mock(IWizardPage.class), after("page that does not exist"));
        composer.registerExtensionPages(asList(extensionPage));

        // when
        composer.compose(wizard);

        // then
        InOrder inOrder = Mockito.inOrder(wizard);
        verifyThatPageHasBeenAdded(inOrder, basePageZz);
        verifyThatPageHasBeenAdded(inOrder, basePageX);
        verifyThatPageHasBeenAdded(inOrder, extensionPage.getPage());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void should_add_extension_page_at_last_position_when_before_unknown_page() throws Exception
    {
        // given
        INewTestCaseWizardPage extensionPage = new ExtensionPage("test page", mock(IWizardPage.class), before("page that does not exist"));
        composer.registerExtensionPages(asList(extensionPage));

        // when
        composer.compose(wizard);

        // then
        InOrder inOrder = Mockito.inOrder(wizard);
        verifyThatPageHasBeenAdded(inOrder, basePageZz);
        verifyThatPageHasBeenAdded(inOrder, basePageX);
        verifyThatPageHasBeenAdded(inOrder, extensionPage.getPage());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void composition_should_be_repeatable() throws Exception
    {
        // given
        INewTestCaseWizardPage extensionPage1 = new ExtensionPage("test page 1", mock(IWizardPage.class), before("page x"));
        INewTestCaseWizardPage extensionPage2 = new ExtensionPage("test page 2", mock(IWizardPage.class), before("page that does not exist"));
        composer.registerExtensionPages(asList(extensionPage1, extensionPage2));

        // when
        composer.compose(wizard);

        // then
        InOrder inOrder = Mockito.inOrder(wizard);
        verifyThatPageHasBeenAdded(inOrder, basePageZz);
        verifyThatPageHasBeenAdded(inOrder, extensionPage1.getPage());
        verifyThatPageHasBeenAdded(inOrder, basePageX);
        verifyThatPageHasBeenAdded(inOrder, extensionPage2.getPage());
        inOrder.verifyNoMoreInteractions();

        // given
        wizard = mock(NewTestCaseWizard.class);

        // when composed another time
        composer.compose(wizard);

        // then
        inOrder = Mockito.inOrder(wizard);
        verifyThatPageHasBeenAdded(inOrder, basePageZz);
        verifyThatPageHasBeenAdded(inOrder, extensionPage1.getPage());
        verifyThatPageHasBeenAdded(inOrder, basePageX);
        verifyThatPageHasBeenAdded(inOrder, extensionPage2.getPage());
        inOrder.verifyNoMoreInteractions();
    }
}
