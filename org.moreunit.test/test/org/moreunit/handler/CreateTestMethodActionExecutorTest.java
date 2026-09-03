package org.moreunit.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

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
import org.moreunit.elements.TestmethodCreator;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.ui.EditorUI;

@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "com:Foo", testCls = "com:FooTest")
public class CreateTestMethodActionExecutorTest extends ContextTestCase
{
    private final List<IMethod> openedMethods = new CopyOnWriteArrayList<>();
    private final List<Object[]> revealedElements = new CopyOnWriteArrayList<>();
    private final AtomicInteger selectionCount = new AtomicInteger();
    private EditorUI editorUI;

    @BeforeEach
    public void setUp() throws Exception
    {
        TestmethodCreator.discardExtensions = true;
        openedMethods.clear();
        revealedElements.clear();
        selectionCount.set(0);

        awaitTestTypeAvailable();

        editorUI = mock(EditorUI.class);
        when(editorUI.open(any(IJavaElement.class))).thenAnswer(invocation -> {
            openedMethods.add(invocation.getArgument(0));
            return null;
        });
        org.mockito.Mockito.doAnswer(invocation -> {
            revealedElements.add(new Object[] { invocation.getArgument(0), invocation.getArgument(1) });
            return null;
        }).when(editorUI).reveal(any(IEditorPart.class), any(IJavaElement.class));
    }

    /**
     * The test project is created asynchronously with respect to the JDT
     * model: this helper waits until the test class type is resolvable.
     */
    private void awaitTestTypeAvailable() throws Exception
    {
        final org.eclipse.swt.widgets.Display display = org.eclipse.swt.widgets.Display.getDefault();
        final long deadline = System.currentTimeMillis() + 20_000;
        while (System.currentTimeMillis() < deadline)
        {
            if(context.getPrimaryTypeHandler("com.FooTest").get() != null)
                return;
            display.readAndDispatch();
            Thread.sleep(25);
        }
    }

    /**
     * The EditorUI-based constructor is package-private, which is not
     * accessible from another bundle at runtime, hence the reflection.
     */
    private CreateTestMethodActionExecutor newExecutor()
    {
        try
        {
            final var constructor = CreateTestMethodActionExecutor.class.getDeclaredConstructor(EditorUI.class, org.moreunit.preferences.Preferences.class);
            constructor.setAccessible(true);
            return constructor.newInstance(editorUI, org.moreunit.preferences.Preferences.getInstance());
        }
        catch (final ReflectiveOperationException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void await(int expectedOpenCount) throws Exception
    {
        final Display display = Display.getDefault();
        final long deadline = System.currentTimeMillis() + 30_000;
        while (openedMethods.size() < expectedOpenCount && System.currentTimeMillis() < deadline)
        {
            // the executor runs its final step through Display#syncExec, so the
            // events have to be dispatched, even from the UI thread
            display.readAndDispatch();
            Thread.sleep(10);
        }
        assertEquals(expectedOpenCount, openedMethods.size(), "EditorUI.open was not called as often as expected");
    }

    private IEditorPart editorOver(ICompilationUnit compilationUnit, ISourceRange selectionRange)
    {
        final IEditorPart editorPart = mock(IEditorPart.class);
        final IEditorInput editorInput = mock(IEditorInput.class);
        final IWorkbenchPartSite site = mock(IWorkbenchPartSite.class);
        final ISelectionProvider selectionProvider = mock(ISelectionProvider.class);

        when(editorPart.getEditorInput()).thenReturn(editorInput);
        when(editorInput.getAdapter(IFile.class)).thenReturn((IFile) compilationUnit.getResource());
        when(editorPart.getSite()).thenReturn(site);
        when(site.getSelectionProvider()).thenReturn(selectionProvider);
        when(selectionProvider.getSelection()).thenReturn(new TextSelection(selectionRange.getOffset(), selectionRange.getLength()));
        org.mockito.Mockito.doAnswer(invocation -> {
            selectionCount.incrementAndGet();
            return null;
        }).when(selectionProvider).setSelection(any());

        return editorPart;
    }

    @Test
    public void executeCreateTestMethodAction_should_create_test_method_in_corresponding_test_case() throws Exception
    {
        final IMethod foo = context.getPrimaryTypeHandler("com.Foo").addMethod("public int foo()", "return 0;").get();
        final int testMethodCountBefore = testMethodCount();

        final CreateTestMethodActionExecutor executor = newExecutor();
        executor.executeCreateTestMethodAction(editorOver(context.getCompilationUnit("com.Foo"), foo.getNameRange()));

        await(1);
        final IMethod createdMethod = openedMethods.get(0);
        assertEquals("FooTest", createdMethod.getDeclaringType().getElementName());
        assertEquals(testMethodCountBefore + 1, testMethodCount());
    }

    @Test
    public void executeCreateTestMethodAction_should_report_existing_test_method() throws Exception
    {
        final IMethod foo = context.getPrimaryTypeHandler("com.Foo").addMethod("public int foo()", "return 0;").get();

        final CreateTestMethodActionExecutor executor = newExecutor();

        // first call creates the test method...
        executor.executeCreateTestMethodAction(editorOver(context.getCompilationUnit("com.Foo"), foo.getNameRange()));
        await(1);
        final IMethod createdMethod = openedMethods.get(0);

        // ... second call finds it already existing and simply opens it
        executor.executeCreateTestMethodAction(editorOver(context.getCompilationUnit("com.Foo"), foo.getNameRange()));
        await(2);

        assertEquals(createdMethod, openedMethods.get(1));
    }

    @Test
    public void executeCreateTestMethodAction_should_create_test_method_when_edited_unit_is_a_test_case() throws Exception
    {
        final IMethod helper = context.getPrimaryTypeHandler("com.FooTest").addMethod("public void helper()", "").get();
        final int testMethodCountBefore = testMethodCount();

        final CreateTestMethodActionExecutor executor = newExecutor();
        executor.executeCreateTestMethodAction(editorOver(context.getCompilationUnit("com.FooTest"), helper.getNameRange()));

        await(1);
        assertEquals(testMethodCountBefore + 1, testMethodCount());
    }

    @Test
    public void executeCreateTestMethodAction_should_select_method_suffix_in_created_method() throws Exception
    {
        // the created test method keeps the source method name for JUnit 4, so
        // a name ending in "Suffix" triggers the suffix selection logic
        final IMethod fooSuffix = context.getPrimaryTypeHandler("com.Foo").addMethod("public int fooSuffix()", "return 0;").get();

        final IEditorPart editorPart = editorOver(context.getCompilationUnit("com.Foo"), fooSuffix.getNameRange());
        final CreateTestMethodActionExecutor executor = newExecutor();
        executor.executeCreateTestMethodAction(editorPart);

        await(1);
        assertTrue(selectionCount.get() >= 1, "the method suffix should have been selected");
        assertEquals(1, revealedElements.size());
        assertEquals(editorPart, revealedElements.get(0)[0]);
        assertEquals(openedMethods.get(0), revealedElements.get(0)[1]);
    }

    private int testMethodCount() throws Exception
    {
        return context.getPrimaryTypeHandler("com.FooTest").get().getMethods().length;
    }
}
