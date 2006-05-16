package moreUnit.refactoring;

import java.util.Iterator;
import java.util.Set;

import moreUnit.elements.JavaFileFacade;
import moreUnit.log.LogHandler;
import moreUnit.util.BaseTools;
import moreUnit.util.MagicNumbers;

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
		Set<IType> allTestcases = javaFileFacade.getCorrespondingTestCaseList();
		
		if(allTestcases == null || allTestcases.size() == 0)
			return null;
		
		for (Iterator iterator = allTestcases.iterator(); iterator.hasNext();) {
			IType typeToRename = (IType) iterator.next();
			String testcaseNameAfterRename = getNewTestName(typeToRename);
			RenameSupport renameSupport = RenameSupport.create(typeToRename.getCompilationUnit(), testcaseNameAfterRename, RenameSupport.UPDATE_REFERENCES);
			PlatformUI.getWorkbench().getDisplay().asyncExec(new RenameRunnable(renameSupport, getDialogMessage(typeToRename.getElementName(), testcaseNameAfterRename)));
		}
		return null;
	}
	
	private String getNewTestName(IType typeToRename) {
		String newName = getArguments().getNewName();
		newName = newName.replaceFirst(MagicNumbers.JAVA_FILE_EXTENSION, MagicNumbers.EMPTY_STRING);
		return typeToRename.getElementName().replaceFirst(compilationUnit.findPrimaryType().getElementName(), newName).concat(MagicNumbers.JAVA_FILE_EXTENSION);
	}
	
	private String getDialogMessage(String originalTestclass, String renamedTestclass) {
		StringBuffer result = new StringBuffer();
		
		result.append("moreUnit found a corresponding testcase.\n");
		result.append("Do you wish to rename ");
		result.append(originalTestclass);
		result.append(" to ");
		result.append(renamedTestclass);
		result.append(" as well?");
		
		return result.toString();
	}
}


// $Log$
// Revision 1.5  2006/05/15 19:51:09  gianasista
// added todo
//
// Revision 1.4  2006/05/13 08:26:54  channingwalton
// corrected the english
//
// Revision 1.3  2006/02/19 21:46:33  gianasista
// Dialog to ask user of refactoring should be performed on corresponding tests (configurable via properties)
//
// Revision 1.2  2006/02/12 20:50:06  gianasista
// Rename refactorings completed for testcases and testmethods
//
// Revision 1.1  2006/02/04 21:54:27  gianasista
// Added classes to listen to refactoring (copy and move of classes and methods)
//