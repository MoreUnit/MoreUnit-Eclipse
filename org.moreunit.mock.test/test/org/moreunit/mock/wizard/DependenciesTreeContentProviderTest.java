package org.moreunit.mock.wizard;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.moreunit.core.log.Logger;
import org.moreunit.mock.dependencies.DependencyInjectionPointProvider;
import org.moreunit.mock.dependencies.Field;
import org.moreunit.mock.wizard.DependenciesTreeContentProvider.VisibleFields;

public class DependenciesTreeContentProviderTest
{
    private IType classUnderTest;
    private IMethod constructor;
    private IField eclipseField;
    private ITypeHierarchy hierarchy;
    private DependencyInjectionPointProvider provider;

    private Logger logger;

    @BeforeEach
    public void setUp() throws Exception
    {
        MockitoAnnotations.openMocks(this);

        classUnderTest = mock(IType.class);
        constructor = mock(IMethod.class);
        eclipseField = mock(IField.class);
        hierarchy = mock(ITypeHierarchy.class);
        provider = mock(DependencyInjectionPointProvider.class);

        when(classUnderTest.newSupertypeHierarchy(any())).thenReturn(hierarchy);
        when(hierarchy.getAllSuperclasses(classUnderTest)).thenReturn(new IType[0]);
        when(constructor.getElementName()).thenReturn("Foo");
        when(constructor.getSignature()).thenReturn("Foo(QFoo;)V");
        when(constructor.getDeclaringType()).thenReturn(classUnderTest);
        when(eclipseField.getElementName()).thenReturn("bar");
        when(eclipseField.getDeclaringType()).thenReturn(classUnderTest);

        logger = mock(Logger.class);
    }

    private void stubProvider(boolean fieldVisibleAndAssignable, boolean fieldInjectable) throws JavaModelException
    {
        when(provider.getConstructors()).thenReturn(asList(constructor));
        when(provider.getSetters()).thenReturn(emptyList());
        Field field = new Field(eclipseField, true);
        when(eclipseField.getFlags()).thenReturn(fieldVisibleAndAssignable ? Flags.AccDefault : Flags.AccFinal);
        IAnnotation[] annotations = fieldInjectable ? new IAnnotation[] { injectableAnnotation() } : new IAnnotation[0];
        when(eclipseField.getAnnotations()).thenReturn(annotations);
        when(provider.getFields()).thenReturn(asList(field));
    }

    private IAnnotation injectableAnnotation()
    {
        IAnnotation annotation = mock(IAnnotation.class);
        when(annotation.getElementName()).thenReturn("com.google.inject.Inject");
        return annotation;
    }

    @Test
    public void should_return_class_under_test_as_only_type_and_its_members_as_children() throws Exception
    {
        stubProvider(true, false);

        DependenciesTreeContentProvider contentProvider = new DependenciesTreeContentProvider(classUnderTest, provider, VisibleFields.VISIBLE_TO_TEST_CASE_ONLY, logger);

        assertArrayEquals(new IType[] { classUnderTest }, contentProvider.getTypes());
        assertArrayEquals(new IType[] { classUnderTest }, contentProvider.getElements(null));
        assertArrayEquals(new IMember[] { constructor, eclipseField }, contentProvider.getChildren(classUnderTest));
        assertTrue(contentProvider.hasChildren(classUnderTest));
        assertFalse(contentProvider.hasChildren(new Object()));
        assertSame(classUnderTest, contentProvider.getParent(constructor));
        assertNull(contentProvider.getParent(new Object()));
    }

    @Test
    public void should_replace_already_collected_method_with_identical_one() throws Exception
    {
        IMethod sameNamedMethod = mock(IMethod.class);
        when(sameNamedMethod.getElementName()).thenReturn("Foo");
        when(sameNamedMethod.getSignature()).thenReturn("Foo(QFoo;)V");
        when(sameNamedMethod.getDeclaringType()).thenReturn(classUnderTest);

        when(provider.getConstructors()).thenReturn(asList(constructor));
        when(provider.getSetters()).thenReturn(asList(sameNamedMethod));
        when(provider.getFields()).thenReturn(emptyList());

        DependenciesTreeContentProvider contentProvider = new DependenciesTreeContentProvider(classUnderTest, provider, VisibleFields.ALL, logger);

        assertArrayEquals(new IMethod[] { sameNamedMethod }, contentProvider.getChildren(classUnderTest));
    }

    @Test
    public void should_not_show_non_assignable_field_when_only_fields_visible_to_test_case_are_displayed() throws Exception
    {
        stubProvider(false, true);

        DependenciesTreeContentProvider contentProvider = new DependenciesTreeContentProvider(classUnderTest, provider, VisibleFields.VISIBLE_TO_TEST_CASE_ONLY, logger);

        assertArrayEquals(new IMember[] { constructor }, contentProvider.getChildren(classUnderTest));
    }

    @Test
    public void should_show_injectable_field_when_injectable_fields_are_displayed() throws Exception
    {
        stubProvider(false, true);

        DependenciesTreeContentProvider contentProvider = new DependenciesTreeContentProvider(classUnderTest, provider, VisibleFields.VISIBLE_TO_TEST_CASE_AND_INJECTABLE, logger);

        assertArrayEquals(new IMember[] { constructor, eclipseField }, contentProvider.getChildren(classUnderTest));
    }

    @Test
    public void should_show_all_fields_when_all_fields_are_displayed() throws Exception
    {
        stubProvider(false, false);

        DependenciesTreeContentProvider contentProvider = new DependenciesTreeContentProvider(classUnderTest, provider, VisibleFields.ALL, logger);

        assertArrayEquals(new IMember[] { constructor, eclipseField }, contentProvider.getChildren(classUnderTest));
    }

    @Test
    public void should_refresh_content_when_visible_fields_are_changed() throws Exception
    {
        stubProvider(false, true);

        DependenciesTreeContentProvider contentProvider = new DependenciesTreeContentProvider(classUnderTest, provider, VisibleFields.VISIBLE_TO_TEST_CASE_ONLY, logger);
        assertArrayEquals(new IMember[] { constructor }, contentProvider.getChildren(classUnderTest));

        contentProvider.showFields(VisibleFields.ALL);

        assertArrayEquals(new IMember[] { constructor, eclipseField }, contentProvider.getChildren(classUnderTest));
    }

    @Test
    public void should_log_error_when_java_model_cannot_be_read() throws Exception
    {
        when(classUnderTest.newSupertypeHierarchy(any())).thenThrow(javaModelException());
        when(provider.getConstructors()).thenThrow(javaModelException());

        DependenciesTreeContentProvider contentProvider = new DependenciesTreeContentProvider(classUnderTest, provider, VisibleFields.ALL, logger);

        assertEquals(0, contentProvider.getTypes().length);
        assertEquals(0, contentProvider.getChildren(classUnderTest).length);
        verify(logger, org.mockito.Mockito.times(2)).error(any(String.class), any(Throwable.class));
    }

    private static JavaModelException javaModelException()
    {
        return new JavaModelException(new org.eclipse.core.runtime.CoreException(org.eclipse.core.runtime.Status.CANCEL_STATUS));
    }

    @Test
    public void dispose_and_input_changed_should_do_nothing() throws Exception
    {
        stubProvider(true, false);
        DependenciesTreeContentProvider contentProvider = new DependenciesTreeContentProvider(classUnderTest, provider, VisibleFields.ALL, logger);

        contentProvider.dispose();
        contentProvider.inputChanged(null, null, null);
    }
}
