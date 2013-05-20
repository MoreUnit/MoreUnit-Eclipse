package org.moreunit.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.RenameJavaElementDescriptor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.moreunit.core.util.StringConstants;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.TypeFacade;
import org.moreunit.log.LogHandler;

/**
 * @author vera 04.02.2006 22:19:33
 */
public class RenameClassParticipant extends RenameParticipant
{

    private ICompilationUnit compilationUnit;
    private ClassTypeFacade javaFileFacade;

    protected boolean initialize(Object element)
    {
        compilationUnit = (ICompilationUnit) element;
        if(TypeFacade.isTestCase(compilationUnit.findPrimaryType()))
            return false;

        javaFileFacade = new ClassTypeFacade(compilationUnit);
        return true;
    }

    public String getName()
    {
        return "MoreUnit Rename Class";
    }

    public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException
    {
        LogHandler.getInstance().handleInfoLog("RenameClassParticipant.checkConditions");
        return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException
    {
        if(! getArguments().getUpdateReferences())
        {
            return null;
        }

        try
        {
            List<Change> changes = new ArrayList<Change>();
            RefactoringContribution refactoringContribution = RefactoringCore.getRefactoringContribution(IJavaRefactorings.RENAME_COMPILATION_UNIT);
            for (IType typeToRename : javaFileFacade.getCorrespondingTestCases())
            {
                RenameJavaElementDescriptor renameJavaElementDescriptor = (RenameJavaElementDescriptor) refactoringContribution.createDescriptor();
                renameJavaElementDescriptor.setJavaElement(typeToRename.getCompilationUnit());
                renameJavaElementDescriptor.setNewName(getNewTestName(typeToRename));
                RefactoringStatus refactoringStatus = new RefactoringStatus();
                Refactoring renameRefactoring = renameJavaElementDescriptor.createRefactoring(refactoringStatus);
                // RefactoringStatus checkAllConditions =
                // renameRefactoring.checkAllConditions(pm);
                renameRefactoring.checkAllConditions(pm);
                changes.add(renameRefactoring.createChange(pm));
            }

            if(changes.size() == 1)
            {
                return changes.get(0);
            }

            if(changes.size() > 0)
            {
                return new CompositeChange(getName(), changes.toArray(new Change[changes.size()]));
            }
        }
        catch (Exception e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }

        return null;
    }

    private String getNewTestName(IType typeToRename)
    {
        String newName = getArguments().getNewName();
        newName = newName.replaceFirst("\\.[^\\.]*$", StringConstants.EMPTY_STRING);
        return typeToRename.getElementName().replaceFirst(compilationUnit.findPrimaryType().getElementName(), newName);
    }

}
