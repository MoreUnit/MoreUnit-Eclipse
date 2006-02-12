package moreUnit.refactoring;

import moreUnit.elements.JavaFileFacade;
import moreUnit.log.LogHandler;
import moreUnit.util.BaseTools;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.ui.PlatformUI;

/**
 * @author vera
 * 04.02.2006 22:19:33
 */
public class RenameClassParticipant extends RenameParticipant{
	
	ICompilationUnit compilationUnit;
	JavaFileFacade javaFileFacade;

	protected boolean initialize(Object element) {
		LogHandler.getInstance().handleInfoLog("RenameClassParticipant.initialize");
		compilationUnit = (ICompilationUnit) element;
		javaFileFacade = new JavaFileFacade(compilationUnit);
		return !javaFileFacade.isTestCase();
	}

	public String getName() {
		LogHandler.getInstance().handleInfoLog("RenameClassParticipant.getName");
		return "MoreUnitRenameClassParticipant";
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException {
		LogHandler.getInstance().handleInfoLog("RenameClassParticipant.checkConditions");
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		LogHandler.getInstance().handleInfoLog("RenameClassParticipant.createChange");
		String classnameAfterRename = getArguments().getNewName();
		IType testcaseType = javaFileFacade.getCorrespondingTestCase();
		
		if(testcaseType == null)
			return null;
		
		String testcaseNameAfterRename = BaseTools.getNameOfTestCaseClass(classnameAfterRename);
		RenameSupport renameSupport = RenameSupport.create(testcaseType.getCompilationUnit(), testcaseNameAfterRename, RenameSupport.UPDATE_REFERENCES);
		PlatformUI.getWorkbench().getDisplay().asyncExec(new RenameRunnable(renameSupport));
		return null;
	}
}


// $Log$
// Revision 1.1  2006/02/04 21:54:27  gianasista
// Added classes to listen to refactoring (copy and move of classes and methods)
//