/**
 * 
 */
package moreUnit.elements;

import moreUnit.log.LogHandler;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * @author vera
 *
 * 25.01.2006 21:50:37
 */
public class EditorPartFacade {
	
	private IEditorPart editorPart;
	private JavaFileFacade javaFileFacade;	
	
	public EditorPartFacade(IEditorPart editorPart) {
		this.editorPart = editorPart;
	}
	
	public IFile getFile() {
		return (IFile)editorPart.getEditorInput().getAdapter(IFile.class);
	}
	
	public ICompilationUnit getCompilationUnit() {
		return JavaCore.createCompilationUnitFrom(getFile());
	}
	
	public ITextSelection getTextSelection() {
		IWorkbenchPartSite site = editorPart.getSite();
		ISelectionProvider selectionProvider = site.getSelectionProvider();
		return (ITextSelection) selectionProvider.getSelection();
	}
	
	public JavaFileFacade getJavaFileFacade() {
		if(javaFileFacade == null)
			javaFileFacade = new JavaFileFacade(getCompilationUnit());
		
		return javaFileFacade;
	}
	
	public IMethod getMethodUnderCursorPosition() {
		IMethod method = null;
		try {
			IJavaElement javaElement = getCompilationUnit().getElementAt(getTextSelection().getOffset());
			if(javaElement instanceof IMethod) {
				method = (IMethod) javaElement;
			} else 
				LogHandler.getInstance().handleInfoLog("No method found.");
		} catch (JavaModelException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		}
		
		return method;
	}
	
	public IJavaProject getJavaProject() {
		return getCompilationUnit().getJavaProject();
	}
	
	public IMethod getFirstTestMethodForMethodUnderCursorPosition() {
		IMethod methodUnderCursorPosition = getMethodUnderCursorPosition();
		return getJavaFileFacade().getCorrespondingTestMethod(methodUnderCursorPosition);
	}
}


// $Log$
// Revision 1.2  2006/01/28 15:48:25  gianasista
// Moved several methods from PluginTools to EditorPartFacade
//
// Revision 1.1  2006/01/25 21:26:30  gianasista
// Started implementing a smarter code
//