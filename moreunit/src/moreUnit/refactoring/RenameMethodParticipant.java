package moreUnit.refactoring;

import java.lang.reflect.InvocationTargetException;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

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
		
		JavaFileFacade javaFileFacade = new JavaFileFacade(method.getCompilationUnit());
		IMethod testMethod = javaFileFacade.getCorrespondingTestMethod(method);
		String neuerTestMethodName = BaseTools.getTestmethodNameFromMethodName(neuerName);
		RenameSupport renameSupport = RenameSupport.create(testMethod, neuerTestMethodName, RenameSupport.UPDATE_REFERENCES);
		//Display default1 = Display.getDefault();
		Shell shell = new Shell();
		try {
			renameSupport.perform(shell, null);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

// $Log$
// Revision 1.1  2006/02/04 21:54:27  gianasista
// Added classes to listen to refactoring (copy and move of classes and methods)
//