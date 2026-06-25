package org.moreunit.mock.dependencies;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FieldTest
{
    private static final IAnnotation[] NO_ANNOTATIONS = {};

    private IAnnotation autowiredAnnotation;
    private IAnnotation injectAnnotation;
    private IAnnotation resourceAnnotation;

    private IField eclipseField;
    private Field field = new Field(eclipseField, false);

    @BeforeEach
    public void setUp() throws Exception
    {
        autowiredAnnotation = mock(IAnnotation.class);
        when(autowiredAnnotation.getElementName()).thenReturn("Autowired");
        injectAnnotation = mock(IAnnotation.class);
        when(injectAnnotation.getElementName()).thenReturn("pack.age.Inject");
        resourceAnnotation = mock(IAnnotation.class);
        when(resourceAnnotation.getElementName()).thenReturn("a.Resource");

        eclipseField = mock(IField.class);
        field = new Field(eclipseField, false);
    }

    @Test
    public void field_should_not_be_assignable_when_final() throws Exception
    {
        // given
        when(eclipseField.getFlags()).thenReturn(Flags.AccFinal);

        // then
        assertFalse(field.isAssignable());
    }

    @Test
    public void field_should_be_assignable_when_not_final() throws Exception
    {
        // given
        when(eclipseField.getFlags()).thenReturn(Flags.AccDefault);

        // then
        assertTrue(field.isAssignable());
    }

    @Test
    public void field_should_not_be_injectable_when_not_annotated_with_corresponding_annotation() throws Exception
    {
        // given
        when(eclipseField.getAnnotations()).thenReturn(NO_ANNOTATIONS);

        // then
        assertFalse(field.isInjectable());
    }

    @Test
    public void field_should_be_injectable_when_annotated_with_Inject() throws Exception
    {
        // given
        when(eclipseField.getAnnotations()).thenReturn(new IAnnotation[] { injectAnnotation });

        // then
        assertTrue(field.isInjectable());
    }

    @Test
    public void field_should_be_injectable_when_annotated_with_Resource() throws Exception
    {
        // given
        when(eclipseField.getAnnotations()).thenReturn(new IAnnotation[] { resourceAnnotation });

        // then
        assertTrue(field.isInjectable());
    }

    @Test
    public void field_should_be_injectable_when_annotated_with_Autowired() throws Exception
    {
        // given
        when(eclipseField.getAnnotations()).thenReturn(new IAnnotation[] { autowiredAnnotation });

        // then
        assertTrue(field.isInjectable());
    }
}
