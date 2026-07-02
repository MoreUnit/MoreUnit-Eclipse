package org.moreunit.annotation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.eclipse.jdt.core.ISourceRange;
import org.junit.jupiter.api.Test;

public class MoreUnitAnnotationTest
{
    @Test
    public void create_annotation_for_tested_method_should_return_annotation()
    {
        ISourceRange range = mock(ISourceRange.class);

        assertNotNull(MoreUnitAnnotation.createAnnotationForTestedMethod(range));
    }

    @Test
    public void create_annotation_for_ignored_test_method_should_return_annotation()
    {
        ISourceRange range = mock(ISourceRange.class);

        assertNotNull(MoreUnitAnnotation.createAnnotationForIgnoredTesMethod(range));
    }
}
