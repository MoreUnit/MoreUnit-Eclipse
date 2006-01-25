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
import org.eclipse.jdt.core.IType;
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
	
	IEditorPart editorPart;
	
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
	
	public boolean isTestCase() {
		// FIX to be implemented
		return false;
	}
	
	public boolean createTestCase() {
		// FIX to be implemented
		return false;
	}
	
	public boolean createTestMethod() {
		// FIX to be implemented
		return false;
	}
	
	public IType getCorrespondingTestCase() {
		// FIX to be implemented
		return null;
	}
	
	public IMethod getFirstTestMethodForMethodUnderCursorPosition() {
		// FIX to be implemented
		return null;
	}
}


// $Log$