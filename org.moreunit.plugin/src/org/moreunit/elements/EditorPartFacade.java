/**
 * 
 */
package org.moreunit.elements;

import java.util.List;


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
import org.moreunit.log.LogHandler;

/**
 * @author vera
 * 25.01.2006 21:50:37
 * 
 * EditorPartFacade offers easy access to {@link IEditorPart}	
 */
public class EditorPartFacade {
	
	private IEditorPart editorPart;
	//private JavaFileFacade javaFileFacade;	
	
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
	
//	public JavaFileFacade getJavaFileFacade() {
//		if(javaFileFacade == null)
//			javaFileFacade = new JavaFileFacade(getCompilationUnit());
//		
//		return javaFileFacade;
//	}
	
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
	
	public IMethod getFirstTestMethodForMethodUnderCursorPosition(IType testcaseType) {
		if(TypeFacade.isTestCase(getCompilationUnit().findPrimaryType()))
			return null;
		
		ClassTypeFacade classTypeFacade = new ClassTypeFacade(getCompilationUnit());
		
		IMethod methodUnderCursorPosition = getMethodUnderCursorPosition();
		return classTypeFacade.getCorrespondingTestMethod(methodUnderCursorPosition, testcaseType);
	}
	
	public List getTestmethodsForMethodUnderCursorPosition() {
		if(TypeFacade.isTestCase(getCompilationUnit().findPrimaryType()))
			return null;
		
		ClassTypeFacade classTypeFacade = new ClassTypeFacade(getCompilationUnit());
		
		IMethod methodUnderCursorPosition = getMethodUnderCursorPosition();
		classTypeFacade.getOneCorrespondingTestCase(false);
		return classTypeFacade.getCorrespondingTestMethods(methodUnderCursorPosition);
	}
}


// $Log: not supported by cvs2svn $
// Revision 1.1.1.1  2006/08/13 14:31:15  gianasista
// initial
//
// Revision 1.1  2006/06/22 20:22:29  gianasista
// package rename
//
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.8  2006/05/23 19:38:01  gianasista
// Splitted JavaFileFacade into two classes
//
// Revision 1.7  2006/05/20 16:05:56  gianasista
// Integration of switchunit preferences
//
// Revision 1.6  2006/05/12 17:52:37  gianasista
// added comments
//
// Revision 1.5  2006/04/14 17:14:22  gianasista
// Refactoring Support with dialog
//
// Revision 1.4  2006/02/19 21:47:44  gianasista
// Deleted unnecessary code
//
// Revision 1.3  2006/01/30 21:12:31  gianasista
// Further Refactorings (moved methods from singleton classes like PluginTools to facade classes)
//
// Revision 1.2  2006/01/28 15:48:25  gianasista
// Moved several methods from PluginTools to EditorPartFacade
//
// Revision 1.1  2006/01/25 21:26:30  gianasista
// Started implementing a smarter code
//