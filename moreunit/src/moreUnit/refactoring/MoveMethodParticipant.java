package moreUnit.refactoring;

import moreUnit.elements.JavaFileFacade;
import moreUnit.log.LogHandler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.eclipse.ltk.core.refactoring.participants.MoveProcessor;
import org.eclipse.ltk.core.refactoring.participants.MoveRefactoring;

import com.sun.javadoc.Type;

/**
 * @author vera
 * 04.02.2006 22:29:07
 */
public class MoveMethodParticipant extends MoveParticipant{
	
	IMethod method;
	JavaFileFacade javaFileFacade;

	protected boolean initialize(Object element) {
		LogHandler.getInstance().handleInfoLog("MoveMethodParticipant.initialize");
		method = (IMethod) element;
		javaFileFacade = new JavaFileFacade(method.getCompilationUnit());
		return true;
	}

	public String getName() {
		LogHandler.getInstance().handleInfoLog("MoveMethodParticipant.getName");
		return null;
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException {
		LogHandler.getInstance().handleInfoLog("MoveMethodParticipant.checkConditions");
		return null;
	}

	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		LogHandler.getInstance().handleInfoLog("MoveMethodParticipant.createChange");
		Type targetType = (Type) getArguments().getDestination();
		
		IMethod testMethod = javaFileFacade.getCorrespondingTestMethod(method);
		if(testMethod == null)
			return null;
		
		MoveProcessor moveProcessor = null;
		MoveRefactoring moveRefactoring = new MoveRefactoring(moveProcessor);
		return null;
	}

}


// $Log$
// Revision 1.1  2006/02/04 21:54:27  gianasista
// Added classes to listen to refactoring (copy and move of classes and methods)
//