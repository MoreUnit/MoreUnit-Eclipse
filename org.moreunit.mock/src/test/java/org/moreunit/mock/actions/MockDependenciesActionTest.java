package org.moreunit.mock.actions;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.mock.elements.TypeFacadeFactory;
import org.moreunit.mock.log.Logger;
import org.moreunit.mock.templates.MockingTemplate;
import org.moreunit.mock.templates.MockingTemplateStore;
import org.moreunit.mock.templates.TemplateProcessor;
import org.moreunit.mock.utils.ConversionUtils;

@RunWith(MockitoJUnitRunner.class)
public class MockDependenciesActionTest
{
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
    private ICompilationUnit openCompilationUnit;
    private IAction anAction = null;

    @Before
    public void createAction() throws Exception
    {
        action = new MockDependenciesAction(templateStore, templateApplicator, conversionUtils, facadeFactory, logger);

        IEditorPart activeEditor = mock(IEditorPart.class);
        when(conversionUtils.getCompilationUnit(activeEditor)).thenReturn(openCompilationUnit);
        action.setActiveEditor(null, activeEditor);
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

        IType testCase = mock(IType.class);
        ClassTypeFacade facade = classFacadeThatWillFoundTestCase(testCase);
        when(facadeFactory.createClassFacade(openCompilationUnit)).thenReturn(facade);

        // when
        action.run(anAction);

        // then
        verify(templateApplicator).applyTemplate(template, testCase);
    }

    @Test
    public void should_apply_template_if_open_class_is_a_test_case() throws Exception
    {
        // given
        MockingTemplate template = new MockingTemplate("test template");
        when(templateStore.get(anyString())).thenReturn(template);

        when(facadeFactory.isTestCase(openCompilationUnit)).thenReturn(true);

        IType testCase = mock(IType.class);
        when(openCompilationUnit.findPrimaryType()).thenReturn(testCase);

        // when
        action.run(anAction);

        // then
        verify(templateApplicator).applyTemplate(template, testCase);
    }
}
