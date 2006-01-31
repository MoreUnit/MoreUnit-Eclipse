package moreUnit.listener;

import moreUnit.elements.JavaFileFacade;
import moreUnit.log.LogHandler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;

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
				JavaFileFacade javaFileFacade = new JavaFileFacade((ICompilationUnit) element);
				javaFileFacade.createMarkerForTestedClass();
			} catch (CoreException exc) {
				LogHandler.getInstance().handleExceptionLog(exc);
			}
		}
	}
	
	private boolean isTestCaseChanged(IJavaElement element) {
		JavaFileFacade javaFileFacade = new JavaFileFacade((ICompilationUnit)element);
		return javaFileFacade.isTestCase();
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.3  2006/01/30 21:12:32  gianasista
// Further Refactorings (moved methods from singleton classes like PluginTools to facade classes)
//
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//