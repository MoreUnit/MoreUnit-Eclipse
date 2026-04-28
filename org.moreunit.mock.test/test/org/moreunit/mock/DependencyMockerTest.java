package org.moreunit.mock;

import static org.mockito.Mockito.*;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.moreunit.core.log.Logger;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.preferences.Preferences;
import org.moreunit.mock.templates.MockingTemplateStore;
import org.moreunit.mock.templates.TemplateProcessor;
import org.moreunit.preferences.PreferenceConstants;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DependencyMockerTest
{
    private static final String SOME_TEST_TYPE = PreferenceConstants.DEFAULT_TEST_TYPE;

    @Mock
    private Logger logger;
    @Mock
    private MockingTemplateStore templateStore;
    @Mock
    private Preferences preferences;
    @Mock
    private TemplateProcessor templateApplicator;

    private DependencyMocker dependencyMocker;

    @Mock
    private IJavaProject project;
    @Mock
    private Dependencies dependencies;
    @Mock
    private IType classUnderTest;
    @Mock
    private IType testCase;

    @Before
    public void createDependencyMocker() throws Exception
    {
        dependencyMocker = new DependencyMocker(preferences, templateStore, templateApplicator, logger);
    }

    @Test
    public void should_abort_when_there_are_dependencies() throws Exception
    {
        // given
        when(dependencies.isEmpty()).thenReturn(true);

        // when
        dependencyMocker.mockDependencies(dependencies, classUnderTest, testCase, SOME_TEST_TYPE);

        // then
        verifyNoInteractions(templateApplicator);
    }

    @Test
    public void should_retrieve_template_from_preferences_for_test_case_project() throws Exception
    {
        // given
        when(classUnderTest.getJavaProject()).thenReturn(project);

        when(preferences.getMockingTemplate(project)).thenReturn("test-template-id");

        // when
        dependencyMocker.mockDependencies(dependencies, classUnderTest, testCase, SOME_TEST_TYPE);

        // then
        verify(templateStore).get("test-template-id");
    }

    @Test
    public void should_abort_when_template_is_null() throws Exception
    {
        // given
        when(dependencies.isEmpty()).thenReturn(false);
        when(classUnderTest.getJavaProject()).thenReturn(project);
        when(preferences.getMockingTemplate(project)).thenReturn("test-template-id");
        when(templateStore.get("test-template-id")).thenReturn(null);

        // when
        dependencyMocker.mockDependencies(dependencies, classUnderTest, testCase, SOME_TEST_TYPE);

        // then
        verify(logger).error("Template not found: test-template-id");
        verifyNoInteractions(templateApplicator);
    }

    @Test
    public void should_apply_template_when_found() throws Exception
    {
        // given
        when(dependencies.isEmpty()).thenReturn(false);
        when(classUnderTest.getJavaProject()).thenReturn(project);
        when(preferences.getMockingTemplate(project)).thenReturn("test-template-id");

        org.moreunit.mock.model.MockingTemplate template = new org.moreunit.mock.model.MockingTemplate("test-template-id");
        when(templateStore.get("test-template-id")).thenReturn(template);

        // when
        dependencyMocker.mockDependencies(dependencies, classUnderTest, testCase, SOME_TEST_TYPE);

        // then
        verify(templateApplicator).applyTemplate(template, dependencies, classUnderTest, testCase, SOME_TEST_TYPE);
    }

    @Test
    public void should_log_error_when_template_application_fails() throws Exception
    {
        // given
        when(dependencies.isEmpty()).thenReturn(false);
        when(classUnderTest.getJavaProject()).thenReturn(project);
        when(preferences.getMockingTemplate(project)).thenReturn("test-template-id");

        org.moreunit.mock.model.MockingTemplate template = new org.moreunit.mock.model.MockingTemplate("test-template-id");
        when(templateStore.get("test-template-id")).thenReturn(template);

        when(testCase.getElementName()).thenReturn("MyTest");

        org.moreunit.mock.templates.MockingTemplateException exception = new org.moreunit.mock.templates.MockingTemplateException("error");
        doThrow(exception).when(templateApplicator).applyTemplate(template, dependencies, classUnderTest, testCase, SOME_TEST_TYPE);

        // when
        dependencyMocker.mockDependencies(dependencies, classUnderTest, testCase, SOME_TEST_TYPE);

        // then
        verify(logger).error("Could not apply MockingTemplate [id=test-template-id, category=null] to MyTest", exception);
    }
}
