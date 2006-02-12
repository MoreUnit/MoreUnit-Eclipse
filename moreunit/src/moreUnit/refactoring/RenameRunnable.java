package moreUnit.refactoring;

import java.lang.reflect.InvocationTargetException;

import moreUnit.log.LogHandler;

import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class RenameRunnable implements Runnable {
	
	RenameSupport renameSupport;
	
	public RenameRunnable(RenameSupport renameSupport) {
		this.renameSupport = renameSupport;
	}

	public void run() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorSite().getShell();
		try {
			renameSupport.perform(shell, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		} catch (InterruptedException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		} catch (InvocationTargetException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		}
	}
}

// $Log$