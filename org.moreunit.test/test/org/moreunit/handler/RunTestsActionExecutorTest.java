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
import java.lang.reflect.Method;
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
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        // some tests replace the detector with a mock: always restore the default here
        setPrivateField("featureDetector", new FeatureDetector());
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
    public void executeRunTestAction_should_let_the_user_choose_a_concrete_subclass_of_an_abstract_test_case() throws Exception
    {
        TypeHandlerAccess.createAbstractTest(context, "com.FooAbstractTest");
        TypeHandlerAccess.createSubclass(context, "com.FooAbstractTest", "com.FooAbstractTestImpl");
        TypeHandlerAccess.createSubclass(context, "com.FooAbstractTest", "com.FooAbstractTestImpl2");

        final Display display = Display.getDefault();
        final java.util.Set<Shell> knownShells = DialogHelper.knownShells(display);
        display.asyncExec(DialogHelper.closerFor(display, knownShells, //
                shell -> DialogHelper.confirmItem(shell, "FooAbstractTestImpl"), 2000));

        RunTestsActionExecutor.getInstance().executeRunTestAction(context.getCompilationUnit("com.Foo"), ILaunchManager.RUN_MODE);

        awaitLaunch(90_000);
        final List<String> launchedNames = launchedElementNames();
        assertTrue(launchedNames.contains("FooTest"));
        assertTrue(launchedNames.contains("FooAbstractTestImpl"), "chosen subclass should be launched: " + launchedNames);
        assertFalse(launchedNames.contains("FooAbstractTestImpl2"), "not chosen subclass should not be launched: " + launchedNames);
    }

    @Test
    @Preferences(testClassNameTemplate = "${srcFile}*Test", testSrcFolder = "test")
    public void executeRunTestsOfSelectedMemberAction_should_replace_abstract_test_method_by_chosen_subclass_method() throws Exception
    {
        TypeHandlerAccess.createAbstractTest(context, "com.FooAbstractTest");
        context.getPrimaryTypeHandler("com.FooAbstractTest").addMethod("@Test\npublic void testFoo()", "");
        TypeHandlerAccess.createSubclass(context, "com.FooAbstractTest", "com.FooAbstractTestImpl");
        context.getPrimaryTypeHandler("com.FooAbstractTestImpl").addMethod("@Test\npublic void testFoo()", "");
        TypeHandlerAccess.createSubclass(context, "com.FooAbstractTest", "com.FooAbstractTestImpl2");

        final IMethod abstractTestMethod = context.getPrimaryTypeHandler("com.FooAbstractTest").get().getMethods()[0];
        final IEditorPart editorPart = editorOver(context.getCompilationUnit("com.FooAbstractTest"), abstractTestMethod.getNameRange());

        final Display display = Display.getDefault();
        final java.util.Set<Shell> knownShells = DialogHelper.knownShells(display);
        display.asyncExec(DialogHelper.closerFor(display, knownShells, //
                shell -> DialogHelper.confirmItem(shell, "FooAbstractTestImpl"), 2000));

        RunTestsActionExecutor.getInstance().executeRunTestsOfSelectedMemberAction(editorPart, ILaunchManager.RUN_MODE);

        awaitLaunch(90_000);
        final List<String> launchedNames = launchedElementNames();
        assertEquals(1, launchedNames.size());
        final IJavaElement launched = launchedMembers.get().iterator().next();
        assertEquals("testFoo", launched.getElementName());
        assertEquals("FooAbstractTestImpl", ((IMethod) launched).getDeclaringType().getElementName());
    }

    @Test
    @Preferences(testClassNameTemplate = "${srcFile}*Test", testSrcFolder = "test")
    public void executeRunTestsOfSelectedMemberAction_should_launch_all_concrete_subclasses_when_user_chooses_all() throws Exception
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
        final java.util.Set<Shell> knownShells = DialogHelper.knownShells(display);
        display.asyncExec(DialogHelper.closerFor(display, knownShells, //
                shell -> DialogHelper.confirmItem(shell, "All concrete subclasses"), 2000));

        RunTestsActionExecutor.getInstance().executeRunTestsOfSelectedMemberAction(editorPart, ILaunchManager.RUN_MODE);

        awaitLaunch(90_000);
        final java.util.Set<String> declaringTypes = launchedMembers.get().stream() //
                .map(e -> ((IMethod) e).getDeclaringType().getElementName()) //
                .collect(java.util.stream.Collectors.toSet());
        assertEquals(new java.util.HashSet<>(java.util.Arrays.asList("FooAbstractTestImpl", "FooAbstractTestImpl2")), declaringTypes);
    }

    @Test
    public void executeRunTestAction_should_launch_selected_type_itself_when_no_test_case_exists() throws Exception
    {
        context.getProjectHandler().getMainSrcFolderHandler().createClass("com.Orphan");

        RunTestsActionExecutor.getInstance().executeRunTestAction(context.getCompilationUnit("com.Orphan"), ILaunchManager.RUN_MODE);

        awaitLaunch();
        assertEquals(Arrays.asList("Orphan"), launchedElementNames());
    }

    @Test
    public void executeRunTestAction_should_save_dirty_editor_before_running_tests() throws Exception
    {
        final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        final IFile file = (IFile) context.getCompilationUnit("com.Foo").getResource();
        final IEditorPart editor = page.openEditor(new FileEditorInput(file), "org.eclipse.ui.DefaultTextEditor", true);
        try
        {
            assertTrue(editor instanceof ITextEditor);
            final ITextEditor textEditor = (ITextEditor) editor;
            textEditor.getDocumentProvider().getDocument(editor.getEditorInput()).set("package com;\npublic class Foo {\n}\n// dirty\n");
            assertTrue(editor.isDirty());

            RunTestsActionExecutor.getInstance().executeRunTestAction(context.getCompilationUnit("com.Foo"), ILaunchManager.RUN_MODE);

            awaitLaunch();
            assertFalse(editor.isDirty());
            assertEquals(Arrays.asList("FooTest"), launchedElementNames());
        }
        finally
        {
            page.closeAllEditors(false);
        }
    }

    @Test
    public void executeRunTestsOfSelectedMemberAction_should_launch_test_case_when_cursor_is_outside_any_method() throws Exception
    {
        final IEditorPart editorPart = editorOver(context.getCompilationUnit("com.Foo"), new org.eclipse.jdt.core.SourceRange(0, 0));
        RunTestsActionExecutor.getInstance().executeRunTestsOfSelectedMemberAction(editorPart, ILaunchManager.RUN_MODE);

        awaitLaunch();
        assertEquals(Arrays.asList("FooTest"), launchedElementNames());
    }

    @Test
    public void executeRunTestsOfSelectedMemberAction_should_launch_class_under_test_when_no_corresponding_test_method_is_found() throws Exception
    {
        final IMethod bar = context.getPrimaryTypeHandler("com.Foo").addMethod("public int bar()", "return 1;").get();

        final IEditorPart editorPart = editorOver(context.getCompilationUnit("com.Foo"), bar.getNameRange());
        RunTestsActionExecutor.getInstance().executeRunTestsOfSelectedMemberAction(editorPart, ILaunchManager.RUN_MODE);

        awaitLaunch();
        assertEquals(Arrays.asList("Foo"), launchedElementNames());
    }

    @Test
    public void executeRunTestsOfSelectedMemberAction_should_use_single_corresponding_member_when_test_selection_run_is_not_supported() throws Exception
    {
        final FeatureDetector featureDetector = mock(FeatureDetector.class);
        when(featureDetector.isTestSelectionRunSupported(any())).thenReturn(false);
        setPrivateField("featureDetector", featureDetector);

        context.getPrimaryTypeHandler("com.Foo").addMethod("public int foo()", "return 0;");
        context.getPrimaryTypeHandler("com.FooTest").addMethod("public void foo()", "");
        final IMethod foo = context.getPrimaryTypeHandler("com.Foo").get().getMethods()[0];

        final IEditorPart editorPart = editorOver(context.getCompilationUnit("com.Foo"), foo.getNameRange());
        RunTestsActionExecutor.getInstance().executeRunTestsOfSelectedMemberAction(editorPart, ILaunchManager.RUN_MODE);

        awaitLaunch();
        assertEquals(Arrays.asList("foo"), launchedElementNames());
    }

    @Test
    @Preferences(testClassNameTemplate = "${srcFile}*Test", testSrcFolder = "test")
    public void executeRunTestsOfSelectedMemberAction_should_keep_abstract_test_method_without_concrete_subclass() throws Exception
    {
        TypeHandlerAccess.createAbstractTest(context, "com.FooAbstractTest");
        context.getPrimaryTypeHandler("com.FooAbstractTest").addMethod("@Test\npublic void testFoo()", "");

        final IMethod abstractTestMethod = context.getPrimaryTypeHandler("com.FooAbstractTest").get().getMethods()[0];
        final IEditorPart editorPart = editorOver(context.getCompilationUnit("com.FooAbstractTest"), abstractTestMethod.getNameRange());
        RunTestsActionExecutor.getInstance().executeRunTestsOfSelectedMemberAction(editorPart, ILaunchManager.RUN_MODE);

        awaitLaunch();
        assertEquals(Arrays.asList("testFoo"), launchedElementNames());
        final IJavaElement launched = launchedMembers.get().iterator().next();
        assertEquals("FooAbstractTest", ((IMethod) launched).getDeclaringType().getElementName());
    }

    @Test
    @Preferences(testClassNameTemplate = "${srcFile}*Test", testSrcFolder = "test")
    public void executeRunTestsOfSelectedMemberAction_should_replace_abstract_test_type_by_single_concrete_subclass() throws Exception
    {
        TypeHandlerAccess.createAbstractTest(context, "com.FooAbstractTest");
        TypeHandlerAccess.createSubclass(context, "com.FooAbstractTest", "com.FooAbstractTestImpl");

        final IEditorPart editorPart = editorOver(context.getCompilationUnit("com.FooAbstractTest"), new org.eclipse.jdt.core.SourceRange(0, 0));
        RunTestsActionExecutor.getInstance().executeRunTestsOfSelectedMemberAction(editorPart, ILaunchManager.RUN_MODE);

        awaitLaunch();
        assertEquals(Arrays.asList("FooAbstractTestImpl"), launchedElementNames());
    }

    @Test
    @Preferences(testClassNameTemplate = "${srcFile}*Test", testSrcFolder = "test")
    public void executeRunTestsOfSelectedMemberAction_should_replace_abstract_test_method_by_single_concrete_subclass_method() throws Exception
    {
        TypeHandlerAccess.createAbstractTest(context, "com.FooAbstractTest");
        context.getPrimaryTypeHandler("com.FooAbstractTest").addMethod("@Test\npublic void testFoo()", "");
        TypeHandlerAccess.createSubclass(context, "com.FooAbstractTest", "com.FooAbstractTestImpl");
        context.getPrimaryTypeHandler("com.FooAbstractTestImpl").addMethod("@Test\npublic void testFoo()", "");

        final IMethod abstractTestMethod = context.getPrimaryTypeHandler("com.FooAbstractTest").get().getMethods()[0];
        final IEditorPart editorPart = editorOver(context.getCompilationUnit("com.FooAbstractTest"), abstractTestMethod.getNameRange());
        RunTestsActionExecutor.getInstance().executeRunTestsOfSelectedMemberAction(editorPart, ILaunchManager.RUN_MODE);

        awaitLaunch();
        assertEquals(Arrays.asList("testFoo"), launchedElementNames());
        final IJavaElement launched = launchedMembers.get().iterator().next();
        assertEquals("FooAbstractTestImpl", ((IMethod) launched).getDeclaringType().getElementName());
    }

    @Test
    @Preferences(testClassNameTemplate = "${srcFile}*Test", testSrcFolder = "test")
    public void executeRunTestsOfSelectedMemberAction_should_keep_abstract_test_method_when_subclass_does_not_declare_it() throws Exception
    {
        TypeHandlerAccess.createAbstractTest(context, "com.FooAbstractTest");
        context.getPrimaryTypeHandler("com.FooAbstractTest").addMethod("@Test\npublic void testFoo()", "");
        TypeHandlerAccess.createSubclass(context, "com.FooAbstractTest", "com.FooAbstractTestImpl");

        final IMethod abstractTestMethod = context.getPrimaryTypeHandler("com.FooAbstractTest").get().getMethods()[0];
        final IEditorPart editorPart = editorOver(context.getCompilationUnit("com.FooAbstractTest"), abstractTestMethod.getNameRange());
        RunTestsActionExecutor.getInstance().executeRunTestsOfSelectedMemberAction(editorPart, ILaunchManager.RUN_MODE);

        awaitLaunch();
        assertEquals(Arrays.asList("testFoo"), launchedElementNames());
        final IJavaElement launched = launchedMembers.get().iterator().next();
        assertEquals("FooAbstractTest", ((IMethod) launched).getDeclaringType().getElementName());
    }

    @Test
    @Preferences(testClassNameTemplate = "${srcFile}*Test", testSrcFolder = "test")
    public void executeRunTestAction_should_keep_abstract_test_case_when_subclass_choice_is_cancelled() throws Exception
    {
        TypeHandlerAccess.createAbstractTest(context, "com.FooAbstractTest");
        TypeHandlerAccess.createSubclass(context, "com.FooAbstractTest", "com.FooAbstractTestImpl");
        TypeHandlerAccess.createSubclass(context, "com.FooAbstractTest", "com.FooAbstractTestImpl2");

        final Display display = Display.getDefault();
        final java.util.Set<Shell> knownShells = DialogHelper.knownShells(display);
        display.asyncExec(DialogHelper.closerFor(display, knownShells, Shell::close, 2000));

        RunTestsActionExecutor.getInstance().executeRunTestAction(context.getCompilationUnit("com.Foo"), ILaunchManager.RUN_MODE);

        awaitLaunch(90_000);
        final List<String> launchedNames = launchedElementNames();
        assertTrue(launchedNames.contains("FooTest"));
        assertTrue(launchedNames.contains("FooAbstractTest"), "cancelled choice should keep the abstract case: " + launchedNames);
        assertFalse(launchedNames.contains("FooAbstractTestImpl"), "cancelled choice should launch no subclass: " + launchedNames);
        assertFalse(launchedNames.contains("FooAbstractTestImpl2"), "cancelled choice should launch no subclass: " + launchedNames);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void resolveAbstractTestCase_should_return_original_type_when_flags_cannot_be_read() throws Exception
    {
        final IType testCase = mock(IType.class);
        when(testCase.getFlags()).thenThrow(mock(JavaModelException.class));

        final Method method = RunTestsActionExecutor.class.getDeclaredMethod("resolveAbstractTestCase", IType.class);
        method.setAccessible(true);
        final Collection<IType> resolved = (Collection<IType>) method.invoke(RunTestsActionExecutor.getInstance(), testCase);

        assertEquals(1, resolved.size());
        assertSame(testCase, resolved.iterator().next());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void resolveAbstractTestElement_should_return_original_element_when_flags_cannot_be_read() throws Exception
    {
        final IType testElement = mock(IType.class);
        when(testElement.getFlags()).thenThrow(mock(JavaModelException.class));

        final Method method = RunTestsActionExecutor.class.getDeclaredMethod("resolveAbstractTestElement", IMember.class);
        method.setAccessible(true);
        final Collection<IMember> resolved = (Collection<IMember>) method.invoke(RunTestsActionExecutor.getInstance(), testElement);

        assertEquals(1, resolved.size());
        assertSame(testElement, resolved.iterator().next());
    }
}
