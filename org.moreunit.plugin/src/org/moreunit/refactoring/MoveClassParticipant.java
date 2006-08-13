package org.moreunit.refactoring;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.moreunit.log.LogHandler;

/**
 * @author vera
 * 04.02.2006 22:26:18
 */
public class MoveClassParticipant extends MoveParticipant{

	protected boolean initialize(Object element) {
		LogHandler.getInstance().handleInfoLog("MoveClassParticipant.initialize");
		return false;
	}

	public String getName() {
		LogHandler.getInstance().handleInfoLog("MoveClassParticipant.getName");
		return null;
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException {
		LogHandler.getInstance().handleInfoLog("MoveClassParticipant.checkConditions");
		return null;
	}

	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		LogHandler.getInstance().handleInfoLog("MoveClassParticipant.createChange");
		
		// TODO not implemented yet because MoveSupport is not part of the API yet
		return null;
	}
}

// $Log: not supported by cvs2svn $
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