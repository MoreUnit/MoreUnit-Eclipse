package moreUnit.refactoring;

import moreUnit.log.LogHandler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;

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
		return null;
	}
}

// $Log$