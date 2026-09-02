package org.moreunit.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.TestmethodCreator;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.ui.EditorUI;

@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "com:Foo", testCls = "com:FooTest")
public class CreateTestMethodHierarchyActionTest extends ContextTestCase
{
    private final List<Object> openedElements = new CopyOnWriteArrayList<>();
    private EditorUI editorUI;

    @BeforeEach
    public void setUp() throws Exception
    {
        TestmethodCreator.discardExtensions = true;
        openedElements.clear();

        awaitTestTypeAvailable();
        editorUI = mock(EditorUI.class);
        when(editorUI.open(any(IJavaElement.class))).thenAnswer(invocation -> {
            openedElements.add(invocation.getArgument(0));
            return null;
        });
    }

    /**
     * The test project is created asynchronously with respect to the JDT
     * model: this helper waits until the test class type is resolvable.
     */
    private void awaitTestTypeAvailable() throws Exception
    {
        org.eclipse.swt.widgets.Display display = org.eclipse.swt.widgets.Display.getDefault();
        long deadline = System.currentTimeMillis() + 20_000;
        while (System.currentTimeMillis() < deadline)
        {
            if(context.getPrimaryTypeHandler("com.FooTest").get() != null)
                return;
            display.readAndDispatch();
            Thread.sleep(25);
        }
    }

    /**
     * JDT search results depend on the (asynchronous) index state: this helper
     * polls until the corresponding test case of the given compilation unit is
     * found.
     */
    private void awaitTestSearchReady(java.util.function.Supplier<java.util.Collection< ? >> search) throws Exception
    {
        org.eclipse.swt.widgets.Display display = org.eclipse.swt.widgets.Display.getDefault();
        long deadline = System.currentTimeMillis() + 20_000;
        while (System.currentTimeMillis() < deadline)
        {
            if(! search.get().isEmpty())
                return;
            display.readAndDispatch();
            Thread.sleep(25);
        }
    }

    private void await(int expectedOpenCount) throws Exception
    {
        Display display = Display.getDefault();
        long deadline = System.currentTimeMillis() + 30_000;
        while (openedElements.size() < expectedOpenCount && System.currentTimeMillis() < deadline)
        {
            // the action runs its final step through Display#syncExec, so the
            // events have to be dispatched, even from the UI thread
            display.readAndDispatch();
            Thread.sleep(10);
        }
        assertEquals(expectedOpenCount, openedElements.size(), "EditorUI.open was not called as often as expected");
    }

    @Test
    public void should_accept_selection_and_ignore_non_structured_selection()
    {
        CreateTestMethodHierarchyAction action = new CreateTestMethodHierarchyAction();

        ISelection nonStructured = mock(ISelection.class);
        action.selectionChanged(null, nonStructured);
        // run with non-structured selection should do nothing (no exception)
        action.run(mock(IAction.class));
    }

    @Test
    public void should_ignore_structured_selection_not_containing_a_method()
    {
        CreateTestMethodHierarchyAction action = new CreateTestMethodHierarchyAction();
        action.selectionChanged(null, new StructuredSelection(new Object()));

        action.run(mock(IAction.class));

        assertEquals(0, openedElements.size());
    }

    @Test
    public void run_should_create_test_method_for_selected_method_of_class_under_test() throws Exception
    {
        MethodHandler foo = context.getPrimaryTypeHandler("com.Foo").addMethod("public int foo()", "return 0;");
        int testMethodCountBefore = testMethodCount();

        awaitTestSearchReady(() -> new org.moreunit.elements.ClassTypeFacade(context.getCompilationUnit("com.Foo")).getCorrespondingTestCases());

        CreateTestMethodHierarchyAction action = new CreateTestMethodHierarchyAction(editorUI);
        action.selectionChanged(null, new StructuredSelection(foo.get()));
        action.run(mock(IAction.class));

        await(1);
        assertEquals(testMethodCountBefore + 1, testMethodCount());
    }

    @Test
    public void run_should_create_test_method_when_selected_method_belongs_to_a_test_case() throws Exception
    {
        MethodHandler testFoo = context.getPrimaryTypeHandler("com.FooTest").addMethod("public void testFoo()", "");
        int testMethodCountBefore = testMethodCount();

        awaitTestSearchReady(() -> new org.moreunit.elements.TestCaseTypeFacade(context.getCompilationUnit("com.FooTest")).getCorrespondingClasses(false));

        CreateTestMethodHierarchyAction action = new CreateTestMethodHierarchyAction(editorUI);
        action.selectionChanged(null, new StructuredSelection(testFoo.get()));
        action.run(mock(IAction.class));

        await(1);
        assertEquals(testMethodCountBefore + 1, testMethodCount());
    }

    private int testMethodCount() throws Exception
    {
        return context.getPrimaryTypeHandler("com.FooTest").get().getMethods().length;
    }
}
