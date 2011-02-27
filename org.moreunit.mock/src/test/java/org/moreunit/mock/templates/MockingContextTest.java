package org.moreunit.mock.templates;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.mock.model.CodeTemplate;
import org.moreunit.mock.model.Dependencies;
import org.moreunit.mock.model.Dependency;
import org.moreunit.mock.model.InjectionType;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.model.Part;
import org.moreunit.mock.model.SetterDependency;

@RunWith(MockitoJUnitRunner.class)
public class MockingContextTest
{
    @Mock
    private IType classUnderTest;
    @Mock
    private IType testCase;
    @Mock
    private ICompilationUnit testCaseCompilationUnit;
    @Mock
    private TemplateProcessor templateProcessor;

    private Dependencies dependencies;
    private ArrayList<PatternResolver> patternResolvers;
    private MockingContext mockingContext;

    @Before
    public void createMockingContext() throws Exception
    {
        when(testCaseCompilationUnit.findPrimaryType()).thenReturn(testCase);

        dependencies = new Dependencies(null, null);
        patternResolvers = new ArrayList<PatternResolver>();
        mockingContext = new MockingContext(classUnderTest, testCaseCompilationUnit, dependencies, patternResolvers);
    }

    @Test
    public void should_detect_constructor_injections_when_initializing() throws Exception
    {
        // given
        dependencies.constructorDependencies.add(new Dependency("pack.age.Type", "aType"));

        // when
        mockingContext = new MockingContext(classUnderTest, testCaseCompilationUnit, dependencies, patternResolvers);

        // then
        assertThat(mockingContext.usesInjectionType(InjectionType.constructor)).isTrue();
        assertThat(mockingContext.usesInjectionType(InjectionType.field)).isFalse();
        assertThat(mockingContext.usesInjectionType(InjectionType.setter)).isFalse();
    }

    @Test
    public void should_detect_setter_injections_when_initializing() throws Exception
    {
        // given
        dependencies.setterDependencies.add(new SetterDependency("pack.age.Type", "setIt"));

        // when
        mockingContext = new MockingContext(classUnderTest, testCaseCompilationUnit, dependencies, patternResolvers);

        // then
        assertThat(mockingContext.usesInjectionType(InjectionType.constructor)).isFalse();
        assertThat(mockingContext.usesInjectionType(InjectionType.field)).isFalse();
        assertThat(mockingContext.usesInjectionType(InjectionType.setter)).isTrue();
    }

    @Test
    public void should_detect_field_injections_when_initializing() throws Exception
    {
        // given
        dependencies.fieldDependencies.add(new Dependency("pack.age.Type", "aType"));

        // when
        mockingContext = new MockingContext(classUnderTest, testCaseCompilationUnit, dependencies, patternResolvers);

        // then
        assertThat(mockingContext.usesInjectionType(InjectionType.constructor)).isFalse();
        assertThat(mockingContext.usesInjectionType(InjectionType.field)).isTrue();
        assertThat(mockingContext.usesInjectionType(InjectionType.setter)).isFalse();
    }

    @Test
    public void should_detect_several_different_injection_types_when_initializing() throws Exception
    {
        // given
        dependencies.constructorDependencies.add(new Dependency("pack.age.Type", "aType"));
        dependencies.setterDependencies.add(new SetterDependency("pack.age2.OtherType", "setIt"));

        // when
        mockingContext = new MockingContext(classUnderTest, testCaseCompilationUnit, dependencies, patternResolvers);

        // then
        assertThat(mockingContext.usesInjectionType(InjectionType.constructor)).isTrue();
        assertThat(mockingContext.usesInjectionType(InjectionType.field)).isFalse();
        assertThat(mockingContext.usesInjectionType(InjectionType.setter)).isTrue();
    }

    @Test
    public void should_detect_all_injection_types_when_initializing() throws Exception
    {
        // given
        dependencies.constructorDependencies.add(new Dependency("pack.age.Type", "aType"));
        dependencies.setterDependencies.add(new SetterDependency("pack.age2.OtherType", "setIt"));
        dependencies.fieldDependencies.add(new Dependency("some.where.Thing", "attribute"));

        // when
        mockingContext = new MockingContext(classUnderTest, testCaseCompilationUnit, dependencies, patternResolvers);

        // then
        assertThat(mockingContext.usesInjectionType(InjectionType.constructor)).isTrue();
        assertThat(mockingContext.usesInjectionType(InjectionType.field)).isTrue();
        assertThat(mockingContext.usesInjectionType(InjectionType.setter)).isTrue();
    }

    @Test
    public void should_find_before_bethod_if_it_exists_when_preparing_context() throws Exception
    {
        // given
        when(classUnderTest.getElementName()).thenReturn("Something");

        IMethod[] methods = new IMethod[] { method("doIt", false), method("foobar", true), method("createSomething", true) };
        when(testCase.getMethods()).thenReturn(methods);

        // when
        mockingContext.prepareContext(templateRequiringBeforeMethod(), null);

        // then
        verifyZeroInteractions(templateProcessor);
        assertThat(mockingContext.beforeInstanceMethod().getElementName()).isEqualTo("createSomething");
    }

    private IMethod method(String name, boolean hasBeforeAnnotation)
    {
        IMethod method = mock(IMethod.class);
        when(method.getElementName()).thenReturn(name);

        IAnnotation annotation = mock(IAnnotation.class);
        when(annotation.exists()).thenReturn(hasBeforeAnnotation);

        when(method.getAnnotation("Before")).thenReturn(annotation);

        return method;
    }

    private MockingTemplate templateRequiringBeforeMethod()
    {
        CodeTemplate templateRequiringBeforeMethod = new CodeTemplate("", Part.BEFORE_INSTANCE_METHOD, "");
        return new MockingTemplate("", "", "", asList(templateRequiringBeforeMethod));
    }

    @Test
    public void should_create_before_bethod_if_it_does_not_exist_when_preparing_context() throws Exception
    {
        // given
        when(classUnderTest.getElementName()).thenReturn("Something");

        IMethod[] methods = new IMethod[] { method("doIt", false), method("foobar", true) };
        when(testCase.getMethods()).thenReturn(methods);

        // when
        mockingContext.prepareContext(templateRequiringBeforeMethod(), templateProcessor);

        // then
        CodeTemplate codeTemplate = new CodeTemplate("org.moreunit.mock.beforeInstanceMethodCreation", null, null);
        verify(templateProcessor).applyTemplate(codeTemplate, mockingContext);

        // when
        // (result is not directly checked as it would involve more mocking)
        mockingContext.beforeInstanceMethod();

        // then
        verify(testCase).getMethod("createSomething", new String[0]);
    }

    @Test
    public void should_increment_before_bethod_name_if_name_is_already_used() throws Exception
    {
        // given
        when(classUnderTest.getElementName()).thenReturn("Something");

        IMethod[] methods = new IMethod[] { method("doIt", false), method("foobar", true), method("createSomething", false) };
        when(testCase.getMethods()).thenReturn(methods);

        // when
        mockingContext.prepareContext(templateRequiringBeforeMethod(), templateProcessor);

        // then
        CodeTemplate codeTemplate = new CodeTemplate("org.moreunit.mock.beforeInstanceMethodCreation", null, null);
        verify(templateProcessor).applyTemplate(codeTemplate, mockingContext);

        // when
        // (result is not directly checked as it would involve more mocking)
        mockingContext.beforeInstanceMethod();

        // then
        verify(testCase).getMethod("createSomething2", new String[0]);
    }

    @Test
    public void happy_preevaluation() throws Exception
    {
        // given
        patternResolvers.add(new TestResolver());
        patternResolvers.add(new TestResolver());

        // when
        EclipseTemplate eclipseTemplate = mockingContext.preEvaluate(new CodeTemplate("a template", Part.TEST_CLASS_FIELDS, "pattern contents"));

        // then
        assertThat(eclipseTemplate.template().getPattern()).isEqualTo("pattern contents");
        assertThat(eclipseTemplate.part()).isEqualTo(Part.TEST_CLASS_FIELDS);

        for (PatternResolver resolver : patternResolvers)
        {
            assertThat(((TestResolver) resolver).called).isTrue();
        }
    }

    private static class TestResolver implements PatternResolver
    {
        boolean called;

        public String resolve(String pattern)
        {
            called = true;
            return pattern;
        }
    }
}
