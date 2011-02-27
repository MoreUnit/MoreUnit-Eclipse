package org.moreunit.mock.actions;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.TestCaseTypeFacade;
import org.moreunit.mock.elements.TypeFacadeFactory;
import org.moreunit.mock.log.Logger;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.preferences.Preferences;
import org.moreunit.mock.templates.MockingTemplateStore;
import org.moreunit.mock.templates.TemplateProcessor;
import org.moreunit.mock.utils.ConversionUtils;

@RunWith(MockitoJUnitRunner.class)
public class MockDependenciesActionTest
{
    @Mock
    private Preferences preferences;
    @Mock
    private MockingTemplateStore templateStore;
    @Mock
    private TemplateProcessor templateApplicator;
    @Mock
    private ConversionUtils conversionUtils;
    @Mock
    private TypeFacadeFactory facadeFactory;
    @Mock
    private Logger logger;
    private MockDependenciesAction action;

    @Mock
    private IJavaProject project;
    @Mock
    private ICompilationUnit openCompilationUnit;
    private IAction anAction = null;

    @Before
    public void createAction() throws Exception
    {
        action = new MockDependenciesAction(preferences, templateStore, templateApplicator, conversionUtils, facadeFactory, logger);

        when(project.getElementName()).thenReturn("test-project");
        when(openCompilationUnit.getJavaProject()).thenReturn(project);

        IEditorPart activeEditor = mock(IEditorPart.class);
        when(conversionUtils.getCompilationUnit(activeEditor)).thenReturn(openCompilationUnit);
        action.setActiveEditor(null, activeEditor);
    }

    @Test
    public void should_retrieve_template_from_preferences_for_compilation_unit_project() throws Exception
    {
        // given
        when(preferences.getMockingTemplate(project)).thenReturn("test-template-id");

        // when
        action.run(anAction);

        // then
        verify(templateStore).get("test-template-id");
    }

    @Test
    public void should_log_error_when_template_not_found() throws Exception
    {
        // given
        when(templateStore.get(anyString())).thenReturn(null);

        // when
        action.run(anAction);

        // then
        verify(logger).error(any());
        verifyZeroInteractions(templateApplicator);
    }

    @Test
    public void should_not_apply_template_if_no_test_case_found_or_created() throws Exception
    {
        // given
        MockingTemplate template = new MockingTemplate("test template");
        when(templateStore.get(anyString())).thenReturn(template);

        when(facadeFactory.isTestCase(openCompilationUnit)).thenReturn(false);

        IType classUnderTest = mock(IType.class);
        when(openCompilationUnit.findPrimaryType()).thenReturn(classUnderTest);

        ClassTypeFacade facade = classFacadeThatWillFoundTestCase(null);
        when(facadeFactory.createClassFacade(openCompilationUnit)).thenReturn(facade);

        // when
        action.run(anAction);

        // then
        verifyZeroInteractions(templateApplicator);
    }

    private ClassTypeFacade classFacadeThatWillFoundTestCase(IType testCase)
    {
        ClassTypeFacade facade = mock(ClassTypeFacade.class);
        when(facade.getOneCorrespondingTestCase(eq(true), anyString())).thenReturn(testCase);
        return facade;
    }

    @Test
    public void should_apply_template_if_test_case_found_or_created() throws Exception
    {
        // given
        MockingTemplate template = new MockingTemplate("test template");
        when(templateStore.get(anyString())).thenReturn(template);

        when(facadeFactory.isTestCase(openCompilationUnit)).thenReturn(false);

        IType classUnderTest = mock(IType.class);
        when(openCompilationUnit.findPrimaryType()).thenReturn(classUnderTest);

        IType testCase = mock(IType.class);
        ClassTypeFacade facade = classFacadeThatWillFoundTestCase(testCase);
        when(facadeFactory.createClassFacade(openCompilationUnit)).thenReturn(facade);

        // when
        action.run(anAction);

        // then
        verify(templateApplicator).applyTemplate(template, classUnderTest, testCase);
    }

    @Test
    public void should_apply_template_if_class_under_test_found_or_created() throws Exception
    {
        // given
        MockingTemplate template = new MockingTemplate("test template");
        when(templateStore.get(anyString())).thenReturn(template);

        when(facadeFactory.isTestCase(openCompilationUnit)).thenReturn(true);

        IType classUnderTest = mock(IType.class);
        TestCaseTypeFacade facade = classFacadeThatWillFoundClassUnderTest(classUnderTest);
        when(facadeFactory.createTestCaseFacade(openCompilationUnit)).thenReturn(facade);

        IType testCase = mock(IType.class);
        when(openCompilationUnit.findPrimaryType()).thenReturn(testCase);

        // when
        action.run(anAction);

        // then
        verify(templateApplicator).applyTemplate(template, classUnderTest, testCase);
    }

    private TestCaseTypeFacade classFacadeThatWillFoundClassUnderTest(IType classUnderTest)
    {
        TestCaseTypeFacade facade = mock(TestCaseTypeFacade.class);
        when(facade.getOneCorrespondingMember(eq((IMethod) null), eq(true), anyBoolean(), anyString())).thenReturn(classUnderTest);
        return facade;
    }

    @Test
    public void should_not_apply_template_if_no_class_under_test_found_or_created() throws Exception
    {
        // given
        MockingTemplate template = new MockingTemplate("test template");
        when(templateStore.get(anyString())).thenReturn(template);

        when(facadeFactory.isTestCase(openCompilationUnit)).thenReturn(true);

        TestCaseTypeFacade facade = classFacadeThatWillFoundClassUnderTest(null);
        when(facadeFactory.createTestCaseFacade(openCompilationUnit)).thenReturn(facade);

        IType testCase = mock(IType.class);
        when(openCompilationUnit.findPrimaryType()).thenReturn(testCase);

        // when
        action.run(anAction);

        // then
        verifyZeroInteractions(templateApplicator);
    }
}
