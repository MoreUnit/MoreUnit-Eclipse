package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.templates.MockingContext;

public class PartTest
{
    private IType testCaseType;
    private MockingContext context;

    @BeforeEach
    public void createMockingContext() throws Exception
    {
        final ICompilationUnit testCaseCompilationUnit = mock(ICompilationUnit.class);
        testCaseType = mock(IType.class);
        when(testCaseCompilationUnit.findPrimaryType()).thenReturn(testCaseType);

        context = new MockingContext(new Dependencies(null, null, null), mock(IType.class), testCaseCompilationUnit, "junit4",
                                     Collections.emptyList());
    }

    @Test
    public void should_insert_test_class_annotation_after_javadoc_when_javadoc_exists() throws Exception
    {
        // given
        final ISourceRange javadocRange = range(10, 5);
        when(testCaseType.getJavadocRange()).thenReturn(javadocRange);

        // when
        assertEquals(16, Part.TEST_CLASS_ANNOTATION.getInsertionOffset(context));
    }

    @Test
    public void should_insert_test_class_annotation_at_type_start_when_javadoc_does_not_exist() throws Exception
    {
        // given
        when(testCaseType.getJavadocRange()).thenReturn(null);
        final ISourceRange typeRange = range(7, 100);
        when(testCaseType.getSourceRange()).thenReturn(typeRange);

        // when
        assertEquals(7, Part.TEST_CLASS_ANNOTATION.getInsertionOffset(context));
    }

    @Test
    public void should_insert_test_class_fields_after_last_field_when_fields_exist() throws Exception
    {
        // given
        final IField lastField = mock(IField.class);
        final ISourceRange lastFieldRange = range(20, 12);
        when(lastField.getSourceRange()).thenReturn(lastFieldRange);
        when(testCaseType.getFields()).thenReturn(new IField[] { mock(IField.class), lastField });

        // when
        assertEquals(32, Part.TEST_CLASS_FIELDS.getInsertionOffset(context));
    }

    @Test
    public void should_insert_test_class_fields_before_first_member_when_no_field_exists() throws Exception
    {
        // given
        when(testCaseType.getFields()).thenReturn(new IField[0]);
        final ISourceRange typeRange = range(7, 100);
        when(testCaseType.getSourceRange()).thenReturn(typeRange);
        when(testCaseType.getSource()).thenReturn("public class Foo {\n    // a comment\n}");

        // when
        assertEquals(7 + "public class Foo {".indexOf('{') + 1, Part.TEST_CLASS_FIELDS.getInsertionOffset(context));
    }

    @Test
    public void should_insert_code_at_end_of_before_instance_method() throws Exception
    {
        // given
        final ICompilationUnit testCaseCompilationUnit = mock(ICompilationUnit.class);
        final IType type = mock(IType.class);
        when(testCaseCompilationUnit.findPrimaryType()).thenReturn(type);
        final IMethod beforeMethod = mock(IMethod.class);
        when(type.getMethod(null, new String[0])).thenReturn(beforeMethod);
        final ISourceRange beforeMethodRange = range(42, 60);
        when(beforeMethod.getSourceRange()).thenReturn(beforeMethodRange);
        final String methodSource = "    @Before\n    public void setUp() throws Exception {\n        doSomething();\n    }";
        when(beforeMethod.getSource()).thenReturn(methodSource);
        context = new MockingContext(new Dependencies(null, null, null), mock(IType.class), testCaseCompilationUnit, "junit4",
                                     Collections.emptyList());

        // when
        final int offset = Part.BEFORE_INSTANCE_METHOD.getInsertionOffset(context);

        // then: offset points to the character before the closing brace
        assertEquals(42 + methodSource.lastIndexOf('}') - 1, offset);
    }

    @Test
    public void should_insert_before_instance_method_definition_before_first_method_when_methods_exist() throws Exception
    {
        // given
        final IMethod firstMethod = mock(IMethod.class);
        final ISourceRange firstMethodRange = range(55, 30);
        when(firstMethod.getSourceRange()).thenReturn(firstMethodRange);
        when(testCaseType.getMethods()).thenReturn(new IMethod[] { firstMethod });

        // when
        assertEquals(55, Part.BEFORE_INSTANCE_METHOD_DEFINITION.getInsertionOffset(context));
    }

    @Test
    public void should_insert_before_instance_method_definition_before_first_member_when_no_method_exists() throws Exception
    {
        // given
        when(testCaseType.getMethods()).thenReturn(new IMethod[0]);
        final ISourceRange typeRange = range(7, 100);
        when(testCaseType.getSourceRange()).thenReturn(typeRange);
        when(testCaseType.getSource()).thenReturn("public class Foo {\n}");

        // when
        assertEquals(7 + "public class Foo {".indexOf('{') + 1, Part.BEFORE_INSTANCE_METHOD_DEFINITION.getInsertionOffset(context));
    }

    private ISourceRange range(int offset, int length)
    {
        final ISourceRange range = mock(ISourceRange.class);
        when(range.getOffset()).thenReturn(offset);
        when(range.getLength()).thenReturn(length);
        return range;
    }
}
