package org.moreunit.mock.wizard;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.core.log.Logger;
import org.moreunit.extensionpoints.INewTestCaseWizardContext;
import org.moreunit.mock.DependencyMocker;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.dependencies.DependencyInjectionPointCollector;
import org.moreunit.mock.dependencies.DependencyInjectionPointProvider;
import org.moreunit.mock.dependencies.DependencyInjectionPointProviderCache;
import org.moreunit.mock.dependencies.DependencyInjectionPointStore;
import org.moreunit.mock.elements.NamingRules;
import org.moreunit.preferences.Preferences;

public class MockDependenciesPageManager
{
    private final WizardFactory wizardFactory;
    private final DependencyMocker mocker;
    private final Logger logger;

    public MockDependenciesPageManager(WizardFactory wizardFactory, DependencyMocker mocker, Logger logger)
    {
        this.wizardFactory = wizardFactory;
        this.mocker = mocker;
        this.logger = logger;
    }

    public MockDependenciesWizardPage createPage(final INewTestCaseWizardContext context)
    {
        final DependencyInjectionPointStore injectionPointStore = new DependencyInjectionPointStore(logger);
        return wizardFactory.createMockDependenciesWizardPage(new MockDependenciesWizardValues()
        {
            @Override
            public IType getClassUnderTest()
            {
                return context.getClassUnderTest();
            }

            @Override
            public DependencyInjectionPointProvider getInjectionPointProvider()
            {
                final DependencyInjectionPointProvider provider = new DependencyInjectionPointCollector(context.getClassUnderTest(), context.getTestCasePackage());
                return new DependencyInjectionPointProviderCache(provider);
            }
        }, injectionPointStore);
    }

    private MockDependenciesWizardPage createPage(final IType classUnderTest, final IPackageFragment testCasePackage)
    {
        final DependencyInjectionPointStore injectionPointStore = new DependencyInjectionPointStore(logger);

        return wizardFactory.createMockDependenciesWizardPage(new MockDependenciesWizardValues()
        {
            @Override
            public IType getClassUnderTest()
            {
                return classUnderTest;
            }

            @Override
            public DependencyInjectionPointProvider getInjectionPointProvider()
            {
                final DependencyInjectionPointCollector provider = new DependencyInjectionPointCollector(classUnderTest, testCasePackage);
                return new DependencyInjectionPointProviderCache(provider);
            }
        }, injectionPointStore);
    }

    public void pageValidated(MockDependenciesWizardPage page, IType testCase, String testType)
    {
        page.validated();

        final IType classUnderTest = page.getClassUnderTest();
        final DependencyInjectionPointStore injectionPointStore = page.getInjectionPointStore();

        final NamingRules namingRules = new NamingRules(classUnderTest.getJavaProject());
        final Dependencies dependencies = new Dependencies(classUnderTest, injectionPointStore, namingRules);
        try
        {
            dependencies.init();
        }
        catch (final JavaModelException e)
        {
            // MSG
            logger.error("Could not determine dependencies to mock for " + classUnderTest.getElementName());
            return;
        }

        mocker.mockDependencies(dependencies, classUnderTest, testCase, testType);
    }

    public void openWizard(IType classUnderTest, IType testCase)
    {
        final MockDependenciesWizardPage page = createPage(classUnderTest, testCase.getPackageFragment());

        if(logger.debugEnabled())
        {
            logger.debug("Opening MockDependenciesWizard...");
        }

        if(newWizard(page).openAndReturnIfOk())
        {
            if(logger.debugEnabled())
            {
                logger.debug("User confirmed mocking of dependencies");
            }
            final String testType = Preferences.forProject(classUnderTest.getJavaProject()).getTestType();
            pageValidated(page, testCase, testType);
        }
    }

    protected MockDependenciesWizard newWizard(MockDependenciesWizardPage page)
    {
        return wizardFactory.createMockDependenciesWizard(page);
    }
}
