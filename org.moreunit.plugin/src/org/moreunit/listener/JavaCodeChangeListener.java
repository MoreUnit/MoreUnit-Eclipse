package org.moreunit.listener;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.moreunit.elements.TestCaseTypeFacade;
import org.moreunit.elements.TypeFacade;
import org.moreunit.log.LogHandler;

/**
 * @author vera
 * 30.10.2005
 */
public class JavaCodeChangeListener implements IElementChangedListener {

	public void elementChanged(ElementChangedEvent event) {
		int type = event.getDelta().getElement().getElementType();
		switch(type) {
			case(IJavaElement.COMPILATION_UNIT): handleTypeCompilationUnit(event.getDelta().getElement()); break;
			default: {}
		}
	}

	private void handleTypeCompilationUnit(IJavaElement element) {
		if(isTestCaseChanged(element)) {
			try {
				TestCaseTypeFacade javaFileFacade = new TestCaseTypeFacade((ICompilationUnit) element);
				javaFileFacade.createMarkerForTestedClass();
			} catch (CoreException exc) {
				LogHandler.getInstance().handleExceptionLog(exc);
			}
		}
	}
	
	private boolean isTestCaseChanged(IJavaElement element) {
		return TypeFacade.isTestCase(((ICompilationUnit)element).findPrimaryType());
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.1  2006/06/22 20:22:28  gianasista
// package rename
//
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.5  2006/05/23 19:39:30  gianasista
// Splitted JavaFileFacade into two classes
//
// Revision 1.4  2006/01/31 19:06:04  gianasista
// Refactored MarkerTools and added methods to corresponding facade classes.
//
// Revision 1.3  2006/01/30 21:12:32  gianasista
// Further Refactorings (moved methods from singleton classes like PluginTools to facade classes)
//
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//