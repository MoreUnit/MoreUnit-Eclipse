package org.moreunit.elements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.moreunit.preferences.PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.junit.jupiter.api.Test;
import org.moreunit.core.util.StringConstants;
import org.moreunit.elements.TestmethodCreator.TestMethodCreationSettings;
import org.moreunit.log.LogHandler;

public class TestmethodCreatorBranchesCoverageTest
{
    private static TestmethodCreator creatorOverMocks(boolean generateComments) throws Exception
    {
        return creatorFor(mock(ICompilationUnit.class), generateComments);
    }

    private static TestmethodCreator creatorFor(ICompilationUnit compilationUnit, boolean generateComments) throws Exception
    {
        return new TestmethodCreator(new TestMethodCreationSettings().compilationUnit(compilationUnit).testType(TEST_TYPE_VALUE_JUNIT_3).generateComments(generateComments).defaultTestMethodContent("// test"));
    }

    private static IType typeThrowingOnMethods() throws Exception
    {
        final IType type = mock(IType.class);
        when(type.getMethods()).thenThrow(new JavaModelException(new RuntimeException("boom"), 1));
        return type;
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception
    {
        final Field field = TestmethodCreator.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static Object callPrivate(Object target, String methodName, Class< ? >[] parameterTypes, Object[] args) throws Exception
    {
        final Method method = TestmethodCreator.class.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        try
        {
            return method.invoke(target, args);
        }
        catch (final InvocationTargetException e)
        {
            throw new AssertionError("unexpected invocation failure", e.getCause());
        }
    }

    @SuppressWarnings("unchecked")
    private static List<IMethod> callGetOverloadedMethods(TestmethodCreator creator) throws Exception
    {
        return (List<IMethod>) callPrivate(creator, "getOverloadedMethods", new Class< ? >[0], new Object[0]);
    }

    private static void giveSilentFormatter(TestmethodCreator creator) throws Exception
    {
        final CodeFormatter formatter = mock(CodeFormatter.class);
        final TextEdit edit = mock(TextEdit.class);
        when(formatter.format(anyInt(), anyString(), anyInt(), anyInt(), anyInt(), anyString())).thenReturn(edit);
        setPrivateField(creator, "testFormatter", formatter);
    }

    @Test
    public void should_return_empty_overloaded_list_when_methods_cannot_be_read() throws Exception
    {
        // given a compilation unit whose methods cannot be read
        final ICompilationUnit compilationUnit = mock(ICompilationUnit.class);
        final IType unreadableType = typeThrowingOnMethods();
        when(compilationUnit.findPrimaryType()).thenReturn(unreadableType);
        final TestmethodCreator creator = creatorFor(compilationUnit, false);

        // when
        final List<IMethod> result = callGetOverloadedMethods(creator);

        // then no overloaded method is reported
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void should_return_empty_comment_when_javadoc_cannot_be_read() throws Exception
    {
        // given comments are requested but the javadoc cannot be read
        final TestmethodCreator creator = creatorOverMocks(true);
        final IMethod method = mock(IMethod.class);
        when(method.getJavadocRange()).thenThrow(new JavaModelException(new RuntimeException("boom"), 1));

        // when
        final String result = (String) callPrivate(creator, "getComments", new Class< ? >[] { IMethod.class }, new Object[] { method });

        // then
        assertEquals("", result);
    }

    @Test
    public void should_return_empty_comment_when_comments_are_not_requested() throws Exception
    {
        final TestmethodCreator creator = creatorOverMocks(false);

        final String result = (String) callPrivate(creator, "getComments", new Class< ? >[] { IMethod.class }, new Object[] { mock(IMethod.class) });

        assertEquals("", result);
    }

    @Test
    public void should_return_null_sibling_when_methods_cannot_be_read() throws Exception
    {
        // given a test case whose methods cannot be read
        final TestmethodCreator creator = creatorOverMocks(false);
        final ICompilationUnit testCaseUnit = mock(ICompilationUnit.class);
        final IType unreadableTestCaseType = typeThrowingOnMethods();
        when(testCaseUnit.findPrimaryType()).thenReturn(unreadableTestCaseType);
        setPrivateField(creator, "testCaseCompilationUnit", testCaseUnit);

        try (var logs = mockStatic(LogHandler.class))
        {
            final LogHandler mockLog = mock(LogHandler.class);
            logs.when(LogHandler::getInstance).thenReturn(mockLog);

            // when
            final Object result = callPrivate(creator, "getSiblingForInsert", new Class< ? >[] { IMethod.class }, new Object[] { mock(IMethod.class) });

            // then
            assertNull(result);
            verify(mockLog).handleExceptionLog(any(JavaModelException.class));
        }
    }

    @Test
    public void should_use_default_line_separator_when_it_cannot_be_read() throws Exception
    {
        // given a test case whose line separator cannot be read
        final TestmethodCreator creator = creatorOverMocks(false);
        final ICompilationUnit testCaseUnit = mock(ICompilationUnit.class);
        when(testCaseUnit.findRecommendedLineSeparator()).thenThrow(new JavaModelException(new RuntimeException("boom"), 1));
        setPrivateField(creator, "testCaseCompilationUnit", testCaseUnit);

        try (var logs = mockStatic(LogHandler.class))
        {
            final LogHandler mockLog = mock(LogHandler.class);
            logs.when(LogHandler::getInstance).thenReturn(mockLog);

            // when
            final String result = (String) callPrivate(creator, "findRecommendedLineSeparator", new Class< ? >[0], new Object[0]);

            // then the default separator is used
            assertEquals(StringConstants.NEWLINE, result);
            verify(mockLog).handleExceptionLog(any(JavaModelException.class));
        }
    }

    @Test
    public void should_return_null_when_method_already_exists() throws Exception
    {
        // given a test case that already contains the method
        final TestmethodCreator creator = creatorOverMocks(false);
        final IMethod existing = mock(IMethod.class);
        when(existing.getElementName()).thenReturn("existing");
        final IType testCaseType = mock(IType.class);
        when(testCaseType.getMethods()).thenReturn(new IMethod[] { existing });
        final ICompilationUnit testCaseUnit = mock(ICompilationUnit.class);
        when(testCaseUnit.findPrimaryType()).thenReturn(testCaseType);
        setPrivateField(creator, "testCaseCompilationUnit", testCaseUnit);

        // when
        final Object result = callPrivate(creator, "createMethod", new Class< ? >[] { String.class, String.class, IMethod.class }, new Object[] { "existing", "public void existing() {}", null });

        // then nothing is created
        assertNull(result);
    }

    @Test
    public void should_log_when_test_method_creation_fails_with_java_model_exception() throws Exception
    {
        // given a test case that fails on method creation
        final TestmethodCreator creator = creatorOverMocks(false);
        final IType testCaseType = mock(IType.class);
        when(testCaseType.getMethods()).thenReturn(new IMethod[0]);
        when(testCaseType.createMethod(anyString(), any(), anyBoolean(), any())).thenThrow(new JavaModelException(new RuntimeException("boom"), 1));
        final ICompilationUnit testCaseUnit = mock(ICompilationUnit.class);
        when(testCaseUnit.findPrimaryType()).thenReturn(testCaseType);
        setPrivateField(creator, "testCaseCompilationUnit", testCaseUnit);
        giveSilentFormatter(creator);

        try (var logs = mockStatic(LogHandler.class))
        {
            final LogHandler mockLog = mock(LogHandler.class);
            logs.when(LogHandler::getInstance).thenReturn(mockLog);

            // when
            final Object result = callPrivate(creator, "createMethod", new Class< ? >[] { String.class, String.class, IMethod.class }, new Object[] { "fresh", "public void fresh() {}", null });

            // then
            assertNull(result);
            verify(mockLog).handleExceptionLog(any(JavaModelException.class));
        }
    }

    @Test
    public void should_log_when_test_method_creation_fails_with_malformed_tree() throws Exception
    {
        // given a test case that fails on method creation
        final TestmethodCreator creator = creatorOverMocks(false);
        final IType testCaseType = mock(IType.class);
        when(testCaseType.getMethods()).thenReturn(new IMethod[0]);
        when(testCaseType.createMethod(anyString(), any(), anyBoolean(), any())).thenThrow(mock(MalformedTreeException.class));
        final ICompilationUnit testCaseUnit = mock(ICompilationUnit.class);
        when(testCaseUnit.findPrimaryType()).thenReturn(testCaseType);
        setPrivateField(creator, "testCaseCompilationUnit", testCaseUnit);
        giveSilentFormatter(creator);

        try (var logs = mockStatic(LogHandler.class))
        {
            final LogHandler mockLog = mock(LogHandler.class);
            logs.when(LogHandler::getInstance).thenReturn(mockLog);

            // when
            final Object result = callPrivate(creator, "createMethod", new Class< ? >[] { String.class, String.class, IMethod.class }, new Object[] { "fresh", "public void fresh() {}", null });

            // then
            assertNull(result);
            verify(mockLog).handleExceptionLog(any(MalformedTreeException.class));
        }
    }

    @Test
    public void should_log_when_test_method_creation_fails_with_bad_location() throws Exception
    {
        // given a formatter whose edit cannot be applied
        final TestmethodCreator creator = creatorOverMocks(false);
        final IType testCaseType = mock(IType.class);
        when(testCaseType.getMethods()).thenReturn(new IMethod[0]);
        final ICompilationUnit testCaseUnit = mock(ICompilationUnit.class);
        when(testCaseUnit.findPrimaryType()).thenReturn(testCaseType);
        setPrivateField(creator, "testCaseCompilationUnit", testCaseUnit);

        final CodeFormatter formatter = mock(CodeFormatter.class);
        final TextEdit edit = mock(TextEdit.class);
        doThrow(new BadLocationException()).when(edit).apply(any(IDocument.class));
        when(formatter.format(anyInt(), anyString(), anyInt(), anyInt(), anyInt(), anyString())).thenReturn(edit);
        setPrivateField(creator, "testFormatter", formatter);

        try (var logs = mockStatic(LogHandler.class))
        {
            final LogHandler mockLog = mock(LogHandler.class);
            logs.when(LogHandler::getInstance).thenReturn(mockLog);

            // when
            final Object result = callPrivate(creator, "createMethod", new Class< ? >[] { String.class, String.class, IMethod.class }, new Object[] { "fresh", "public void fresh() {}", null });

            // then
            assertNull(result);
            verify(mockLog).handleExceptionLog(any(BadLocationException.class));
        }
    }

    @Test
    public void should_return_null_when_test_method_lookup_fails() throws Exception
    {
        // given a test case whose methods cannot be read
        final TestmethodCreator creator = creatorOverMocks(false);
        final ICompilationUnit testCaseUnit = mock(ICompilationUnit.class);
        final IType unreadableTestCaseType = typeThrowingOnMethods();
        when(testCaseUnit.findPrimaryType()).thenReturn(unreadableTestCaseType);
        setPrivateField(creator, "testCaseCompilationUnit", testCaseUnit);

        try (var logs = mockStatic(LogHandler.class))
        {
            final LogHandler mockLog = mock(LogHandler.class);
            logs.when(LogHandler::getInstance).thenReturn(mockLog);

            // when (protected method across bundles: call reflectively)
            final IMethod result = (IMethod) callPrivate(creator, "findTestMethod", new Class< ? >[] { String.class }, new Object[] { "anything" });

            // then
            assertNull(result);
            verify(mockLog).handleExceptionLog(any(JavaModelException.class));
        }
    }
}
