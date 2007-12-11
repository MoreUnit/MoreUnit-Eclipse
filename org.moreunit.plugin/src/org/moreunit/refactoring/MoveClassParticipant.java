package org.moreunit.refactoring;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
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
import org.moreunit.elements.JavaProjectFacade;
import org.moreunit.log.LogHandler;

/**
 * @author vera
 * 04.02.2006 22:26:18
 */
public class MoveClassParticipant extends MoveParticipant{
	
	private static final String EMPTY_CONTENT = "";
	
	private ICompilationUnit compilationUnit;
	private ClassTypeFacade javaFileFacade;

	protected boolean initialize(Object element) {
		LogHandler.getInstance().handleInfoLog("MoveClassParticipant.initialize2");
		compilationUnit = (ICompilationUnit) element;
		javaFileFacade = new ClassTypeFacade(compilationUnit);
		return true;
	}

	public String getName() {
		LogHandler.getInstance().handleInfoLog("MoveClassParticipant.getName");
		return "MoreUnit testcase move operation";
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException {
		LogHandler.getInstance().handleInfoLog("MoveClassParticipant.checkConditions");
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		LogHandler.getInstance().handleInfoLog("MoveClassParticipant.createChange");
		IPackageFragment moveClassDestinationPackage = (IPackageFragment) getArguments().getDestination();
		
		IPackageFragment moveTestsDestinationPackage = getMoveTestsDestinationPackage(moveClassDestinationPackage);
		RefactoringContribution refactoringContribution = RefactoringCore.getRefactoringContribution(IJavaRefactorings.MOVE);
		
		List<Change> changes = new ArrayList<Change>();
		Set<IType> allTestcases = javaFileFacade.getCorrespondingTestCaseList();
		for (IType typeToRename : allTestcases) {
			ICompilationUnit[] members = new ICompilationUnit[1];
			members[0] = typeToRename.getCompilationUnit();
			ICompilationUnit newType = moveTestsDestinationPackage.createCompilationUnit(members[0].getElementName(), EMPTY_CONTENT, true, pm);
			
			MoveDescriptor moveDescriptor = createMoveDescriptor(refactoringContribution, typeToRename, members, newType);
			RefactoringStatus refactoringStatus = new RefactoringStatus();
			Refactoring createRefactoring = moveDescriptor.createRefactoring(refactoringStatus);
			createRefactoring.checkAllConditions(pm);
			Change createChange = createRefactoring.createChange(null);
			changes.add(createChange);
		}
		
		if (changes.size() == 1) {
			return changes.get(0);
		}
		
		if (changes.size() > 0) {
			return new CompositeChange(getName(), changes.toArray(new Change[changes.size()]));
		}

		return null;
	}
	
	private MoveDescriptor createMoveDescriptor(RefactoringContribution refactoringContribution, IType typeToRename, ICompilationUnit[] members, ICompilationUnit newType) {
		MoveDescriptor moveDescriptor = (MoveDescriptor) refactoringContribution.createDescriptor();
		
		if(moveDescriptor == null)
			moveDescriptor = new MoveDescriptor();
		
		moveDescriptor.setDestination(newType);
		moveDescriptor.setMoveResources(new IFile[] {}, new IFolder[] {}, members);
		moveDescriptor.setUpdateQualifiedNames(true);
		moveDescriptor.setUpdateReferences(true);
		
		return moveDescriptor;
	}

	private IPackageFragment getMoveTestsDestinationPackage(IPackageFragment moveClassDestinationPackage) {
		IPackageFragmentRoot unitSourceFolder = (new JavaProjectFacade(moveClassDestinationPackage.getJavaProject())).getJUnitSourceFolder();
		return unitSourceFolder.getPackageFragment(moveClassDestinationPackage.getElementName());
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.4  2007/10/29 06:40:22  gianasista
// Move refactoring via move descriptor
//
// Revision 1.3  2007/09/19 19:19:43  gianasista
// Started move refactoring support
//
// Revision 1.2  2006/10/24 18:37:39  channingwalton
// made  the properties page appear on the navigator view and fixed some gui text
//
// Revision 1.1.1.1  2006/08/13 14:31:16  gianasista
// initial
//
// Revision 1.1  2006/06/22 20:22:28  gianasista
// package rename
//
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.2  2006/05/20 16:10:22  gianasista
// todo-comment
//
// Revision 1.1  2006/02/04 21:54:27  gianasista
// Added classes to listen to refactoring (copy and move of classes and methods)
//