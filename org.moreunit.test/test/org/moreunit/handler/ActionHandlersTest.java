package org.moreunit.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.IEditorPart;
import org.junit.jupiter.api.Test;
import org.moreunit.core.extension.jump.IJumpContext;
import org.moreunit.core.extension.jump.JumpResult;
import org.moreunit.util.PluginTools;

/**
 * Tests the small command handlers delegating to the action executors, plus
 * the {@link Jumper} used by the org.moreunit.core jump extension point.
 */
public class ActionHandlersTest
{
    private final IEditorPart editorPart = mock(IEditorPart.class);

    @Test
    public void runTestActionHandler_should_run_tests_of_open_editor() throws Exception
    {
        try (var pluginTools = mockStatic(PluginTools.class);
             var executor = mockStatic(RunTestsActionExecutor.class))
        {
            pluginTools.when(PluginTools::getOpenEditorPart).thenReturn(editorPart);
            RunTestsActionExecutor executorInstance = mock(RunTestsActionExecutor.class);
            executor.when(RunTestsActionExecutor::getInstance).thenReturn(executorInstance);

            assertEquals(null, new RunTestActionHandler().execute(mock(ExecutionEvent.class)));

            verify(executorInstance).executeRunTestAction(editorPart, ILaunchManager.RUN_MODE);
        }
    }

    @Test
    public void debugTestActionHandler_should_debug_tests_of_open_editor() throws Exception
    {
        try (var pluginTools = mockStatic(PluginTools.class);
             var executor = mockStatic(RunTestsActionExecutor.class))
        {
            pluginTools.when(PluginTools::getOpenEditorPart).thenReturn(editorPart);
            RunTestsActionExecutor executorInstance = mock(RunTestsActionExecutor.class);
            executor.when(RunTestsActionExecutor::getInstance).thenReturn(executorInstance);

            new DebugTestActionHandler().execute(mock(ExecutionEvent.class));

            verify(executorInstance).executeRunTestAction(editorPart, ILaunchManager.DEBUG_MODE);
        }
    }

    @Test
    public void runTestsOfSelectedMemberActionHandler_should_run_tests_of_selected_member() throws Exception
    {
        try (var pluginTools = mockStatic(PluginTools.class);
             var executor = mockStatic(RunTestsActionExecutor.class))
        {
            pluginTools.when(PluginTools::getOpenEditorPart).thenReturn(editorPart);
            RunTestsActionExecutor executorInstance = mock(RunTestsActionExecutor.class);
            executor.when(RunTestsActionExecutor::getInstance).thenReturn(executorInstance);

            new RunTestsOfSelectedMemberActionHandler().execute(mock(ExecutionEvent.class));

            verify(executorInstance).executeRunTestsOfSelectedMemberAction(editorPart, ILaunchManager.RUN_MODE);
        }
    }

    @Test
    public void debugTestsOfSelectedMemberActionHandler_should_debug_tests_of_selected_member() throws Exception
    {
        try (var pluginTools = mockStatic(PluginTools.class);
             var executor = mockStatic(RunTestsActionExecutor.class))
        {
            pluginTools.when(PluginTools::getOpenEditorPart).thenReturn(editorPart);
            RunTestsActionExecutor executorInstance = mock(RunTestsActionExecutor.class);
            executor.when(RunTestsActionExecutor::getInstance).thenReturn(executorInstance);

            new DebugTestsOfSelectedMemberActionHandler().execute(mock(ExecutionEvent.class));

            verify(executorInstance).executeRunTestsOfSelectedMemberAction(editorPart, ILaunchManager.DEBUG_MODE);
        }
    }

    @Test
    public void handlers_should_be_enabled_by_default() throws Exception
    {
        assertTrue(new RunTestActionHandler().isEnabled());
        assertTrue(new DebugTestActionHandler().isEnabled());
        assertTrue(new RunTestsOfSelectedMemberActionHandler().isEnabled());
        assertTrue(new DebugTestsOfSelectedMemberActionHandler().isEnabled());
        assertTrue(new JumpActionHandler().isEnabled());
        assertTrue(new CreateTestMethodActionHandler().isEnabled());
    }

    @Test
    public void jumpActionHandler_should_jump_from_open_editor() throws Exception
    {
        try (var pluginTools = mockStatic(PluginTools.class);
             var executor = mockStatic(JumpActionExecutor.class))
        {
            pluginTools.when(PluginTools::getOpenEditorPart).thenReturn(editorPart);
            JumpActionExecutor executorInstance = mock(JumpActionExecutor.class);
            executor.when(JumpActionExecutor::getInstance).thenReturn(executorInstance);

            new JumpActionHandler().execute(mock(ExecutionEvent.class));

            verify(executorInstance).executeJumpAction(editorPart);
        }
    }

    @Test
    public void createTestMethodActionHandler_should_delegate_to_executor() throws Exception
    {
        try (var pluginTools = mockStatic(PluginTools.class);
             var executor = mockStatic(CreateTestMethodActionExecutor.class))
        {
            pluginTools.when(PluginTools::getOpenEditorPart).thenReturn(editorPart);
            CreateTestMethodActionExecutor executorInstance = mock(CreateTestMethodActionExecutor.class);
            executor.when(CreateTestMethodActionExecutor::getInstance).thenReturn(executorInstance);

            new CreateTestMethodActionHandler().execute(mock(ExecutionEvent.class));

            verify(executorInstance).executeCreateTestMethodAction(editorPart);
        }
    }

    @Test
    public void jumper_should_jump_from_open_editor_when_file_is_open() throws Exception
    {
        try (var executor = mockStatic(JumpActionExecutor.class))
        {
            JumpActionExecutor executorInstance = mock(JumpActionExecutor.class);
            executor.when(JumpActionExecutor::getInstance).thenReturn(executorInstance);

            IJumpContext context = mock(IJumpContext.class);
            when(context.isFileOpenInEditor()).thenReturn(true);
            when(context.getOpenEditorPart()).thenReturn(editorPart);

            JumpResult result = new Jumper().jump(context);

            verify(executorInstance).executeJumpAction(editorPart);
            assertEquals(JumpResult.done(), result);
        }
    }

    @Test
    public void jumper_should_jump_from_selected_file_when_no_file_is_open() throws Exception
    {
        try (var executor = mockStatic(JumpActionExecutor.class))
        {
            JumpActionExecutor executorInstance = mock(JumpActionExecutor.class);
            executor.when(JumpActionExecutor::getInstance).thenReturn(executorInstance);

            IJumpContext context = mock(IJumpContext.class);
            when(context.isFileOpenInEditor()).thenReturn(false);
            IFile selectedFile = mock(IFile.class);
            when(context.getSelectedFile()).thenReturn(selectedFile);

            JumpResult result = new Jumper().jump(context);

            verify(executorInstance).executeJumpAction(selectedFile);
            assertEquals(JumpResult.done(), result);
        }
    }
}
