package moreUnit.listener;

import moreUnit.log.LogHandler;
import moreUnit.util.MarkerTools;
import moreUnit.util.PluginTools;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;

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
		ICompilationUnit compilationUnit = (ICompilationUnit) element;
		IType type = compilationUnit.findPrimaryType();
		if(PluginTools.isTestCase(type)) {
			try {
				MarkerTools.createMarkerForTestedClass(element.getJavaProject(), type);
			} catch (CoreException exc) {
				LogHandler.getInstance().handleExceptionLog(exc);
			}
		}
	}
}