package org.moreunit.refactoring;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.MoveDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.MoveMethodDescriptor;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.corext.refactoring.reorg.JavaMoveProcessor;
import org.eclipse.jdt.internal.corext.refactoring.reorg.ReorgPolicyFactory;
import org.eclipse.jdt.internal.corext.refactoring.reorg.ReorgUtils;
import org.eclipse.jdt.internal.corext.refactoring.reorg.IReorgPolicy.IMovePolicy;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.eclipse.ltk.core.refactoring.participants.MoveRefactoring;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.log.LogHandler;

/**
 * @author vera
 * 04.02.2006 22:26:18
 */
public class MoveClassParticipant extends MoveParticipant{
	
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
		return "MoreUnit Move Class";
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException {
		LogHandler.getInstance().handleInfoLog("MoveClassParticipant.checkConditions");
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		LogHandler.getInstance().handleInfoLog("MoveClassParticipant.createChange");
		IPackageFragment moveToPackage = (IPackageFragment) getArguments().getDestination();
		
		Set<IType> allTestcases = javaFileFacade.getCorrespondingTestCaseList();
		for (IType typeToRename : allTestcases) {			
			//changes.add(new RenameClassChange(typeToRename, getNewTestName(typeToRename)));
		}
		
//		if (changes.size() == 1) {
//			return changes.get(0);
//		}
		
//		if (changes.size() > 0) {
//			return new CompositeChange(getName(), changes.toArray(new Change[changes.size()]));
//		}
//		List elementList = new ArrayList();
//		elementList.add(allTestcases.iterator().next());
//		IResource[] resources = ReorgUtils.getResources(elementList);
//		IJavaElement[] elements = ReorgUtils.getJavaElements(elementList);
//		IMovePolicy policy = ReorgPolicyFactory.createMovePolicy(resources, elements);
//		JavaMoveProcessor processor = new JavaMoveProcessor(policy);
//		MoveRefactoring refactoring = new MoveRefactoring(processor); 
//		//IReorgQueries yourQueries; 
//		//processor.setReorgQueries(yourQueries);
//		// yours option here
//		processor.setDestination(moveToPackage);
//		processor.setUpdateReferences(true);
//		processor.setUpdateQualifiedNames(true);
//		RefactoringStatus status = refactoring.checkAllConditions(null);
//		if (!status.hasFatalError()) {
//		    Change change = refactoring.createChange(null);
//		    change.initializeValidationData(null);
//		    change.perform(null);
//		    return change;
//		} 
		
		RefactoringContribution refactoringContribution = RefactoringCore.getRefactoringContribution(IJavaRefactorings.MOVE);
		//MoveDescriptor refactoringDescriptor = (MoveDescriptor) refactoringContribution.createDescriptor("id", compilationUnit.getJavaProject().getElementName(), "des", "com", new HashMap(), 0);
		MoveDescriptor moveDescriptor = new MoveDescriptor();
		moveDescriptor.setDestination(moveToPackage);
		IMember[] members = new IMember[1];
		members[0] = allTestcases.iterator().next();
		moveDescriptor.setMoveMembers(members);
		moveDescriptor.setUpdateQualifiedNames(true);
		moveDescriptor.setUpdateReferences(true);
		RefactoringStatus refactoringStatus = new RefactoringStatus();
		moveDescriptor.createRefactoring(refactoringStatus).createChange(null);
		
		return null;
	}
}

// $Log: not supported by cvs2svn $
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