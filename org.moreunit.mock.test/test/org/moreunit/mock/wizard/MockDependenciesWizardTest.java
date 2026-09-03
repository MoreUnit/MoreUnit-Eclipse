package org.moreunit.mock.wizard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MockDependenciesWizardTest
{
    @Mock
    private WizardFactory wizardFactory;
    @Mock
    private MockDependenciesWizardPage page;
    @Mock
    private WizardDialog dialog;

    private Shell shell;

    private MockDependenciesWizard wizard;

    @BeforeEach
    public void setUp()
    {
        MockitoAnnotations.openMocks(this);

        shell = new Shell();
        wizard = new MockDependenciesWizard(wizardFactory, page);
        when(wizardFactory.createWizardDialog(any(Shell.class), same(wizard))).thenReturn(dialog);
    }

    @Test
    public void should_add_page_to_wizard()
    {
        final IWizardPage[] pages = wizard.getPages();

        assertEquals(1, pages.length);
        assertSame(page, pages[0]);
    }

    @Test
    public void should_return_true_when_user_finishes_wizard()
    {
        assertTrue(wizard.performFinish());
    }

    @Test
    public void should_return_false_when_user_cancels_wizard()
    {
        // given
        wizard.init(workbenchWithShell(), null);

        when(dialog.open()).thenReturn(org.eclipse.jface.window.Window.CANCEL);

        // when + then
        assertEquals(false, wizard.openAndReturnIfOk());
        verify(wizardFactory).createWizardDialog(shell, wizard);
    }

    @Test
    public void should_return_true_when_user_confirms_wizard()
    {
        // given
        wizard.init(workbenchWithShell(), null);
        when(dialog.open()).thenReturn(org.eclipse.jface.window.Window.OK);

        // when + then
        assertEquals(true, wizard.openAndReturnIfOk());
        verify(wizardFactory).createWizardDialog(shell, wizard);
    }

    private IWorkbench workbenchWithShell()
    {
        final IWorkbench workbench = mock(IWorkbench.class);
        final IWorkbenchWindow window = mock(IWorkbenchWindow.class);
        when(workbench.getActiveWorkbenchWindow()).thenReturn(window);
        when(window.getShell()).thenReturn(shell);
        return workbench;
    }
}
