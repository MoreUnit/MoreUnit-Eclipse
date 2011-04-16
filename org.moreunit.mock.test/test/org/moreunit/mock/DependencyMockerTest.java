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
import org.moreunit.mock.log.Logger;
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
    private IType classUnderTest = mock(IType.class);
    @Mock
    private IType testCase = mock(IType.class);

    @Before
    public void createDependencyMocker() throws Exception
    {
        dependencyMocker = new DependencyMocker(preferences, templateStore, templateApplicator, logger);
    }

    @Test
    public void should_log_error_and_abort_when_template_not_found() throws Exception
    {
        // given
        when(templateStore.get(anyString())).thenReturn(null);

        // when
        dependencyMocker.mockDependencies(null, classUnderTest, testCase);

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
        dependencyMocker.mockDependencies(null, classUnderTest, testCase);

        // then
        verify(templateStore).get("test-template-id");
    }
}
