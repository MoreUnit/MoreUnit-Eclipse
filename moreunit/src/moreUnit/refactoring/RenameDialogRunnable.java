package moreUnit.refactoring;

import java.util.List;

import moreUnit.elements.ClassTypeFacade;
import moreUnit.elements.MethodContentProvider;
import moreUnit.log.LogHandler;
import moreUnit.util.BaseTools;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author vera
 * 01.04.2006 13:02:18
 */
public class RenameDialogRunnable implements Runnable {
	ClassTypeFacade javaFile;
	IMethod renamedMethod;
	String newMethodName;
	
	public RenameDialogRunnable(ClassTypeFacade javaFile, IMethod renamedMethod, String newMethodName) {
		this.javaFile = javaFile;
		this.renamedMethod = renamedMethod;
		this.newMethodName = newMethodName;
	}

	public void run() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorSite().getShell();
		
		List corrspondingTests = javaFile.getCorrespondingTestMethods(renamedMethod);
		ListDialog listDialog = new ListDialog(shell);
		listDialog.setMessage("Should the following methods be renamed either?");
		listDialog.setTitle("Rename tests");
		listDialog.setLabelProvider(new JavaElementLabelProvider());
		listDialog.setContentProvider(new MethodContentProvider(corrspondingTests));
		listDialog.setInput(this);
		
		if(listDialog.open() == ListDialog.OK) {
			for(int i=0; i<corrspondingTests.size(); i++) {
				try {
					IMethod testMethod = (IMethod) corrspondingTests.get(i);
					String testMethodNameAfterRename = BaseTools.getTestMethodNameAfterRename(renamedMethod.getElementName(), newMethodName,	testMethod.getElementName());
					RenameSupport renameSupport = RenameSupport.create(testMethod, testMethodNameAfterRename, RenameSupport.UPDATE_REFERENCES);
					renameSupport.perform(shell, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
				} catch (Exception	e) {
					LogHandler.getInstance().handleExceptionLog(e);
				}				
			}
		}
	}

}


// $Log: not supported by cvs2svn $
// Revision 1.1  2006/04/14 17:14:22  gianasista
// Refactoring Support with dialog
//