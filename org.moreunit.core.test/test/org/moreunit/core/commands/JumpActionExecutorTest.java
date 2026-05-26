package org.moreunit.core.commands;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.core.commands.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.core.extension.JumperExtensionManager;
import org.moreunit.core.extension.jump.IJumpContext;
import org.moreunit.core.extension.jump.JumpResult;
import org.moreunit.core.matching.DoesNotMatchConfigurationException;
import org.moreunit.core.matching.MatchingFile;
import org.moreunit.core.resources.SrcFile;
import org.moreunit.core.ui.UserInterface;

public class JumpActionExecutorTest
{

    private JumperExtensionManager extensionManager;
    private JumpActionExecutor executor;
    private ExecutionContext context;
    private Selection selection;
    private UserInterface ui;
    private SelectedSrcFile selectedFile;

    @BeforeEach
    public void setUp()
    {
        extensionManager = mock(JumperExtensionManager.class);
        executor = new JumpActionExecutor(extensionManager);
        context = mock(ExecutionContext.class);
        selection = mock(Selection.class);
        ui = mock(UserInterface.class);
        selectedFile = mock(SelectedSrcFile.class);

        when(context.getSelection()).thenReturn(selection);
        when(context.getUserInterface()).thenReturn(ui);
        when(selection.getUniqueSrcFile()).thenReturn(selectedFile);
    }

    @Test
    public void testExecute_WhenFileNotSupported_DoesNothing() throws ExecutionException
    {
        when(selectedFile.isSupported()).thenReturn(false);

        executor.execute(context);

        verifyNoInteractions(extensionManager);
    }

    @Test
    public void testExecute_WhenJumpResultIsDone_DoesNothingElse() throws ExecutionException
    {
        when(selectedFile.isSupported()).thenReturn(true);
        IJumpContext jumpContext = mock(IJumpContext.class);
        when(selectedFile.createJumpContext()).thenReturn(jumpContext);

        JumpResult jumpResult = JumpResult.done();
        when(extensionManager.jump(jumpContext)).thenReturn(jumpResult);

        executor.execute(context);

        verify(extensionManager).jump(jumpContext);
        verifyNoInteractions(ui);
    }

    @Test
    public void testExecute_WhenSearchIsCancelled_DoesNothingElse() throws ExecutionException, DoesNotMatchConfigurationException
    {
        when(selectedFile.isSupported()).thenReturn(true);
        IJumpContext jumpContext = mock(IJumpContext.class);
        when(selectedFile.createJumpContext()).thenReturn(jumpContext);

        JumpResult jumpResult = JumpResult.notDone();
        when(extensionManager.jump(jumpContext)).thenReturn(jumpResult);

        SrcFile srcFile = mock(SrcFile.class);
        when(selectedFile.getSrcFile()).thenReturn(srcFile);

        MatchingFile matchingFile = mock(MatchingFile.class);
        when(matchingFile.isSearchCancelled()).thenReturn(true);
        when(srcFile.findUniqueMatch()).thenReturn(matchingFile);

        executor.execute(context);

        verify(extensionManager).jump(jumpContext);
        verifyNoInteractions(ui);
    }
}
