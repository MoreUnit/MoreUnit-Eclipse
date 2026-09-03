package org.moreunit.mock.templates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.junit.jupiter.api.Test;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.model.Part;

public class ContextFactoryTest
{
    @Test
    public void should_create_mocking_context() throws MockingTemplateException
    {
        final ContextFactory factory = new ContextFactory();
        final MockingContext ctx = factory.createMockingContext(
            mock(Dependencies.class), mock(IType.class), "JUNIT5", mock(ICompilationUnit.class));

        assertNotNull(ctx);
    }

    @Test
    public void should_create_eclipse_template_context() throws MockingTemplateException
    {
        final ContextFactory factory = new ContextFactory();
        final MockingContext ctx = new MockingContext(
            mock(Dependencies.class), mock(IType.class), mock(ICompilationUnit.class), "JUNIT5",
            java.util.Collections.emptyList());

        assertNotNull(factory.createEclipseTemplateContext(ctx));
    }

    @Test
    public void should_create_eclipse_template_keeping_part_and_pattern() throws MockingTemplateException
    {
        // when
        final EclipseTemplate template = new EclipseTemplate(Part.TEST_CLASS_FIELDS, "some pattern");

        // then
        assertEquals(Part.TEST_CLASS_FIELDS, template.part());
        assertEquals("some pattern", template.template().getPattern());
    }

    @Test
    public void should_delegate_insertion_offset_computation_to_part() throws Exception
    {
        // given
        final ICompilationUnit testCaseCompilationUnit = mock(ICompilationUnit.class);
        final IType testCaseType = mock(IType.class);
        when(testCaseCompilationUnit.findPrimaryType()).thenReturn(testCaseType);
        final IField lastField = mock(IField.class);
        final ISourceRange lastFieldRange = range(20, 12);
        when(lastField.getSourceRange()).thenReturn(lastFieldRange);
        when(testCaseType.getFields()).thenReturn(new IField[] { lastField });
        final MockingContext ctx = new MockingContext(new Dependencies(null, null, null), mock(IType.class), testCaseCompilationUnit, "junit4",
                                                java.util.Collections.emptyList());

        // when
        final EclipseTemplate template = new EclipseTemplate(Part.TEST_CLASS_FIELDS, "some pattern");

        // then
        assertEquals(32, template.getInsertionOffset(ctx));
    }

    private ISourceRange range(int offset, int length)
    {
        final ISourceRange range = mock(ISourceRange.class);
        when(range.getOffset()).thenReturn(offset);
        when(range.getLength()).thenReturn(length);
        return range;
    }
}
