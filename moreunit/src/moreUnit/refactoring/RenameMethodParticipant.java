package moreUnit.refactoring;

import moreUnit.elements.JavaFileFacade;
import moreUnit.log.LogHandler;
import moreUnit.util.BaseTools;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.ui.PlatformUI;

/**
 * @author vera
 * 04.02.2006 21:55:31
 */
public class RenameMethodParticipant extends RenameParticipant{
	
	IMethod method;
	JavaFileFacade javaFileFacade;

	protected boolean initialize(Object element) {
		LogHandler.getInstance().handleInfoLog("RenameMethodParticipant.initialize");
		method = (IMethod) element;
		javaFileFacade = new JavaFileFacade(method.getCompilationUnit());
		return !javaFileFacade.isTestCase();
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
		String methodNameAfterRename = getArguments().getNewName();
		IMethod testMethod = javaFileFacade.getCorrespondingTestMethod(method);
		
		if(testMethod == null)
			return null;
		
		String testMethodNameAfterRename = BaseTools.getTestmethodNameFromMethodName(methodNameAfterRename);
		RenameSupport renameSupport = RenameSupport.create(testMethod, testMethodNameAfterRename, RenameSupport.UPDATE_REFERENCES);
		PlatformUI.getWorkbench().getDisplay().asyncExec(new RenameRunnable(renameSupport));
		return null;
	}
}

// $Log$
// Revision 1.2  2006/02/05 21:14:34  gianasista
// First try to delegate refactoring to rename testmethods
//
// Revision 1.1  2006/02/04 21:54:27  gianasista
// Added classes to listen to refactoring (copy and move of classes and methods)
//