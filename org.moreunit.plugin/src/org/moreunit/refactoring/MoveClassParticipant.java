package org.moreunit.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.MoveDescriptor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;

/**
 * @author vera 04.02.2006 22:26:18
 */
public class MoveClassParticipant extends MoveParticipant
{

    private static final String EMPTY_CONTENT = "";

    private ICompilationUnit compilationUnit;
    private ClassTypeFacade javaFileFacade;

    @Override
    protected boolean initialize(Object element)
    {
        compilationUnit = (ICompilationUnit) element;
        javaFileFacade = new ClassTypeFacade(compilationUnit);

        return !ClassTypeFacade.isTestCase(compilationUnit);
    }

    @Override
    public String getName()
    {
        return "MoreUnit testcase move operation";
    }

    @Override
    public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException
    {
        return new RefactoringStatus();
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException
    {
        if(! (getArguments().getDestination() instanceof IPackageFragment))
        {
            return null;
        }

        try
        {
            IPackageFragment moveClassDestinationPackage = (IPackageFragment) getArguments().getDestination();

            IPackageFragment moveTestsDestinationPackage = getMoveTestsDestinationPackage(moveClassDestinationPackage);
            if(moveTestsDestinationPackage == null)
            {
                return null;
            }

            RefactoringContribution refactoringContribution = RefactoringCore.getRefactoringContribution(IJavaRefactorings.MOVE);

            List<Change> changes = new ArrayList<Change>();
            IPackageDeclaration packageDeclaration = javaFileFacade.getCompilationUnit().getPackageDeclarations()[0];
            String importString = String.format("%s.%s", packageDeclaration.getElementName(), javaFileFacade.getCompilationUnit().findPrimaryType().getElementName());

            for (IType typeToMove : javaFileFacade.getCorrespondingTestCases())
            {
                // fix https://sourceforge.net/p/moreunit/bugs/141/
                // if CUT is moved to a different source folder and the test
                // stays in the same test source folder -> don't do anything
                // with this testcase
                if(moveTestsDestinationPackage.equals(typeToMove.getPackageFragment()))
                {
                    continue;
                }

                ICompilationUnit[] members = new ICompilationUnit[1];
                members[0] = typeToMove.getCompilationUnit();
                ICompilationUnit newType = moveTestsDestinationPackage.createCompilationUnit(members[0].getElementName(), EMPTY_CONTENT, true, pm);

                MoveDescriptor moveDescriptor = createMoveDescriptor(refactoringContribution, typeToMove, members, newType);
                RefactoringStatus refactoringStatus = new RefactoringStatus();
                Refactoring createRefactoring = moveDescriptor.createRefactoring(refactoringStatus);
                createRefactoring.checkAllConditions(pm);
                Change createChange = createRefactoring.createChange(null);
                changes.add(createChange);

                // Because of bug
                // https://sourceforge.net/tracker/?func=detail&aid=3191142&group_id=156007&atid=798056
                // we need to check if there is already an import for the CUT in the tests
                // If not, the moveDescriptor creates a new (wrong) import for the CUT,
                // to fix this, we remove this wrong import from the testcase
                if(!typeToMove.getCompilationUnit().getImport(importString).exists())
                {
                    changes.add(new CutImportChange(importString, newType));
                }
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

    private MoveDescriptor createMoveDescriptor(RefactoringContribution refactoringContribution, IType typeToRename, ICompilationUnit[] members, ICompilationUnit newType)
    {
        MoveDescriptor moveDescriptor = (MoveDescriptor) refactoringContribution.createDescriptor();

        if(moveDescriptor == null)
            moveDescriptor = new MoveDescriptor();

        moveDescriptor.setDestination(newType);
        moveDescriptor.setMoveResources(new IFile[] {}, new IFolder[] {}, members);
        moveDescriptor.setUpdateQualifiedNames(true);
        moveDescriptor.setUpdateReferences(true);

        return moveDescriptor;
    }

    private IPackageFragment getMoveTestsDestinationPackage(IPackageFragment moveClassDestinationPackage)
    {
        IPackageFragmentRoot unitSourceFolder = Preferences.getInstance().getTestSourceFolder(moveClassDestinationPackage.getJavaProject(), (IPackageFragmentRoot) moveClassDestinationPackage.getParent());
        if(unitSourceFolder == null || ! unitSourceFolder.exists())
        {
            return null;
        }

        IPackageFragment packageFragment = unitSourceFolder.getPackageFragment(moveClassDestinationPackage.getElementName());
        if(packageFragment != null && ! packageFragment.exists())
        {
            try
            {
                packageFragment = unitSourceFolder.createPackageFragment(moveClassDestinationPackage.getElementName(), false, null);
            }
            catch (JavaModelException e)
            {
                LogHandler.getInstance().handleExceptionLog(e);
            }
        }
        return packageFragment;
    }
}