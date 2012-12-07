package org.moreunit.mock.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.ClassTypeFacade.CorrespondingTestCase;
import org.moreunit.elements.CorrespondingMemberRequest;
import org.moreunit.elements.CorrespondingMemberRequest.MemberType;
import org.moreunit.mock.elements.TypeFacadeFactory;
import org.moreunit.mock.util.ConversionUtils;
import org.moreunit.mock.wizard.MockDependenciesPageManager;
import org.moreunit.util.PluginTools;

import static org.moreunit.elements.CorrespondingMemberRequest.newCorrespondingMemberRequest;
import static org.moreunit.mock.config.MockModule.$;

public class MockDependenciesAction extends AbstractHandler implements IEditorActionDelegate
{
    private final MockDependenciesPageManager pageManager;
    private final ConversionUtils conversionUtils;
    private final TypeFacadeFactory facadeFactory;
    private ICompilationUnit compilationUnit;

    public MockDependenciesAction()
    {
        this($().getMockDependenciesPageManager(), $().getConversionUtils(), $().getTypeFacadeFactory());
    }

    public MockDependenciesAction(MockDependenciesPageManager pageManager, ConversionUtils conversionUtils, TypeFacadeFactory facadeFactory)
    {
        this.pageManager = pageManager;
        this.conversionUtils = conversionUtils;
        this.facadeFactory = facadeFactory;
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

    private void execute()
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

        // selection canceled by user, or test case created during the call (in
        // which case the user could use the "New Test Case" wizard's page
        // dedicated to the mocking of the dependencies)
        if(testCase == null)
        {
            return;
        }

        pageManager.openWizard(classUnderTest, testCase);
    }

    private IType getClassUnderTest(ICompilationUnit editedCompilationUnit, boolean compilationUnitIsTestCase)
    {
        if(compilationUnitIsTestCase)
        {
            CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
            .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
            .createClassIfNoResult("Class under test...") //
            .build();

            return (IType) facadeFactory.createTestCaseFacade(editedCompilationUnit).getOneCorrespondingMember(request);
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
            ClassTypeFacade classFacade = facadeFactory.createClassFacade(editedCompilationUnit);
            CorrespondingTestCase testCase = classFacade.getOneCorrespondingTestCase(true, "Mock dependencies in...");
            return testCase.hasJustBeenCreated() ? null : testCase.get();
        }
    }

    public void selectionChanged(IAction action, ISelection selection)
    {
        // nothing to do
    }
}
