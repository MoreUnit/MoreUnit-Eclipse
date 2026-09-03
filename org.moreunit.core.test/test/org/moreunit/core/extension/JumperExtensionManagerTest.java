package org.moreunit.core.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.core.extension.jump.IJumpContext;
import org.moreunit.core.extension.jump.IJumper;
import org.moreunit.core.extension.jump.JumpResult;
import org.moreunit.core.log.Logger;

public class JumperExtensionManagerTest
{
    private LanguageExtensionManager languageExtensionManager;
    private Logger logger;
    private JumperExtensionManager manager;

    @BeforeEach
    public void setUp()
    {
        languageExtensionManager = mock(LanguageExtensionManager.class);
        logger = mock(Logger.class);
        manager = new JumperExtensionManager(languageExtensionManager, logger);
    }

    @Test
    public void jump_should_return_notDone_when_no_jumpers_for_extension()
    {
        final IJumpContext context = createMockContext("java");

        when(languageExtensionManager.getJumpersFor("java")).thenReturn((Iterable<IJumper>) () -> java.util.Collections.emptyIterator());

        final JumpResult result = manager.jump(context);

        assertEquals(JumpResult.notDone(), result);
    }

    @Test
    public void jump_should_return_done_when_jumper_returns_done()
    {
        final IJumpContext context = createMockContext("java");
        final IJumper jumper = mock(IJumper.class);

        when(languageExtensionManager.getJumpersFor("java")).thenReturn(() -> java.util.Collections.singleton(jumper).iterator());
        when(jumper.jump(context)).thenReturn(JumpResult.done());

        final JumpResult result = manager.jump(context);

        assertEquals(JumpResult.done(), result);
    }

    @Test
    public void jump_should_stop_at_first_done_result()
    {
        final IJumpContext context = createMockContext("java");
        final IJumper jumper1 = mock(IJumper.class);
        final IJumper jumper2 = mock(IJumper.class);

        when(languageExtensionManager.getJumpersFor("java")).thenReturn(() -> java.util.Arrays.asList(jumper1, jumper2).iterator());
        when(jumper1.jump(context)).thenReturn(JumpResult.done());
        when(jumper2.jump(context)).thenReturn(JumpResult.notDone());

        final JumpResult result = manager.jump(context);

        assertEquals(JumpResult.done(), result);
        verify(jumper1).jump(context);
    }

    @Test
    public void jump_should_return_done_when_jumper_throws_exception()
    {
        final IJumpContext context = createMockContext("java");
        final IJumper jumper = mock(IJumper.class);

        when(languageExtensionManager.getJumpersFor("java")).thenReturn(() -> java.util.Collections.singleton(jumper).iterator());
        when(jumper.jump(context)).thenThrow(new RuntimeException("test exception"));

        final JumpResult result = manager.jump(context);

        assertEquals(JumpResult.done(), result);
    }

    @Test
    public void jump_should_return_done_when_jumper_returns_null()
    {
        final IJumpContext context = createMockContext("java");
        final IJumper jumper = mock(IJumper.class);

        when(languageExtensionManager.getJumpersFor("java")).thenReturn(() -> java.util.Collections.singleton(jumper).iterator());
        when(jumper.jump(context)).thenReturn(null);

        final JumpResult result = manager.jump(context);

        assertEquals(JumpResult.done(), result);
    }

    private IJumpContext createMockContext(String fileExtension)
    {
        final IJumpContext context = mock(IJumpContext.class);
        final IFile file = mock(IFile.class);
        when(file.getFileExtension()).thenReturn(fileExtension);
        when(context.getSelectedFile()).thenReturn(file);
        when(context.getExecutionEvent()).thenReturn(mock(ExecutionEvent.class));
        when(context.getOpenEditorPart()).thenReturn(mock(IEditorPart.class));
        when(context.isFileOpenInEditor()).thenReturn(false);
        return context;
    }
}