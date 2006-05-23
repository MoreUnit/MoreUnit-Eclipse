package moreUnit.refactoring;

import java.util.Set;

import moreUnit.elements.ClassTypeFacade;
import moreUnit.elements.JavaFileFacade;
import moreUnit.elements.TypeFacade;
import moreUnit.log.LogHandler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
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
	ClassTypeFacade javaFileFacade;

	protected boolean initialize(Object element) {
		LogHandler.getInstance().handleInfoLog("RenameMethodParticipant.initialize");
		method = (IMethod) element;
		if(TypeFacade.isTestCase(method.getCompilationUnit().findPrimaryType()))
			return false;
		
		javaFileFacade = new ClassTypeFacade(method.getCompilationUnit());
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
		String methodNameAfterRename = getArguments().getNewName();
		
		Set<IType> allTestcases = javaFileFacade.getCorrespondingTestCaseList();
		
		for(IType aTestCaseType: allTestcases) {
			IMethod testMethod = javaFileFacade.getCorrespondingTestMethod(method, aTestCaseType);
			
			if(testMethod == null)
				return null;
			
			PlatformUI.getWorkbench().getDisplay().asyncExec(new RenameDialogRunnable(javaFileFacade, method, methodNameAfterRename));
		}
		
		
		return null;
	}
}

// $Log$
// Revision 1.6  2006/05/20 16:11:00  gianasista
// Integration of switchunit preferences
//
// Revision 1.5  2006/04/14 17:14:22  gianasista
// Refactoring Support with dialog
//
// Revision 1.4  2006/02/19 21:46:21  gianasista
// *** empty log message ***
//
// Revision 1.3  2006/02/12 20:50:06  gianasista
// Rename refactorings completed for testcases and testmethods
//
// Revision 1.2  2006/02/05 21:14:34  gianasista
// First try to delegate refactoring to rename testmethods
//
// Revision 1.1  2006/02/04 21:54:27  gianasista
// Added classes to listen to refactoring (copy and move of classes and methods)
//