package org.moreunit.mock.actions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.ClassTypeFacade.CorrespondingTestCase;
import org.moreunit.elements.CorrespondingMemberRequest;
import org.moreunit.elements.TestCaseTypeFacade;
import org.moreunit.mock.elements.TypeFacadeFactory;
import org.moreunit.mock.util.ConversionUtils;
import org.moreunit.mock.wizard.MockDependenciesPageManager;

public class MockDependenciesActionTest
{
    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Mock
    private MockDependenciesPageManager pageManager;
    @Mock
    private ConversionUtils conversionUtils;
    @Mock
    private TypeFacadeFactory facadeFactory;
    @Mock
    private ICompilationUnit openCompilationUnit;

    private MockDependenciesAction action;

    private final IAction anAction = null;

    @BeforeEach
    public void createAction() throws Exception
    {
        action = new MockDependenciesAction(pageManager, conversionUtils, facadeFactory);

        final IEditorPart activeEditor = mock(IEditorPart.class);
        when(conversionUtils.getCompilationUnit(activeEditor)).thenReturn(openCompilationUnit);
        action.setActiveEditor(null, activeEditor);
    }

    @Test
    public void should_not_mock_dependencies_if_no_test_case_found_or_created() throws Exception
    {
        // given
        when(facadeFactory.isTestCase(openCompilationUnit)).thenReturn(false);

        final IType classUnderTest = mock(IType.class);
        when(openCompilationUnit.findPrimaryType()).thenReturn(classUnderTest);

        final ClassTypeFacade facade = classFacadeThatWillFindTestCase(null);
        when(facadeFactory.createClassFacade(openCompilationUnit)).thenReturn(facade);

        // when
        action.run(anAction);

        // then
        verifyNoInteractions(pageManager);
    }

    private ClassTypeFacade classFacadeThatWillFindTestCase(IType testCase)
    {
        final ClassTypeFacade facade = mock(ClassTypeFacade.class);
        when(facade.getOneCorrespondingTestCase(eq(true), anyString())).thenReturn(new CorrespondingTestCase(testCase, false));
        return facade;
    }

    @Test
    public void should_mock_dependencies_if_test_case_found_or_created() throws Exception
    {
        // given
        when(facadeFactory.isTestCase(openCompilationUnit)).thenReturn(false);

        final IType classUnderTest = mock(IType.class);
        when(openCompilationUnit.findPrimaryType()).thenReturn(classUnderTest);

        final IType testCase = mock(IType.class);
        final ClassTypeFacade facade = classFacadeThatWillFindTestCase(testCase);
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

        final IType classUnderTest = mock(IType.class);
        final TestCaseTypeFacade facade = classFacadeThatWillFoundClassUnderTest(classUnderTest);
        when(facadeFactory.createTestCaseFacade(openCompilationUnit)).thenReturn(facade);

        final IType testCase = mock(IType.class);
        when(openCompilationUnit.findPrimaryType()).thenReturn(testCase);

        // when
        action.run(anAction);

        // then
        verify(pageManager).openWizard(eq(classUnderTest), eq(testCase));
    }

    private TestCaseTypeFacade classFacadeThatWillFoundClassUnderTest(IType classUnderTest)
    {
        final TestCaseTypeFacade facade = mock(TestCaseTypeFacade.class);
        when(facade.getOneCorrespondingMember(any(CorrespondingMemberRequest.class))).thenReturn(classUnderTest);
        return facade;
    }

    @Test
    public void should_not_mock_dependencies_if_no_class_under_test_found_or_created() throws Exception
    {
        // given
        when(facadeFactory.isTestCase(openCompilationUnit)).thenReturn(true);

        final TestCaseTypeFacade facade = classFacadeThatWillFoundClassUnderTest(null);
        when(facadeFactory.createTestCaseFacade(openCompilationUnit)).thenReturn(facade);

        final IType testCase = mock(IType.class);
        when(openCompilationUnit.findPrimaryType()).thenReturn(testCase);

        // when
        action.run(anAction);

        // then
        verifyNoInteractions(pageManager);
    }
}
