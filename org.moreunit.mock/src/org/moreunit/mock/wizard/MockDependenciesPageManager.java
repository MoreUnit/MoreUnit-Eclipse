package org.moreunit.mock.wizard;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.core.log.Logger;
import org.moreunit.mock.DependencyMocker;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.dependencies.DependencyInjectionPointCollector;
import org.moreunit.mock.dependencies.DependencyInjectionPointProvider;
import org.moreunit.mock.dependencies.DependencyInjectionPointProviderWrapper;
import org.moreunit.mock.dependencies.DependencyInjectionPointStore;
import org.moreunit.mock.elements.NamingRules;

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

    public MockDependenciesWizardPage createPage(IType classUnderTest, IPackageFragment testCasePackage)
    {
        DependencyInjectionPointProvider provider = new DependencyInjectionPointCollector(classUnderTest, testCasePackage);

        try
        {
            provider = new DependencyInjectionPointProviderWrapper(provider);

            DependencyInjectionPointStore injectionPointStore = new DependencyInjectionPointStore(provider, logger);

            return wizardFactory.createMockDependenciesWizardPage(classUnderTest, provider, injectionPointStore);
        }
        catch (JavaModelException e)
        {
            logger.error("Could not determine dependencies to mock for " + classUnderTest.getElementName(), e);
        }

        return null;
    }

    public void pageValidated(MockDependenciesWizardPage page, IType testCase)
    {
        page.validated();

        IType classUnderTest = page.getClassUnderTest();
        DependencyInjectionPointProvider injectionPointStore = page.getInjectionPointStore();

        NamingRules namingRules = new NamingRules(classUnderTest.getJavaProject());
        Dependencies dependencies = new Dependencies(classUnderTest, injectionPointStore, namingRules);
        try
        {
            dependencies.init();
        }
        catch (JavaModelException e)
        {
            // MSG
            logger.error("Could not determine dependencies to mock for " + classUnderTest.getElementName());
            return;
        }

        mocker.mockDependencies(dependencies, classUnderTest, testCase);
    }

    public void openWizard(IType classUnderTest, IType testCase)
    {
        MockDependenciesWizardPage page = createPage(classUnderTest, testCase.getPackageFragment());

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
            pageValidated(page, testCase);
        }
    }

    protected MockDependenciesWizard newWizard(MockDependenciesWizardPage page)
    {
        return wizardFactory.createMockDependenciesWizard(page);
    }
}
