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
import org.moreunit.elements.ClassTypeFacade.CorrespondingTestCase;
import org.moreunit.elements.CorrespondingMemberRequest;
import org.moreunit.elements.TestCaseTypeFacade;
import org.moreunit.mock.elements.TypeFacadeFactory;
import org.moreunit.mock.utils.ConversionUtils;
import org.moreunit.mock.wizard.MockDependenciesPageManager;

@RunWith(MockitoJUnitRunner.class)
public class MockDependenciesActionTest
{
    @Mock
    private MockDependenciesPageManager pageManager;
    @Mock
    private ConversionUtils conversionUtils;
    @Mock
    private TypeFacadeFactory facadeFactory;
    @Mock
    private ICompilationUnit openCompilationUnit;

    private MockDependenciesAction action;

    private IAction anAction = null;

    @Before
    public void createAction() throws Exception
    {
        action = new MockDependenciesAction(pageManager, conversionUtils, facadeFactory);

        IEditorPart activeEditor = mock(IEditorPart.class);
        when(conversionUtils.getCompilationUnit(activeEditor)).thenReturn(openCompilationUnit);
        action.setActiveEditor(null, activeEditor);
    }

    @Test
    public void should_not_mock_dependencies_if_no_test_case_found_or_created() throws Exception
    {
        // given
        when(facadeFactory.isTestCase(openCompilationUnit)).thenReturn(false);

        IType classUnderTest = mock(IType.class);
        when(openCompilationUnit.findPrimaryType()).thenReturn(classUnderTest);

        ClassTypeFacade facade = classFacadeThatWillFindTestCase(null);
        when(facadeFactory.createClassFacade(openCompilationUnit)).thenReturn(facade);

        // when
        action.run(anAction);

        // then
        verifyZeroInteractions(pageManager);
    }

    private ClassTypeFacade classFacadeThatWillFindTestCase(IType testCase)
    {
        ClassTypeFacade facade = mock(ClassTypeFacade.class);
        when(facade.getOneCorrespondingTestCase(eq(true), anyString())).thenReturn(new CorrespondingTestCase(testCase, false));
        return facade;
    }

    @Test
    public void should_mock_dependencies_if_test_case_found_or_created() throws Exception
    {
        // given
        when(facadeFactory.isTestCase(openCompilationUnit)).thenReturn(false);

        IType classUnderTest = mock(IType.class);
        when(openCompilationUnit.findPrimaryType()).thenReturn(classUnderTest);

        IType testCase = mock(IType.class);
        ClassTypeFacade facade = classFacadeThatWillFindTestCase(testCase);
        when(facadeFactory.createClassFacade(openCompilationUnit)).thenReturn(facade);

        // when
        action.run(anAction);

        // then
        verify(pageManager).openWizard(eq(classUnderTest), eq(testCase));
    }

    @Test
    public void should_mock_dependencies_if_class_under_test_found_or_created() throws Exception
    {
        // given
        when(facadeFactory.isTestCase(openCompilationUnit)).thenReturn(true);

        IType classUnderTest = mock(IType.class);
        TestCaseTypeFacade facade = classFacadeThatWillFoundClassUnderTest(classUnderTest);
        when(facadeFactory.createTestCaseFacade(openCompilationUnit)).thenReturn(facade);

        IType testCase = mock(IType.class);
        when(openCompilationUnit.findPrimaryType()).thenReturn(testCase);

        // when
        action.run(anAction);

        // then
        verify(pageManager).openWizard(eq(classUnderTest), eq(testCase));
    }

    private TestCaseTypeFacade classFacadeThatWillFoundClassUnderTest(IType classUnderTest)
    {
        TestCaseTypeFacade facade = mock(TestCaseTypeFacade.class);
        when(facade.getOneCorrespondingMember(any(CorrespondingMemberRequest.class))).thenReturn(classUnderTest);
        return facade;
    }

    @Test
    public void should_not_mock_dependencies_if_no_class_under_test_found_or_created() throws Exception
    {
        // given
        when(facadeFactory.isTestCase(openCompilationUnit)).thenReturn(true);

        TestCaseTypeFacade facade = classFacadeThatWillFoundClassUnderTest(null);
        when(facadeFactory.createTestCaseFacade(openCompilationUnit)).thenReturn(facade);

        IType testCase = mock(IType.class);
        when(openCompilationUnit.findPrimaryType()).thenReturn(testCase);

        // when
        action.run(anAction);

        // then
        verifyZeroInteractions(pageManager);
    }
}
