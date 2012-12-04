package org.moreunit.mock.templates;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.moreunit.preferences.PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3;
import static org.moreunit.preferences.PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4;
import static org.moreunit.preferences.PreferenceConstants.TEST_TYPE_VALUE_TESTNG;

import java.util.ArrayList;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.templates.TemplateException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.model.CodeTemplate;
import org.moreunit.mock.model.ConditionType;
import org.moreunit.mock.model.Dependency;
import org.moreunit.mock.model.ExcludeIf;
import org.moreunit.mock.model.FieldDependency;
import org.moreunit.mock.model.InclusionCondition;
import org.moreunit.mock.model.InjectionType;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.model.Part;
import org.moreunit.mock.model.SetterDependency;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences.ProjectPreferences;

@RunWith(MockitoJUnitRunner.class)
public class MockingContextTest
{
    private static final String DEFAULT_TEST_TYPE = PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4;

    @Mock
    private IType classUnderTest;
    @Mock
    private IType testCase;
    @Mock
    private ICompilationUnit testCaseCompilationUnit;
    @Mock
    private TemplateProcessor templateProcessor;
    @Mock
    private ProjectPreferences projectPreferences;

    private Dependencies dependencies;
    private ArrayList<PatternResolver> patternResolvers;
    private MockingContext mockingContext;

    @Before
    public void prepareMockingContextConfiguration() throws Exception
    {
        when(testCaseCompilationUnit.findPrimaryType()).thenReturn(testCase);

        dependencies = new Dependencies(null, null, null);
        patternResolvers = new ArrayList<PatternResolver>();
        createMockingContextWithTestType(DEFAULT_TEST_TYPE);
    }

    @Test
    public void should_detect_constructor_injections_when_initializing() throws Exception
    {
        // given
        dependencies.injectableByConstructor().add(new Dependency("pack.age.Type", "aType"));

        // when
        mockingContext = createMockingContext();

        // then
        assertThat(mockingContext.usesInjectionType(InjectionType.constructor)).isTrue();
        assertThat(mockingContext.usesInjectionType(InjectionType.field)).isFalse();
        assertThat(mockingContext.usesInjectionType(InjectionType.setter)).isFalse();
    }

    @Test
    public void should_detect_setter_injections_when_initializing() throws Exception
    {
        // given
        dependencies.injectableBySetter().add(new SetterDependency("pack.age.Type", "setIt"));

        // when
        mockingContext = createMockingContext();

        // then
        assertThat(mockingContext.usesInjectionType(InjectionType.constructor)).isFalse();
        assertThat(mockingContext.usesInjectionType(InjectionType.field)).isFalse();
        assertThat(mockingContext.usesInjectionType(InjectionType.setter)).isTrue();
    }

    @Test
    public void should_detect_field_injections_when_initializing() throws Exception
    {
        // given
        dependencies.injectableByField().add(new FieldDependency("pack.age.Type", "aType", "aType"));

        // when
        mockingContext = createMockingContext();

        // then
        assertThat(mockingContext.usesInjectionType(InjectionType.constructor)).isFalse();
        assertThat(mockingContext.usesInjectionType(InjectionType.field)).isTrue();
        assertThat(mockingContext.usesInjectionType(InjectionType.setter)).isFalse();
    }

    @Test
    public void should_detect_several_different_injection_types_when_initializing() throws Exception
    {
        // given
        dependencies.injectableByConstructor().add(new Dependency("pack.age.Type", "aType"));
        dependencies.injectableBySetter().add(new SetterDependency("pack.age2.OtherType", "setIt"));

        // when
        mockingContext = createMockingContext();

        // then
        assertThat(mockingContext.usesInjectionType(InjectionType.constructor)).isTrue();
        assertThat(mockingContext.usesInjectionType(InjectionType.field)).isFalse();
        assertThat(mockingContext.usesInjectionType(InjectionType.setter)).isTrue();
    }

    @Test
    public void should_detect_all_injection_types_when_initializing() throws Exception
    {
        // given
        dependencies.injectableByConstructor().add(new Dependency("pack.age.Type", "aType"));
        dependencies.injectableBySetter().add(new SetterDependency("pack.age2.OtherType", "setIt"));
        dependencies.injectableByField().add(new FieldDependency("some.where.Thing", "attribute", "attribute"));

        // when
        mockingContext = createMockingContext();

        // then
        assertThat(mockingContext.usesInjectionType(InjectionType.constructor)).isTrue();
        assertThat(mockingContext.usesInjectionType(InjectionType.field)).isTrue();
        assertThat(mockingContext.usesInjectionType(InjectionType.setter)).isTrue();
    }

    @Test
    public void should_find_before_method_if_it_exists_when_preparing_context__junit3() throws Exception
    {
        // given
        createMockingContextWithTestType(TEST_TYPE_VALUE_JUNIT_3);

        when(classUnderTest.getElementName()).thenReturn("Something");

        IMethod[] methods = new IMethod[] { method("doIt"), method("foobar"), method("setUp") };
        when(testCase.getMethods()).thenReturn(methods);

        // when
        mockingContext.prepareContext(templateRequiringBeforeMethod(), null);

        // then
        verifyZeroInteractions(templateProcessor);
        assertThat(mockingContext.beforeInstanceMethod().getElementName()).isEqualTo("setUp");
    }

    @Test
    public void should_find_before_method_if_it_exists_when_preparing_context__junit4() throws Exception
    {
        // given
        createMockingContextWithTestType(TEST_TYPE_VALUE_JUNIT_4);

        when(classUnderTest.getElementName()).thenReturn("Something");

        IMethod[] methods = new IMethod[] { method("doIt"), methodWithAnnotation("foobar", "Before"), methodWithAnnotation("createSomething", "Before") };
        when(testCase.getMethods()).thenReturn(methods);

        // when
        mockingContext.prepareContext(templateRequiringBeforeMethod(), null);

        // then
        verifyZeroInteractions(templateProcessor);
        assertThat(mockingContext.beforeInstanceMethod().getElementName()).isEqualTo("createSomething");
    }

    @Test
    public void should_find_before_method_if_it_exists_when_preparing_context__testng() throws Exception
    {
        // given
        createMockingContextWithTestType(TEST_TYPE_VALUE_TESTNG);

        when(classUnderTest.getElementName()).thenReturn("Something");

        IMethod[] methods = new IMethod[] { method("doIt"), methodWithAnnotation("foobar", "BeforeMethod"), methodWithAnnotation("createSomething", "BeforeMethod") };
        when(testCase.getMethods()).thenReturn(methods);

        // when
        mockingContext.prepareContext(templateRequiringBeforeMethod(), null);

        // then
        verifyZeroInteractions(templateProcessor);
        assertThat(mockingContext.beforeInstanceMethod().getElementName()).isEqualTo("createSomething");
    }

    @Test
    public void should_create_before_method_if_it_does_not_exist_when_preparing_context__junit3() throws Exception
    {
        // given
        createMockingContextWithTestType(TEST_TYPE_VALUE_JUNIT_3);

        when(classUnderTest.getElementName()).thenReturn("Something");

        IMethod[] methods = new IMethod[] { method("doIt"), method("foobar") };
        when(testCase.getMethods()).thenReturn(methods);

        // when
        mockingContext.prepareContext(templateRequiringBeforeMethod(), templateProcessor);

        verifyThatBeforeInstanceMethodHasBeenCreated();

        assertThat(mockingContext.getBeforeInstanceMethodName()).isEqualTo("setUp");
    }

    @Test
    public void should_create_before_method_if_it_does_not_exist_when_preparing_context__junit4() throws Exception
    {
        // given
        createMockingContextWithTestType(TEST_TYPE_VALUE_JUNIT_4);

        when(classUnderTest.getElementName()).thenReturn("Something");

        IMethod[] methods = new IMethod[] { method("doIt"), methodWithAnnotation("foobar", "Before") };
        when(testCase.getMethods()).thenReturn(methods);

        // when
        mockingContext.prepareContext(templateRequiringBeforeMethod(), templateProcessor);

        // then
        verifyThatBeforeInstanceMethodHasBeenCreatedWithPatternContaining("org.junit.Before");

        assertThat(mockingContext.getBeforeInstanceMethodName()).isEqualTo("createSomething");
    }

    @Test
    public void should_create_before_method_if_it_does_not_exist_when_preparing_context__testNg() throws Exception
    {
        // given
        createMockingContextWithTestType(TEST_TYPE_VALUE_TESTNG);

        when(classUnderTest.getElementName()).thenReturn("Something");

        IMethod[] methods = new IMethod[] { method("doIt"), methodWithAnnotation("foobar", "BeforeMethod") };
        when(testCase.getMethods()).thenReturn(methods);

        // when
        mockingContext.prepareContext(templateRequiringBeforeMethod(), templateProcessor);

        verifyThatBeforeInstanceMethodHasBeenCreatedWithPatternContaining("org.testng.annotations.BeforeMethod");

        assertThat(mockingContext.getBeforeInstanceMethodName()).isEqualTo("createSomething");
    }

    @Test
    public void should_increment_before_method_name_if_name_is_already_used() throws Exception
    {
        // given
        when(classUnderTest.getElementName()).thenReturn("Something");

        IMethod[] methods = new IMethod[] { method("doIt"), method("foobar"), method("createSomething") };
        when(testCase.getMethods()).thenReturn(methods);

        // when
        mockingContext.prepareContext(templateRequiringBeforeMethod(), templateProcessor);

        verifyThatBeforeInstanceMethodHasBeenCreated();

        assertThat(mockingContext.getBeforeInstanceMethodName()).isEqualTo("createSomething2");
    }

    @Test
    public void should_not_create_before_method_when_template_is_excluded() throws Exception
    {
        // given
        String testType = TEST_TYPE_VALUE_JUNIT_4;

        createMockingContextWithTestType(testType);

        when(classUnderTest.getElementName()).thenReturn("Something");

        IMethod[] methods = new IMethod[] { method("doIt") };
        when(testCase.getMethods()).thenReturn(methods);

        // when
        mockingContext.prepareContext(templateRequiringBeforeMethod(new ExcludeIf(ConditionType.TEST_TYPE, testType)), templateProcessor);

        // then
        CodeTemplate codeTemplate = new CodeTemplate(MockingContext.BEFORE_INSTANCE_METHOD_CREATION_TEMPLATE_ID, null, null);
        verify(templateProcessor, never()).applyTemplate(codeTemplate, mockingContext);

        assertThat(mockingContext.getBeforeInstanceMethodName()).isNull();
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

    private void createMockingContextWithTestType(String testType) throws MockingTemplateException
    {
        when(projectPreferences.getTestType()).thenReturn(testType);
        mockingContext = createMockingContext();
    }

    private MockingContext createMockingContext() throws MockingTemplateException
    {
        return new MockingContext(dependencies, classUnderTest, testCaseCompilationUnit, projectPreferences, patternResolvers);
    }

    private IMethod method(String name)
    {
        return methodWithAnnotation(name, null);
    }

    private IMethod methodWithAnnotation(String name, String methodAnnotation)
    {
        IMethod method = mock(IMethod.class);
        when(method.getElementName()).thenReturn(name);

        IAnnotation unexistingAnnotation = mock(IAnnotation.class);
        when(method.getAnnotation(anyString())).thenReturn(unexistingAnnotation);

        IAnnotation possiblyExistingAnnotation = mock(IAnnotation.class);
        when(possiblyExistingAnnotation.exists()).thenReturn(methodAnnotation != null);

        when(method.getAnnotation(methodAnnotation)).thenReturn(possiblyExistingAnnotation);

        return method;
    }

    private MockingTemplate templateRequiringBeforeMethod(InclusionCondition... conditions)
    {
        CodeTemplate templateRequiringBeforeMethod = new CodeTemplate("", Part.BEFORE_INSTANCE_METHOD, "", newHashSet(conditions));
        return new MockingTemplate("", "", "", asList(templateRequiringBeforeMethod));
    }

    private void verifyThatBeforeInstanceMethodHasBeenCreatedWithPatternContaining(String expectedPatternContent) throws JavaModelException, BadLocationException, TemplateException, MockingTemplateException
    {
        ArgumentCaptor<CodeTemplate> codeTemplate = verifyThatBeforeInstanceMethodHasBeenCreated();
        assertThat(codeTemplate.getValue().pattern()).contains(expectedPatternContent);
    }

    private ArgumentCaptor<CodeTemplate> verifyThatBeforeInstanceMethodHasBeenCreated() throws JavaModelException, BadLocationException, TemplateException, MockingTemplateException
    {
        ArgumentCaptor<CodeTemplate> codeTemplate = ArgumentCaptor.forClass(CodeTemplate.class);
        verify(templateProcessor).applyTemplate(codeTemplate.capture(), eq(mockingContext));

        assertThat(codeTemplate.getValue().id()).isEqualTo(MockingContext.BEFORE_INSTANCE_METHOD_CREATION_TEMPLATE_ID);
        assertThat(codeTemplate.getValue().part()).isEqualTo(Part.BEFORE_INSTANCE_METHOD_DEFINITION);
        return codeTemplate;
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
