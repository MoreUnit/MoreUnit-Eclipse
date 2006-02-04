package moreUnit.refactoring;

import moreUnit.log.LogHandler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

/**
 * @author vera
 * 04.02.2006 21:55:31
 */
public class RenameMethodParticipant extends RenameParticipant{
	
	IMethod method;

	protected boolean initialize(Object element) {
		LogHandler.getInstance().handleInfoLog("RenameMethodParticipant.initialize");
		method = (IMethod) element;
		return true;
	}

	public String getName() {
		LogHandler.getInstance().handleInfoLog("RenameMethodParticipant.getName");
		return "MoreUnitRenameMethodParticipant";
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException {
		LogHandler.getInstance().handleInfoLog("RenameMethodParticipant.checkConditions");
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		LogHandler.getInstance().handleInfoLog("RenameMethodParticipant.createChange");
		String alterName = method.getElementName();
		String neuerName = getArguments().getNewName();
		return null;
	}
}

// $Log$