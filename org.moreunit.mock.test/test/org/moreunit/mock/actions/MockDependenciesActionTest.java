package org.moreunit.mock.actions;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.TestCaseTypeFacade;
import org.moreunit.mock.DependencyMocker;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.elements.TypeFacadeFactory;
import org.moreunit.mock.log.Logger;
import org.moreunit.mock.utils.ConversionUtils;

@RunWith(MockitoJUnitRunner.class)
public class MockDependenciesActionTest
{
    @Mock
    private DependencyMocker dependencyMocker;
    @Mock
    private ConversionUtils conversionUtils;
    @Mock
    private TypeFacadeFactory facadeFactory;
    @Mock
    private Logger logger;

    @Mock
    private ICompilationUnit openCompilationUnit;
    @Mock
    private Dependencies dependencies;

    private MockDependenciesAction action;

    private IAction anAction = null;
    private IPackageFragment testCasePackageUsedToCreateDependencies;

    @Before
    public void createAction() throws Exception
    {
        action = new MockDependenciesAction(dependencyMocker, conversionUtils, facadeFactory, logger)
        {
            protected Dependencies createDependencies(IType classUnderTest, IPackageFragment testCasePackage)
            {
                testCasePackageUsedToCreateDependencies = testCasePackage;
                return dependencies;
            }
        };

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

        ClassTypeFacade facade = classFacadeThatWillFoundTestCase(null);
        when(facadeFactory.createClassFacade(openCompilationUnit)).thenReturn(facade);

        // when
        action.run(anAction);

        // then
        verifyZeroInteractions(dependencyMocker);
    }

    private ClassTypeFacade classFacadeThatWillFoundTestCase(IType testCase)
    {
        ClassTypeFacade facade = mock(ClassTypeFacade.class);
        when(facade.getOneCorrespondingTestCase(eq(true), anyString())).thenReturn(testCase);
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
        ClassTypeFacade facade = classFacadeThatWillFoundTestCase(testCase);
        when(facadeFactory.createClassFacade(openCompilationUnit)).thenReturn(facade);

        // when
        action.run(anAction);

        // then
        verify(dependencyMocker).mockDependencies(any(Dependencies.class), eq(classUnderTest), eq(testCase));
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
        verify(dependencyMocker).mockDependencies(any(Dependencies.class), eq(classUnderTest), eq(testCase));
    }

    private TestCaseTypeFacade classFacadeThatWillFoundClassUnderTest(IType classUnderTest)
    {
        TestCaseTypeFacade facade = mock(TestCaseTypeFacade.class);
        when(facade.getOneCorrespondingMember(eq((IMethod) null), eq(true), anyBoolean(), anyString())).thenReturn(classUnderTest);
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
        verifyZeroInteractions(dependencyMocker);
    }

    private IType mockClassUnderTestAndTestCaseRetrieval()
    {
        when(facadeFactory.isTestCase(openCompilationUnit)).thenReturn(false);

        IType classUnderTest = mock(IType.class);
        when(openCompilationUnit.findPrimaryType()).thenReturn(classUnderTest);

        IType testCase = mock(IType.class);
        IPackageFragment packageFragment = mock(IPackageFragment.class);
        when(testCase.getPackageFragment()).thenReturn(packageFragment);

        ClassTypeFacade facade = classFacadeThatWillFoundTestCase(testCase);
        when(facadeFactory.createClassFacade(openCompilationUnit)).thenReturn(facade);

        return testCase;
    }

    @Test
    public void should_log_error_and_abort_when_dependency_computation_fails() throws Exception
    {
        // given
        mockClassUnderTestAndTestCaseRetrieval();
        doThrow(new JavaModelException(new RuntimeException("Test exception"), 0)).when(dependencies).init();

        // when
        action.run(anAction);

        // then
        verify(logger).error(any());
        verifyZeroInteractions(dependencyMocker);
    }

    @Test
    public void should_pass_test_case_package_to_dependencies() throws Exception
    {
        // given
        IType testCase = mockClassUnderTestAndTestCaseRetrieval();

        // when
        action.run(anAction);

        // then
        assertThat(testCasePackageUsedToCreateDependencies).isSameAs(testCase.getPackageFragment());
    }
}
