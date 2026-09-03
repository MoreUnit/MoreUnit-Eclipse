package org.moreunit.mock.wizard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.moreunit.core.log.Logger;
import org.moreunit.extensionpoints.INewTestCaseWizardContext;
import org.moreunit.mock.DependencyMocker;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.dependencies.DependencyInjectionPointProviderCache;
import org.moreunit.mock.dependencies.DependencyInjectionPointStore;

public class MockDependenciesPageManagerTest
{
    @Mock
    private WizardFactory wizardFactory;
    @Mock
    private DependencyMocker mocker;
    @Mock
    private Logger logger;

    @Mock
    private INewTestCaseWizardContext context;
    @Mock
    private MockDependenciesWizardPage page;
    @Mock
    private MockDependenciesWizard wizard;
    @Mock
    private IType classUnderTest;
    @Mock
    private IType testCase;
    @Mock
    private IPackageFragment testCasePackage;
    @Mock
    private IJavaProject javaProject;

    private MockDependenciesPageManager pageManager;

    @BeforeEach
    public void setUp()
    {
        MockitoAnnotations.openMocks(this);
        pageManager = new MockDependenciesPageManager(wizardFactory, mocker, logger);
    }

    @Test
    public void should_create_page_with_wizard_values_built_from_context() throws Exception
    {
        // given
        when(classUnderTest.getMethods()).thenReturn(new IMethod[0]);
        when(classUnderTest.getFields()).thenReturn(new IField[0]);
        final ITypeHierarchy typeHierarchy = mock(ITypeHierarchy.class);
        when(typeHierarchy.getAllClasses()).thenReturn(new IType[0]);
        when(classUnderTest.newSupertypeHierarchy(any(IProgressMonitor.class))).thenReturn(typeHierarchy);
        when(context.getClassUnderTest()).thenReturn(classUnderTest);
        when(context.getTestCasePackage()).thenReturn(testCasePackage);
        when(wizardFactory.createMockDependenciesWizardPage(any(MockDependenciesWizardValues.class), any(DependencyInjectionPointStore.class))).thenReturn(page);

        // when
        final MockDependenciesWizardPage createdPage = pageManager.createPage(context);

        // then
        assertSame(page, createdPage);

        final ArgumentCaptor<MockDependenciesWizardValues> valuesCaptor = ArgumentCaptor.forClass(MockDependenciesWizardValues.class);
        verify(wizardFactory).createMockDependenciesWizardPage(valuesCaptor.capture(), any(DependencyInjectionPointStore.class));

        final MockDependenciesWizardValues wizardValues = valuesCaptor.getValue();
        assertSame(classUnderTest, wizardValues.getClassUnderTest());

        assertNotNull(wizardValues.getInjectionPointProvider());
        assertEquals(DependencyInjectionPointProviderCache.class, wizardValues.getInjectionPointProvider().getClass());
    }

    @Test
    public void should_open_wizard_with_page_created_for_class_under_test()
    {
        // given
        when(testCase.getPackageFragment()).thenReturn(testCasePackage);
        when(wizardFactory.createMockDependenciesWizardPage(any(MockDependenciesWizardValues.class), any(DependencyInjectionPointStore.class))).thenReturn(page);
        when(wizardFactory.createMockDependenciesWizard(page)).thenReturn(wizard);
        when(wizard.openAndReturnIfOk()).thenReturn(false);

        // when
        pageManager.openWizard(classUnderTest, testCase);

        // then
        verify(wizard).openAndReturnIfOk();
        verify(page, never()).validated();
        verifyNoInteractions(mocker);
    }

    @Test
    public void should_log_wizard_opening_when_debug_is_enabled()
    {
        // given
        when(testCase.getPackageFragment()).thenReturn(testCasePackage);
        when(wizardFactory.createMockDependenciesWizardPage(any(MockDependenciesWizardValues.class), any(DependencyInjectionPointStore.class))).thenReturn(page);
        when(wizardFactory.createMockDependenciesWizard(page)).thenReturn(wizard);
        when(wizard.openAndReturnIfOk()).thenReturn(false);
        when(logger.debugEnabled()).thenReturn(true);

        // when
        pageManager.openWizard(classUnderTest, testCase);

        // then
        verify(logger).debug("Opening MockDependenciesWizard...");
    }

    @Test
    public void should_validate_page_and_mock_dependencies_when_page_is_validated()
    {
        // given
        when(page.getClassUnderTest()).thenReturn(classUnderTest);
        when(classUnderTest.getJavaProject()).thenReturn(javaProject);
        when(page.getInjectionPointStore()).thenReturn(new DependencyInjectionPointStore(logger));

        // when
        pageManager.pageValidated(page, testCase, "TestNG");

        // then
        verify(page).validated();
        verify(mocker).mockDependencies(any(Dependencies.class), eq(classUnderTest), eq(testCase), eq("TestNG"));
    }

    @Test
    public void should_log_error_and_not_mock_dependencies_when_dependencies_cannot_be_determined() throws Exception
    {
        // given
        final DependencyInjectionPointStore injectionPointStore = mock(DependencyInjectionPointStore.class);
        when(injectionPointStore.getConstructors()).thenThrow(new JavaModelException(new CoreException(Status.CANCEL_STATUS)));

        when(page.getClassUnderTest()).thenReturn(classUnderTest);
        when(classUnderTest.getJavaProject()).thenReturn(javaProject);
        when(page.getInjectionPointStore()).thenReturn(injectionPointStore);

        // when
        pageManager.pageValidated(page, testCase, "TestNG");

        // then
        verify(logger).error(startsWith("Could not determine dependencies to mock"));
        verifyNoInteractions(mocker);
    }
}
