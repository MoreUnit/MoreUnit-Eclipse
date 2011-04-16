package org.moreunit.mock.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.moreunit.mock.DependencyMocker;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.dependencies.DependencyInjectionPointCollector;
import org.moreunit.mock.elements.NamingRules;
import org.moreunit.mock.elements.TypeFacadeFactory;
import org.moreunit.mock.log.Logger;
import org.moreunit.mock.utils.ConversionUtils;
import org.moreunit.util.PluginTools;

import com.google.inject.Inject;

public class MockDependenciesAction extends AbstractHandler implements IEditorActionDelegate
{
    private final DependencyMocker mocker;
    private final ConversionUtils conversionUtils;
    private final TypeFacadeFactory facadeFactory;
    private final Logger logger;
    private ICompilationUnit compilationUnit;

    @Inject
    public MockDependenciesAction(DependencyMocker mocker, ConversionUtils conversionUtils, TypeFacadeFactory facadeFactory, Logger logger)
    {
        this.mocker = mocker;
        this.conversionUtils = conversionUtils;
        this.facadeFactory = facadeFactory;
        this.logger = logger;
    }

    public void setActiveEditor(IAction action, IEditorPart targetEditor)
    {
        compilationUnit = targetEditor == null ? null : conversionUtils.getCompilationUnit(targetEditor);
    }

    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IEditorPart openEditorPart = PluginTools.getOpenEditorPart();
        if(openEditorPart == null)
        {
            return null;
        }

        IFile openFile = conversionUtils.getFile(openEditorPart);
        if(openFile == null)
        {
            return null;
        }

        compilationUnit = JavaCore.createCompilationUnitFrom(openFile);

        execute();
        return null;
    }

    public void run(IAction action)
    {
        execute();
    }

    public void execute()
    {
        if(compilationUnit == null)
        {
            return;
        }

        boolean compilationUnitIsTestCase = facadeFactory.isTestCase(compilationUnit);
        IType classUnderTest = getClassUnderTest(compilationUnit, compilationUnitIsTestCase);
        if(classUnderTest == null) // selection canceled by user
        {
            return;
        }

        IType testCase = getTestCaseType(compilationUnit, compilationUnitIsTestCase);
        if(testCase == null) // selection canceled by user
        {
            return;
        }

        Dependencies dependencies = createDependencies(classUnderTest, testCase.getPackageFragment());
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

    private IType getClassUnderTest(ICompilationUnit editedCompilationUnit, boolean compilationUnitIsTestCase)
    {
        if(compilationUnitIsTestCase)
        {
            return (IType) facadeFactory.createTestCaseFacade(editedCompilationUnit).getOneCorrespondingMember(null, true, false, "Class under test...");
        }
        else
        {
            return editedCompilationUnit.findPrimaryType();
        }
    }

    private IType getTestCaseType(ICompilationUnit editedCompilationUnit, boolean compilationUnitIsTestCase)
    {
        if(compilationUnitIsTestCase)
        {
            return editedCompilationUnit.findPrimaryType();
        }
        else
        {
            return facadeFactory.createClassFacade(editedCompilationUnit).getOneCorrespondingTestCase(true, "Mock dependencies in...");
        }
    }

    protected Dependencies createDependencies(IType classUnderTest, IPackageFragment testCasePackage)
    {
        DependencyInjectionPointCollector collector = new DependencyInjectionPointCollector(classUnderTest, testCasePackage);
        NamingRules namingRules = new NamingRules(classUnderTest.getJavaProject());
        return new Dependencies(classUnderTest, collector, namingRules);
    }

    public void selectionChanged(IAction action, ISelection selection)
    {
        // nothing to do
    }
}
