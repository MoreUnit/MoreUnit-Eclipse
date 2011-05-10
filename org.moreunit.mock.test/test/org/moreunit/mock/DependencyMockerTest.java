package org.moreunit.mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.log.Logger;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.preferences.Preferences;
import org.moreunit.mock.templates.MockingTemplateStore;
import org.moreunit.mock.templates.TemplateProcessor;

@RunWith(MockitoJUnitRunner.class)
public class DependencyMockerTest
{
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
        mockTemplateRetrieval();

        when(dependencies.isEmpty()).thenReturn(true);

        // when
        dependencyMocker.mockDependencies(dependencies, classUnderTest, testCase);

        // then
        verifyZeroInteractions(templateApplicator);
    }

    private void mockTemplateRetrieval()
    {
        MockingTemplate template = mock(MockingTemplate.class);
        when(templateStore.get(anyString())).thenReturn(template);
    }

    @Test
    public void should_log_error_and_abort_when_template_not_found() throws Exception
    {
        // given
        when(templateStore.get(anyString())).thenReturn(null);

        // when
        dependencyMocker.mockDependencies(dependencies, classUnderTest, testCase);

        // then
        verify(logger).error(any());
        verifyZeroInteractions(templateApplicator);
    }

    @Test
    public void should_retrieve_template_from_preferences_for_test_case_project() throws Exception
    {
        // given
        when(classUnderTest.getJavaProject()).thenReturn(project);

        when(preferences.getMockingTemplate(project)).thenReturn("test-template-id");

        // when
        dependencyMocker.mockDependencies(dependencies, classUnderTest, testCase);

        // then
        verify(templateStore).get("test-template-id");
    }
}
