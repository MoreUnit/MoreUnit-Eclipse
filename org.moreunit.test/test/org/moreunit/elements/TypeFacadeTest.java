package org.moreunit.elements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.jdt.core.IMember;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.support.DialogHelper;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;

public class TypeFacadeTest extends ContextTestCase
{

    @Project(mainCls="Hello")
    @Preferences(testClassNameTemplate="${srcFile}Test")
    @Test
    public void isTestCase_should_return_false_when_regular_class() throws CoreException
    {
        assertFalse(TypeFacade.isTestCase(context.getCompilationUnit("Hello")));
    }

    @Project(mainCls="HelloTest")
    @Preferences(testClassNameTemplate="${srcFile}Test")
    @Test
    public void isTestCase_should_return_true_when_class_has_test_suffix() throws JavaModelException
    {
        assertTrue(TypeFacade.isTestCase(context.getCompilationUnit("HelloTest")));
    }

    @Project(mainCls="TestHello")
    @Preferences(testClassNameTemplate="Test${srcFile}")
    @Test
    public void isTestCase_should_return_true_when_class_has_test_prefix() throws JavaModelException
    {
        assertTrue(TypeFacade.isTestCase(context.getCompilationUnit("TestHello")));
    }

    @Project(mainCls="Hello")
    @Preferences(testClassNameTemplate="${srcFile}Test")
    @Test
    public void isTestCase_should_return_false_when_type_is_null() throws CoreException
    {
        assertFalse(TypeFacade.isTestCase((IType) null));
    }

    @Project(mainCls="Hello")
    @Preferences(testClassNameTemplate="${srcFile}Test")
    @Test
    public void getType_should_return_primary_type_of_compilation_unit() throws CoreException
    {
        final ICompilationUnit compilationUnit = context.getCompilationUnit("Hello");

        final ClassTypeFacade facade = new ClassTypeFacade(compilationUnit);

        assertEquals(compilationUnit, facade.getCompilationUnit());
        assertEquals(compilationUnit.findPrimaryType(), facade.getType());
    }

    @Project(mainCls="Hello")
    @Preferences(testClassNameTemplate="${srcFile}Test")
    @Test
    public void facade_should_be_creatable_from_editor_part_showing_the_compilation_unit() throws CoreException
    {
        final ICompilationUnit compilationUnit = context.getCompilationUnit("Hello");

        final IEditorInput editorInput = mock(IEditorInput.class);
        when(editorInput.getAdapter(IFile.class)).thenReturn((IFile) compilationUnit.getResource());
        final IEditorPart editorPart = mock(IEditorPart.class);
        when(editorPart.getEditorInput()).thenReturn(editorInput);

        final TypeFacade facade = new ClassTypeFacade(editorPart);

        assertEquals(compilationUnit, facade.getCompilationUnit());
    }

    @Project(mainCls = "com:Foo", testCls = "org:FooTest")
    @Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
    @Test
    public void getOneCorrespondingMember_should_open_dialog_with_likely_matches_when_no_perfect_match_exists() throws Exception
    {
        // the test class lives in another package: it is a likely, not a perfect, match
        final Display display = Display.getDefault();
        final java.util.Set<Shell> knownShells = DialogHelper.knownShells(display);
        display.asyncExec(DialogHelper.closerUntilHandled(display, knownShells, shell -> DialogHelper.confirmItem(shell, "FooTest"), 2000));

        final ClassTypeFacade facade = new ClassTypeFacade(context.getCompilationUnit("com.Foo"));

        final CorrespondingMemberRequest request = CorrespondingMemberRequest.newCorrespondingMemberRequest() //
                .withExpectedResultType(CorrespondingMemberRequest.MemberType.TYPE_OR_METHOD) //
                .build();

        final IMember member = facade.getOneCorrespondingMember(request);

        assertEquals(context.getPrimaryTypeHandler("org.FooTest").get(), member);
    }
}
