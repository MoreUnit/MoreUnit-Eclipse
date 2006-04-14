package moreUnit.handler;

import moreUnit.elements.EditorPartFacade;
import moreUnit.elements.JavaFileFacade;
import moreUnit.log.LogHandler;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

/**
 * @author vera
 * 25.10.2005
 */
public class MoreUnitActionHandler {
	
	private static MoreUnitActionHandler instance;
	
	private MoreUnitActionHandler() {
	}

	public static MoreUnitActionHandler getInstance() {
		if(instance == null)
			instance = new MoreUnitActionHandler();
		
		return instance;
	}
	
	public void executeCreateTestMethodAction(IEditorPart editorPart) {
		LogHandler.getInstance().handleInfoLog("MoreUnitActionHandler.executeCreateTestMethodAction()");
		
		EditorPartFacade editorPartFacade = new EditorPartFacade(editorPart);
		if(editorPartFacade.getJavaFileFacade().isTestCase()) {
			LogHandler.getInstance().handleInfoLog("The class is already a testcase.");
			return;
		}
			
		IMethod method = editorPartFacade.getMethodUnderCursorPosition();
		if(method == null) {
			LogHandler.getInstance().handleInfoLog("No method found under cursor position");
			return;
		}
		
		IType typeOfTestCaseClassFromJavaFile = editorPartFacade.getJavaFileFacade().getCorrespondingTestCase();
		
		if(typeOfTestCaseClassFromJavaFile == null)
			typeOfTestCaseClassFromJavaFile = editorPartFacade.getJavaFileFacade().createTestCase();
		
		if(typeOfTestCaseClassFromJavaFile != null && typeOfTestCaseClassFromJavaFile.exists())
			(new JavaFileFacade(typeOfTestCaseClassFromJavaFile.getCompilationUnit())).createTestMethodForMethod(method);
		else
			LogHandler.getInstance().handleInfoLog("Es wird keine Testmethode erzeugt");
	}
	
	public void executeJumpToTestAction(IEditorPart editorPart) {
		IType testKlasse = (new JavaFileFacade(editorPart)).getCorrespondingTestCase();
		
		if(testKlasse != null) {
			try {
				IEditorPart openedEditor = JavaUI.openInEditor(testKlasse.getParent());
				jumpToMethodIfPossible(editorPart, openedEditor);
			} catch (PartInitException exc) {
				LogHandler.getInstance().handleExceptionLog(exc);
			} catch (JavaModelException exc) {
				LogHandler.getInstance().handleExceptionLog(exc);
			}
		}
	}
	
	private static void jumpToMethodIfPossible(IEditorPart oldEditorPart, IEditorPart openedEditorPart) {
		EditorPartFacade oldEditorPartFacade = new EditorPartFacade(oldEditorPart);
		IMethod method = (oldEditorPartFacade).getMethodUnderCursorPosition();
		if(method == null)
			return;
		
		IMethod testMethod = oldEditorPartFacade.getFirstTestMethodForMethodUnderCursorPosition();
		if(testMethod != null) 
			JavaUI.revealInEditor(openedEditorPart, (IJavaElement)testMethod);
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.11  2006/03/21 20:59:49  gianasista
// Bugfix JumpToTest
//
// Revision 1.10  2006/02/27 19:55:56  gianasista
// Started hover support
//
// Revision 1.9  2006/01/30 21:12:31  gianasista
// Further Refactorings (moved methods from singleton classes like PluginTools to facade classes)
//
// Revision 1.8  2006/01/28 15:48:25  gianasista
// Moved several methods from PluginTools to EditorPartFacade
//
// Revision 1.7  2006/01/25 21:27:19  gianasista
// First refactoring to smarter code (Replacing util-classes)
//
// Revision 1.6  2006/01/20 21:33:30  gianasista
// Organize Imports
//
// Revision 1.5  2006/01/19 21:38:32  gianasista
// Added CVS-commit-logging to all java-files
//
