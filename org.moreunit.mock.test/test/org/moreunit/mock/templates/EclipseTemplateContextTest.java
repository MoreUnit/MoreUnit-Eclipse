package org.moreunit.mock.templates;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.templates.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.model.Part;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class EclipseTemplateContextTest
{
    private static final String TEST_CASE_SOURCE = "public class FooTest {\n    private SomeService someService;\n}";
    private static final int FIELD_OFFSET = TEST_CASE_SOURCE.indexOf("private");
    private static final int FIELD_LENGTH = "private SomeService someService;".length();

    @Mock
    private ICompilationUnit testCaseCompilationUnit;

    private MockingContext mockingContext;
    private EclipseTemplateContext templateContext;

    @BeforeEach
    public void setUp() throws Exception
    {
        when(testCaseCompilationUnit.getSource()).thenReturn(TEST_CASE_SOURCE);

        final IType testCaseType = mock(IType.class);
        when(testCaseCompilationUnit.findPrimaryType()).thenReturn(testCaseType);
        final IField field = mock(IField.class);
        final ISourceRange fieldRange = range(FIELD_OFFSET, FIELD_LENGTH);
        when(field.getSourceRange()).thenReturn(fieldRange);
        when(testCaseType.getFields()).thenReturn(new IField[] { field });

        mockingContext = new MockingContext(new Dependencies(null, null, null), mock(IType.class), testCaseCompilationUnit, "junit4",
                                            java.util.Collections.emptyList());
        templateContext = new EclipseTemplateContext(mockingContext);
    }

    @Test
    public void should_throw_when_template_cannot_be_evaluated() throws Exception
    {
        // given: a template whose name does not match the context key, so that
        // CustomJavaContext.canEvaluate() returns false
        final EclipseTemplate eclipseTemplate = mock(EclipseTemplate.class);
        when(eclipseTemplate.getInsertionOffset(mockingContext)).thenReturn(0);
        when(eclipseTemplate.template()).thenReturn(new Template("a-template-that-cannot-be-evaluated", "", "java", "some pattern", false));

        // when + then
        final MockingTemplateException exception = assertThrows(MockingTemplateException.class, () -> templateContext.evaluate(eclipseTemplate));
        assertTrue(exception.getMessage().contains("some pattern"));
        assertFalse(exception.isUserMessage());
    }

    private ISourceRange range(int offset, int length)
    {
        final ISourceRange range = mock(ISourceRange.class);
        when(range.getOffset()).thenReturn(offset);
        when(range.getLength()).thenReturn(length);
        return range;
    }
}
