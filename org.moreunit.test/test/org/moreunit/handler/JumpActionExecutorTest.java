package org.moreunit.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.ui.EditorUI;

@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "com:Foo", testCls = "com:FooTest")
public class JumpActionExecutorTest extends ContextTestCase
{
    private final List<IJavaElement> openedElements = new CopyOnWriteArrayList<>();
    private final List<Object[]> revealArguments = new CopyOnWriteArrayList<>();
    private EditorUI editorUI;

    @BeforeEach
    public void setUp()
    {
        openedElements.clear();
        revealArguments.clear();

        editorUI = mock(EditorUI.class);
        when(editorUI.open(any())).thenAnswer(invocation -> {
            openedElements.add(invocation.getArgument(0));
            return null;
        });
        doRevealAnswerRecordsArguments();
    }

    private void doRevealAnswerRecordsArguments()
    {
        // any() (without class) is used on purpose: any(Class) does not match null
        org.mockito.Mockito.doAnswer(invocation -> {
            revealArguments.add(new Object[] { invocation.getArgument(0), invocation.getArgument(1) });
            return null;
        }).when(editorUI).reveal(any(), any());
    }

    /**
     * The EditorUI-based constructor is package-private, which is not
     * accessible from another bundle at runtime, hence the reflection.
     */
    private JumpActionExecutor newExecutorWithMockedEditorUI()
    {
        try
        {
            var constructor = JumpActionExecutor.class.getDeclaredConstructor(EditorUI.class);
            constructor.setAccessible(true);
            return constructor.newInstance(editorUI);
        }
        catch (ReflectiveOperationException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void await(Runnable assertion) throws Exception
    {
        Display display = Display.getDefault();
        long deadline = System.currentTimeMillis() + 30_000;
        AssertionError lastFailure = null;
        while (System.currentTimeMillis() < deadline)
        {
            display.readAndDispatch();
            try
            {
                assertion.run();
                return;
            }
            catch (AssertionError e)
            {
                lastFailure = e;
            }
            Thread.sleep(10);
        }
        throw lastFailure != null ? lastFailure : new AssertionError("condition not met in time");
    }

    private IEditorPart editorOver(ICompilationUnit compilationUnit, ISourceRange selectionRange)
    {
        IEditorPart editorPart = mock(IEditorPart.class);
        IEditorInput editorInput = mock(IEditorInput.class);
        IWorkbenchPartSite site = mock(IWorkbenchPartSite.class);
        ISelectionProvider selectionProvider = mock(ISelectionProvider.class);

        when(editorPart.getEditorInput()).thenReturn(editorInput);
        when(editorInput.getAdapter(IFile.class)).thenReturn((IFile) compilationUnit.getResource());
        when(editorPart.getSite()).thenReturn(site);
        when(site.getSelectionProvider()).thenReturn(selectionProvider);
        when(selectionProvider.getSelection()).thenReturn(new TextSelection(selectionRange.getOffset(), selectionRange.getLength()));

        return editorPart;
    }

    @Test
    public void getInstance_should_return_singleton()
    {
        assertSame(JumpActionExecutor.getInstance(), JumpActionExecutor.getInstance());
    }

    @Test
    public void executeJumpAction_should_jump_from_class_to_corresponding_test_case() throws Exception
    {
        ICompilationUnit foo = context.getCompilationUnit("com.Foo");
        ICompilationUnit fooTest = context.getCompilationUnit("com.FooTest");

        newExecutorWithMockedEditorUI().executeJumpAction(foo);

        await(() -> assertEquals(Collections.singletonList(fooTest), openedElements));
    }

    @Test
    public void executeJumpAction_should_jump_from_test_case_to_corresponding_class() throws Exception
    {
        ICompilationUnit fooTest = context.getCompilationUnit("com.FooTest");
        ICompilationUnit foo = context.getCompilationUnit("com.Foo");

        newExecutorWithMockedEditorUI().executeJumpAction(fooTest);

        await(() -> assertEquals(Collections.singletonList(foo), openedElements));
    }

    @Test
    public void executeJumpAction_should_accept_a_file_and_jump_to_corresponding_member() throws Exception
    {
        IFile fooFile = (IFile) context.getCompilationUnit("com.Foo").getResource();
        ICompilationUnit fooTest = context.getCompilationUnit("com.FooTest");

        newExecutorWithMockedEditorUI().executeJumpAction(fooFile);

        await(() -> assertEquals(Collections.singletonList(fooTest), openedElements));
    }

    @Test
    public void executeJumpAction_should_jump_to_corresponding_test_method_and_reveal_it() throws Exception
    {
        IMethod foo = context.getPrimaryTypeHandler("com.Foo").addMethod("public int foo()", "return 0;").get();
        IMethod testFoo = context.getPrimaryTypeHandler("com.FooTest").addMethod("@Test\npublic void foo()", "").get();
        ICompilationUnit fooTest = context.getCompilationUnit("com.FooTest");

        IEditorPart editorPart = editorOver(context.getCompilationUnit("com.Foo"), foo.getNameRange());

        newExecutorWithMockedEditorUI().executeJumpAction(editorPart);

        await(() -> {
            assertEquals(Collections.singletonList(fooTest), openedElements);
            assertEquals(1, revealArguments.size());
            // the mocked EditorUI.open returns null, which is forwarded to reveal
            assertEquals(null, revealArguments.get(0)[0]);
            assertEquals(testFoo, revealArguments.get(0)[1]);
        });
    }

    @Test
    public void revealInEditor_should_delegate_to_editor_ui()
    {
        IEditorPart editorPart = mock(IEditorPart.class);
        IMethod method = mock(IMethod.class);

        try
        {
            var reveal = JumpActionExecutor.class.getDeclaredMethod("revealInEditor", IEditorPart.class, IMethod.class);
            reveal.setAccessible(true);
            reveal.invoke(newExecutorWithMockedEditorUI(), editorPart, method);
        }
        catch (ReflectiveOperationException e)
        {
            throw new RuntimeException(e);
        }

        verify(editorUI).reveal(eq(editorPart), eq(method));
        assertTrue(openedElements.isEmpty());
    }
}
