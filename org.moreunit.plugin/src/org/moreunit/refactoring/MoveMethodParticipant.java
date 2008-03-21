package org.moreunit.refactoring;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.MoveMethodDescriptor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.eclipse.ltk.core.refactoring.participants.MoveRefactoring;
import org.moreunit.log.LogHandler;

/**
 * @author vera
 * 04.02.2006 22:29:07
 */
public class MoveMethodParticipant extends MoveParticipant{
	
	IMethod method;
	//JavaFileFacade javaFileFacade;

	protected boolean initialize(Object element) {
		LogHandler.getInstance().handleInfoLog("MoveMethodParticipant.initialize");
		method = (IMethod) element;
		//javaFileFacade = new JavaFileFacade(method.getCompilationUnit());
		return true;
	}

	public String getName() {
		LogHandler.getInstance().handleInfoLog("MoveMethodParticipant.getName");
		return "MoreUnit Move Method";
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException {
		LogHandler.getInstance().handleInfoLog("MoveMethodParticipant.checkConditions");
		return null;
	}

	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		LogHandler.getInstance().handleInfoLog("MoveMethodParticipant.createChange");
			
		MoveMethodDescriptor refactoringDescriptor = (MoveMethodDescriptor) RefactoringCore.getRefactoringContribution(IJavaRefactorings.MOVE_METHOD).createDescriptor();
		//refactoringDescriptor.se
		RefactoringStatus refactoringStatus = new RefactoringStatus();
		refactoringDescriptor.createRefactoring(refactoringStatus);
		//MoveRefactoring moveRefactoring = refactoringContribution.createDescriptor(id, project, description, comment, arguments, flags)
		
		/*
		IMethod testMethod = javaFileFacade.getCorrespondingTestMethod(method);
		if(testMethod == null)
			return null;
		
		MoveProcessor moveProcessor = null;
		MoveRefactoring moveRefactoring = new MoveRefactoring(moveProcessor);
		*/
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
// Revision 1.6  2006/05/23 19:42:01  gianasista
// Splitted JavaFileFacade into two classes
//
// Revision 1.5  2006/05/23 19:39:50  gianasista
// Splitted JavaFileFacade into two classes
//
// Revision 1.4  2006/05/20 16:10:39  gianasista
// todo-comment
//
// Revision 1.3  2006/04/14 19:42:09  gianasista
// *** empty log message ***
//
// Revision 1.2  2006/02/12 20:50:06  gianasista
// Rename refactorings completed for testcases and testmethods
//
// Revision 1.1  2006/02/04 21:54:27  gianasista
// Added classes to listen to refactoring (copy and move of classes and methods)
//