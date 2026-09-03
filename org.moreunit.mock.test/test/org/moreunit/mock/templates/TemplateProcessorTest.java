package org.moreunit.mock.templates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.templates.TemplateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.moreunit.core.log.Logger;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.model.CodeTemplate;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.model.Part;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class TemplateProcessorTest
{
    @Mock
    private ContextFactory contextFactory;
    @Mock
    private SourceFormatter sourceFormatter;
    @Mock
    private Logger logger;
    @Mock
    private IType classUnderTest;
    @Mock
    private IType testCase;
    @Mock
    private ICompilationUnit testCaseCompilationUnit;
    @Mock
    private ICompilationUnit workingCopy;
    @Mock
    private MockingContext mockingContext;
    @Mock
    private EclipseTemplateContext eclipseTemplateContext;
    @Mock
    private IBuffer buffer;

    private MockingTemplate mockingTemplate;
    private TemplateProcessor templateProcessor;

    @BeforeEach
    public void setUp() throws Exception
    {
        mockingTemplate = mock(MockingTemplate.class);
        templateProcessor = new TemplateProcessor(contextFactory, sourceFormatter, logger);

        when(testCase.getCompilationUnit()).thenReturn(testCaseCompilationUnit);
        when(testCaseCompilationUnit.isOpen()).thenReturn(true);
        when(testCaseCompilationUnit.getWorkingCopy(any())).thenReturn(workingCopy);
        when(contextFactory.createMockingContext(any(Dependencies.class), any(IType.class), anyString(), any(ICompilationUnit.class))).thenReturn(mockingContext);
        when(contextFactory.createEclipseTemplateContext(mockingContext)).thenReturn(eclipseTemplateContext);
        when(mockingContext.hasDependenciesToMock()).thenReturn(true);
        when(sourceFormatter.getFormattedSource(workingCopy)).thenReturn("formatted source");
        when(workingCopy.getBuffer()).thenReturn(buffer);
    }

    @Test
    public void should_apply_all_included_templates_and_update_test_case_source() throws Exception
    {
        // given
        final CodeTemplate includedTemplate = codeTemplate(true);
        final CodeTemplate excludedTemplate = codeTemplate(false);
        when(mockingTemplate.codeTemplates()).thenReturn(List.of(includedTemplate, excludedTemplate));

        final EclipseTemplate eclipseTemplate = new EclipseTemplate(Part.TEST_CLASS_FIELDS, "some pattern");
        when(mockingContext.preEvaluate(includedTemplate)).thenReturn(eclipseTemplate);

        // when
        templateProcessor.applyTemplate(mockingTemplate, new Dependencies(null, null, null), classUnderTest, testCase, "junit4");

        // then
        verify(contextFactory).createMockingContext(any(Dependencies.class), any(IType.class), anyString(), any(ICompilationUnit.class));
        verify(mockingContext).prepareContext(mockingTemplate, templateProcessor);
        verify(eclipseTemplateContext).evaluate(eclipseTemplate);
        verify(eclipseTemplateContext, org.mockito.Mockito.times(1)).evaluate(any());
        verify(buffer).setContents("formatted source");
        verify(workingCopy).commitWorkingCopy(org.mockito.ArgumentMatchers.eq(false), any());
    }

    @Test
    public void should_throw_NoDependenciesToMockException_when_test_case_has_no_dependencies() throws Exception
    {
        // given
        when(mockingContext.hasDependenciesToMock()).thenReturn(false);
        when(classUnderTest.getElementName()).thenReturn("Foo");
        when(mockingTemplate.codeTemplates()).thenReturn(List.of());

        // when + then
        assertThrows(NoDependenciesToMockException.class,
                     () -> templateProcessor.applyTemplate(mockingTemplate, new Dependencies(null, null, null), classUnderTest, testCase, "junit4"));

        verify(mockingContext, never()).prepareContext(mockingTemplate, templateProcessor);
    }

    @Test
    public void should_rethrow_MockingTemplateException_unchanged() throws Exception
    {
        // given
        final MockingTemplateException exception = new MockingTemplateException("boom");
        doThrow(exception).when(mockingContext).prepareContext(mockingTemplate, templateProcessor);
        when(mockingTemplate.codeTemplates()).thenReturn(List.of());

        // when + then
        final MockingTemplateException caught = assertThrows(MockingTemplateException.class,
            () -> templateProcessor.applyTemplate(mockingTemplate, new Dependencies(null, null, null), classUnderTest, testCase, "junit4"));
        assertSame(exception, caught);
    }

    @Test
    public void should_wrap_unexpected_exceptions_into_MockingTemplateException() throws Exception
    {
        // given
        when(mockingTemplate.codeTemplates()).thenReturn(List.of());
        when(sourceFormatter.getFormattedSource(workingCopy)).thenThrow(new BadLocationException("bad location"));

        // when + then
        final MockingTemplateException caught = assertThrows(MockingTemplateException.class,
            () -> templateProcessor.applyTemplate(mockingTemplate, new Dependencies(null, null, null), classUnderTest, testCase, "junit4"));
        assertEquals(BadLocationException.class, caught.getCause().getClass());
    }

    @Test
    public void should_open_compilation_unit_before_creating_working_copy_when_it_is_closed() throws Exception
    {
        // given
        when(testCaseCompilationUnit.isOpen()).thenReturn(false, true);
        when(mockingTemplate.codeTemplates()).thenReturn(List.of());

        // when
        templateProcessor.applyTemplate(mockingTemplate, new Dependencies(null, null, null), classUnderTest, testCase, "junit4");

        // then
        verify(testCaseCompilationUnit).open(any());
        verify(testCaseCompilationUnit, org.mockito.Mockito.times(2)).getWorkingCopy(any());
    }

    @Test
    public void should_log_error_and_continue_when_template_evaluation_fails() throws Exception
    {
        // given
        final CodeTemplate includedTemplate = codeTemplate(true);
        when(mockingTemplate.codeTemplates()).thenReturn(List.of(includedTemplate));
        final EclipseTemplate eclipseTemplate = new EclipseTemplate(Part.TEST_CLASS_FIELDS, "some pattern");
        when(mockingContext.preEvaluate(includedTemplate)).thenReturn(eclipseTemplate);
        final TemplateException evaluationError = new TemplateException("cannot evaluate");
        doThrow(evaluationError).when(eclipseTemplateContext).evaluate(eclipseTemplate);

        // when
        templateProcessor.applyTemplate(mockingTemplate, new Dependencies(null, null, null), classUnderTest, testCase, "junit4");

        // then
        verify(logger).error("Evaluating template: some pattern", evaluationError);
        verify(buffer).setContents("formatted source");
    }

    @Test
    public void should_propagate_error_raised_while_pre_evaluating_template() throws Exception
    {
        // given
        final CodeTemplate template = codeTemplate(true);
        final MockingTemplateException evaluationError = new MockingTemplateException("could not pre-evaluate");
        doThrow(evaluationError).when(mockingContext).preEvaluate(template);
        when(mockingTemplate.codeTemplates()).thenReturn(List.of(template));

        // when + then
        assertSame(evaluationError, assertThrows(MockingTemplateException.class,
            () -> templateProcessor.applyTemplate(mockingTemplate, new Dependencies(null, null, null), classUnderTest, testCase, "junit4")));
    }

    private CodeTemplate codeTemplate(boolean included) throws JavaModelException
    {
        final CodeTemplate codeTemplate = mock(CodeTemplate.class);
        when(codeTemplate.isIncluded(mockingContext)).thenReturn(included);
        return codeTemplate;
    }
}
