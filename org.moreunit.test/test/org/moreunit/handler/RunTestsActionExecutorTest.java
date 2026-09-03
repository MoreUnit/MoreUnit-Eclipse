package org.moreunit.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.moreunit.launch.TestLauncher;
import org.moreunit.test.support.DialogHelper;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.ui.TreeActionElement;
import org.moreunit.util.FeatureDetector;

@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "com:Foo", testCls = "com:FooTest")
public class RunTestsActionExecutorTest extends ContextTestCase
{
    private final AtomicBoolean launchCalled = new AtomicBoolean(false);
    private final AtomicReference<Collection< ? extends IJavaElement>> launchedMembers = new AtomicReference<>();
    private final AtomicReference<String> launchMode = new AtomicReference<>();

    @BeforeEach
    public void injectMockedTestLauncher() throws Exception
    {
        launchCalled.set(false);
        launchedMembers.set(null);
        launchMode.set(null);

        final TestLauncher testLauncher = mock(TestLauncher.class);
        doAnswer(invocation -> {
            launchedMembers.set(invocation.getArgument(1));
            launchMode.set(invocation.getArgument(2));
            launchCalled.set(true);
            return null;
        }).when(testLauncher).launch(anyString(), anyCollection(), anyString());

        setPrivateField("testLauncher", testLauncher);
    }

    private void setPrivateField(String fieldName, Object value) throws Exception
    {
        final Field field = RunTestsActionExecutor.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(RunTestsActionExecutor.getInstance(), value);
    }

    private void awaitLaunch() throws Exception
    {
        awaitLaunch(90_000);
    }

    private void awaitLaunch(long timeoutMillis) throws Exception
    {
        final Display display = Display.getDefault();
        final long deadline = System.currentTimeMillis() + timeoutMillis;
        while (! launchCalled.get() && System.currentTimeMillis() < deadline)
        {
            // the executor runs its final step through Display#syncExec, so the
            // events have to be dispatched, even from the UI thread
            display.readAndDispatch();
            Thread.sleep(10);
        }
        assertTrue(launchCalled.get(), "TestLauncher.launch was not called");
    }

    /**
     * Runs a dialog-driven interaction, waiting briefly for the launch after
     * each attempt. On a shared desktop the ChooseDialog can be dismissed by
     * an unrelated focus change before the test driver confirms the choice;
     * such an attempt launches nothing and is simply retried. A genuinely
     * broken interaction still fails loudly once attempts run out.
     */
    private void awaitLaunchWithRetry(Executable interaction) throws Throwable
    {
        Throwable lastFailure = null;
        for (int attempt = 1; attempt <= 3; attempt++)
        {
            launchCalled.set(false);
            launchedMembers.set(null);
            interaction.execute();
            try
            {
                awaitLaunch(20_000);
                return;
            }
            catch (final AssertionError e)
            {
                lastFailure = e;
            }
        }
        throw lastFailure;
    }

    private List<String> launchedElementNames()
    {
        return launchedMembers.get().stream().map(IJavaElement::getElementName).collect(Collectors.toList());
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

        return editorPart;
    }

    @Test
    public void getInstance_should_return_singleton()
    {
        assertSame(RunTestsActionExecutor.getInstance(), RunTestsActionExecutor.getInstance());
    }

    @Test
    public void executeRunTestAction_should_launch_all_corresponding_test_cases_of_class_under_test() throws Exception
    {
        RunTestsActionExecutor.getInstance().executeRunTestAction(context.getCompilationUnit("com.Foo"), ILaunchManager.RUN_MODE);

        awaitLaunch();
        assertEquals(Arrays.asList("FooTest"), launchedElementNames());
        assertEquals(ILaunchManager.RUN_MODE, launchMode.get());
    }

    @Test
    public void executeRunTestAction_should_launch_test_case_itself_when_edited_unit_is_a_test_case() throws Exception
    {
        RunTestsActionExecutor.getInstance().executeRunTestAction(context.getCompilationUnit("com.FooTest"), ILaunchManager.RUN_MODE);

        awaitLaunch();
        assertEquals(Arrays.asList("FooTest"), launchedElementNames());
    }

    @Test
    public void executeRunTestAction_should_use_one_corresponding_test_case_when_test_selection_run_is_not_supported() throws Exception
    {
        final FeatureDetector featureDetector = mock(FeatureDetector.class);
        when(featureDetector.isTestSelectionRunSupported(any())).thenReturn(false);
        setPrivateField("featureDetector", featureDetector);

        RunTestsActionExecutor.getInstance().executeRunTestAction(context.getCompilationUnit("com.Foo"), ILaunchManager.RUN_MODE);

        awaitLaunch();
        assertEquals(Arrays.asList("FooTest"), launchedElementNames());
    }

    @Test
    @Preferences(testClassNameTemplate = "${srcFile}*Test", testSrcFolder = "test")
    public void executeRunTestAction_should_replace_abstract_test_case_by_single_concrete_subclass() throws Exception
    {
        TypeHandlerAccess.createAbstractTest(context, "com.FooAbstractTest");
        TypeHandlerAccess.createSubclass(context, "com.FooAbstractTest", "com.FooAbstractTestImpl");

        RunTestsActionExecutor.getInstance().executeRunTestAction(context.getCompilationUnit("com.Foo"), ILaunchManager.RUN_MODE);

        awaitLaunch();
        final List<String> launchedNames = launchedElementNames();
        assertTrue(launchedNames.contains("FooTest"));
        assertTrue(launchedNames.contains("FooAbstractTestImpl"));
        assertFalse(launchedNames.contains("FooAbstractTest"));
    }

    @Test
    @Preferences(testClassNameTemplate = "${srcFile}*Test", testSrcFolder = "test")
    public void executeRunTestAction_should_keep_abstract_test_case_without_concrete_subclass() throws Exception
    {
        TypeHandlerAccess.createAbstractTest(context, "com.FooAbstractTest");

        RunTestsActionExecutor.getInstance().executeRunTestAction(context.getCompilationUnit("com.Foo"), ILaunchManager.RUN_MODE);

        awaitLaunch();
        final List<String> launchedNames = launchedElementNames();
        assertTrue(launchedNames.contains("FooTest"));
        assertTrue(launchedNames.contains("FooAbstractTest"));
    }

    @Test
    public void executeRunTestsOfSelectedMemberAction_should_launch_corresponding_test_methods() throws Exception
    {
        final IMethod foo = context.getPrimaryTypeHandler("com.Foo").addMethod("public int foo()", "return 0;").get();
        context.getPrimaryTypeHandler("com.FooTest").addMethod("public void foo()", "");

        final IEditorPart editorPart = editorOver(context.getCompilationUnit("com.Foo"), foo.getNameRange());
        RunTestsActionExecutor.getInstance().executeRunTestsOfSelectedMemberAction(editorPart, ILaunchManager.RUN_MODE);

        awaitLaunch();
        assertEquals(Arrays.asList("foo"), launchedElementNames());
    }

    @Test
    public void executeRunTestsOfSelectedMemberAction_should_launch_selected_test_method_when_edited_unit_is_a_test_case() throws Exception
    {
        final IMethod testFoo = context.getPrimaryTypeHandler("com.FooTest").addMethod("@Test\npublic void foo()", "").get();

        final IEditorPart editorPart = editorOver(context.getCompilationUnit("com.FooTest"), testFoo.getNameRange());
        RunTestsActionExecutor.getInstance().executeRunTestsOfSelectedMemberAction(editorPart, ILaunchManager.RUN_MODE);

        awaitLaunch();
        assertEquals(Arrays.asList("foo"), launchedElementNames());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void allSubclassesAction_should_expose_the_whole_collection_of_subclasses() throws Exception
    {
        final Class< ? > actionClass = Class.forName("org.moreunit.handler.RunTestsActionExecutor$AllSubclassesAction");
        final Constructor< ? > constructor = actionClass.getDeclaredConstructor(Collection.class);
        constructor.setAccessible(true);

        final List<IType> subclasses = new ArrayList<>();
        subclasses.add(mock(IType.class));
        subclasses.add(mock(IType.class));
        final TreeActionElement<Collection<IType>> action = (TreeActionElement<Collection<IType>>) constructor.newInstance(subclasses);

        assertTrue(action.provideElement());
        assertEquals("All concrete subclasses", action.getText());
        assertNull(action.getImage());
        assertEquals(2, action.execute().size());
    }

    /**
     * Small helpers keeping the abstract-test scenarios readable.
     */
    private static class TypeHandlerAccess
    {
        static void createAbstractTest(org.moreunit.test.context.TestContextRule testCase, String fullyQualifiedName) throws Exception
        {
            final int lastDot = fullyQualifiedName.lastIndexOf('.');
            final String packageName = fullyQualifiedName.substring(0, lastDot);
            final String typeName = fullyQualifiedName.substring(lastDot + 1);

            testCase.getProjectHandler().getTestSrcFolderHandler() //
            .createCompilationUnit(fullyQualifiedName, "package %s;\npublic abstract class %s {}\n".formatted(packageName, typeName));
        }

        static void createSubclass(org.moreunit.test.context.TestContextRule testCase, String superTypeQualifiedName, String subTypeQualifiedName) throws Exception
        {
            testCase.getPrimaryTypeHandler(superTypeQualifiedName).createSubclass(subTypeQualifiedName);
        }
    }

    @Test
    public void executeRunTestAction_should_launch_tests_when_called_with_an_editor_part() throws Exception
    {
        final IEditorPart editorPart = editorOver(context.getCompilationUnit("com.Foo"), new org.eclipse.jdt.core.SourceRange(0, 0));

        RunTestsActionExecutor.getInstance().executeRunTestAction(editorPart, ILaunchManager.RUN_MODE);

        awaitLaunch();
        assertEquals(Arrays.asList("FooTest"), launchedElementNames());
    }

    @Test
    @Preferences(testClassNameTemplate = "${srcFile}*Test", testSrcFolder = "test")
    public void executeRunTestAction_should_let_the_user_choose_a_concrete_subclass_of_an_abstract_test_case() throws Throwable
    {
        TypeHandlerAccess.createAbstractTest(context, "com.FooAbstractTest");
        TypeHandlerAccess.createSubclass(context, "com.FooAbstractTest", "com.FooAbstractTestImpl");
        TypeHandlerAccess.createSubclass(context, "com.FooAbstractTest", "com.FooAbstractTestImpl2");

        final Display display = Display.getDefault();

        awaitLaunchWithRetry(() -> {
            final java.util.Set<Shell> knownShells = DialogHelper.knownShells(display);
            DialogHelper.bringWorkbenchToFront(display);
            display.asyncExec(DialogHelper.closerUntilHandled(display, knownShells, //
                    shell -> DialogHelper.confirmItem(shell, "FooAbstractTestImpl"), 2000));

            RunTestsActionExecutor.getInstance().executeRunTestAction(context.getCompilationUnit("com.Foo"), ILaunchManager.RUN_MODE);
        });

        final List<String> launchedNames = launchedElementNames();
        assertTrue(launchedNames.contains("FooTest"));
        assertTrue(launchedNames.contains("FooAbstractTestImpl"), "chosen subclass should be launched: " + launchedNames);
        assertFalse(launchedNames.contains("FooAbstractTestImpl2"), "not chosen subclass should not be launched: " + launchedNames);
    }

    @Test
    @Preferences(testClassNameTemplate = "${srcFile}*Test", testSrcFolder = "test")
    public void executeRunTestsOfSelectedMemberAction_should_replace_abstract_test_method_by_chosen_subclass_method() throws Throwable
    {
        TypeHandlerAccess.createAbstractTest(context, "com.FooAbstractTest");
        context.getPrimaryTypeHandler("com.FooAbstractTest").addMethod("@Test\npublic void testFoo()", "");
        TypeHandlerAccess.createSubclass(context, "com.FooAbstractTest", "com.FooAbstractTestImpl");
        context.getPrimaryTypeHandler("com.FooAbstractTestImpl").addMethod("@Test\npublic void testFoo()", "");
        TypeHandlerAccess.createSubclass(context, "com.FooAbstractTest", "com.FooAbstractTestImpl2");

        final IMethod abstractTestMethod = context.getPrimaryTypeHandler("com.FooAbstractTest").get().getMethods()[0];
        final IEditorPart editorPart = editorOver(context.getCompilationUnit("com.FooAbstractTest"), abstractTestMethod.getNameRange());

        final Display display = Display.getDefault();
        awaitLaunchWithRetry(() -> {
            final java.util.Set<Shell> knownShells = DialogHelper.knownShells(display);
            DialogHelper.bringWorkbenchToFront(display);
            display.asyncExec(DialogHelper.closerUntilHandled(display, knownShells, //
                    shell -> DialogHelper.confirmItem(shell, "FooAbstractTestImpl"), 2000));

            RunTestsActionExecutor.getInstance().executeRunTestsOfSelectedMemberAction(editorPart, ILaunchManager.RUN_MODE);
        });

        final List<String> launchedNames = launchedElementNames();
        assertEquals(1, launchedNames.size());
        final IJavaElement launched = launchedMembers.get().iterator().next();
        assertEquals("testFoo", launched.getElementName());
        assertEquals("FooAbstractTestImpl", ((IMethod) launched).getDeclaringType().getElementName());
    }

    @Test
    @Preferences(testClassNameTemplate = "${srcFile}*Test", testSrcFolder = "test")
    public void executeRunTestsOfSelectedMemberAction_should_launch_all_concrete_subclasses_when_user_chooses_all() throws Throwable
    {
        TypeHandlerAccess.createAbstractTest(context, "com.FooAbstractTest");
        context.getPrimaryTypeHandler("com.FooAbstractTest").addMethod("@Test\npublic void testFoo()", "");
        TypeHandlerAccess.createSubclass(context, "com.FooAbstractTest", "com.FooAbstractTestImpl");
        context.getPrimaryTypeHandler("com.FooAbstractTestImpl").addMethod("@Test\npublic void testFoo()", "");
        TypeHandlerAccess.createSubclass(context, "com.FooAbstractTest", "com.FooAbstractTestImpl2");
        context.getPrimaryTypeHandler("com.FooAbstractTestImpl2").addMethod("@Test\npublic void testFoo()", "");

        final IMethod abstractTestMethod = context.getPrimaryTypeHandler("com.FooAbstractTest").get().getMethods()[0];
        final IEditorPart editorPart = editorOver(context.getCompilationUnit("com.FooAbstractTest"), abstractTestMethod.getNameRange());

        final Display display = Display.getDefault();
        awaitLaunchWithRetry(() -> {
            final java.util.Set<Shell> knownShells = DialogHelper.knownShells(display);
            DialogHelper.bringWorkbenchToFront(display);
            display.asyncExec(DialogHelper.closerUntilHandled(display, knownShells, //
                    shell -> DialogHelper.confirmItem(shell, "All concrete subclasses"), 2000));

            RunTestsActionExecutor.getInstance().executeRunTestsOfSelectedMemberAction(editorPart, ILaunchManager.RUN_MODE);
        });

        final java.util.Set<String> declaringTypes = launchedMembers.get().stream() //
                .map(e -> ((IMethod) e).getDeclaringType().getElementName()) //
                .collect(java.util.stream.Collectors.toSet());
        assertEquals(new java.util.HashSet<>(java.util.Arrays.asList("FooAbstractTestImpl", "FooAbstractTestImpl2")), declaringTypes);
    }
}
