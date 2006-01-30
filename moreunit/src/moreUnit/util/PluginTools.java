package moreUnit.util;


import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class PluginTools {
	
	public static IEditorPart getOpenEditorPart() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		
		return page.getActiveEditor();
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.8  2006/01/28 15:48:24  gianasista
// Moved several methods from PluginTools to EditorPartFacade
//
// Revision 1.7  2006/01/25 21:25:16  gianasista
// getMethodUnderCursorPosition is deprecated, new class EditorPartFacade implements this functionality now
//
// Revision 1.6  2006/01/22 20:53:32  gianasista
// Bugfix: Testcase in wrong java project (sometimes)
//
// Revision 1.5  2006/01/19 21:38:32  gianasista
// Added CVS-commit-logging to all java-files
//