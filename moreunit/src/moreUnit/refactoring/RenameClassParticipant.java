package moreUnit.refactoring;

import moreUnit.log.LogHandler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

/**
 * @author vera
 * 04.02.2006 22:19:33
 */
public class RenameClassParticipant extends RenameParticipant{

	protected boolean initialize(Object element) {
		LogHandler.getInstance().handleInfoLog("RenameClassParticipant.initialize");
		return false;
	}

	public String getName() {
		LogHandler.getInstance().handleInfoLog("RenameClassParticipant.getName");
		return null;
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException {
		LogHandler.getInstance().handleInfoLog("RenameClassParticipant.checkConditions");
		return null;
	}

	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		LogHandler.getInstance().handleInfoLog("RenameClassParticipant.createChange");
		return null;
	}
}


// $Log$