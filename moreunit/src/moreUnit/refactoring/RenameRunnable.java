package moreUnit.refactoring;

import java.lang.reflect.InvocationTargetException;

import moreUnit.MoreUnitPlugin;
import moreUnit.log.LogHandler;

import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class RenameRunnable implements Runnable {
	
	RenameSupport renameSupport;
	String dialogMessage;
	
	public RenameRunnable(RenameSupport renameSupport, String dialogMessage) {
		this.renameSupport = renameSupport;
		this.dialogMessage = dialogMessage;
	}

	public void run() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorSite().getShell();
		if(!shouldPerformRefactoringAfterDialoag(shell))
			return;
		
		try {
			renameSupport.perform(shell, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		} catch (InterruptedException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		} catch (InvocationTargetException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		}
	}
	
	private boolean shouldPerformRefactoringAfterDialoag(Shell shell) {
		if(!MoreUnitPlugin.getDefault().getShowRefactoringDialogFromPreferences())
			return true;
		
		return MessageDialog.openQuestion(shell, "moreUnit Action", dialogMessage);
	}
}

// $Log$
// Revision 1.1  2006/02/12 20:50:06  gianasista
// Rename refactorings completed for testcases and testmethods
//