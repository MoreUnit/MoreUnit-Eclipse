package org.moreunit.mock.wizard;

import org.eclipse.jdt.core.IType;
import org.moreunit.mock.dependencies.DependencyInjectionPointProvider;

public interface MockDependenciesWizardValues
{
    IType getClassUnderTest();

    DependencyInjectionPointProvider getInjectionPointProvider();
}
