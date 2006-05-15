package moreUnit.handler;

import java.util.Set;

import moreUnit.elements.EditorPartFacade;
import moreUnit.elements.JavaFileFacade;
import moreUnit.log.LogHandler;
import moreUnit.ui.TypeChoiceDialog;
import moreUnit.util.BaseTools;
import moreUnit.wizards.NewClassWizard;
import moreUnit.wizards.NewTestCaseWizard;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;


/**
 * Handles the actions which get delegates from several action and handler
 * classes.
 * 
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
		
		IType typeOfTestCaseClassFromJavaFile = editorPartFacade.getJavaFileFacade().getOneCorrespondingTestCase();
		
		if(typeOfTestCaseClassFromJavaFile == null)
			typeOfTestCaseClassFromJavaFile = editorPartFacade.getJavaFileFacade().createTestCase();
		
		if(typeOfTestCaseClassFromJavaFile != null && typeOfTestCaseClassFromJavaFile.exists())
			(new JavaFileFacade(typeOfTestCaseClassFromJavaFile.getCompilationUnit())).createTestMethodForMethod(method);
		else
			LogHandler.getInstance().handleInfoLog("No testmethod is created.");
	}
	
	public void executeJumpAction(IEditorPart editorPart) {
		JavaFileFacade javaFileFacade = new JavaFileFacade(editorPart);
		if(javaFileFacade.isTestCase())
			executeJumpFromTest(editorPart, javaFileFacade);
		else
			executeJumpToTest(editorPart, javaFileFacade);
	}
	
	private void executeJumpFromTest(IEditorPart editorPart, JavaFileFacade javaFileFacade) {
		IType classUnderTest = javaFileFacade.getCorrespondingClassUnderTest();
		if (classUnderTest == null) {
			classUnderTest = new NewClassWizard(javaFileFacade.getType()).open();
		}
		if(classUnderTest != null)
			try {
				IEditorPart openedEditorPart = JavaUI.openInEditor(classUnderTest.getParent());
				jumpToMethodUnderTestIfPossible(classUnderTest, editorPart, openedEditorPart);
			} catch (PartInitException exc) {
				LogHandler.getInstance().handleExceptionLog(exc);
			} catch (JavaModelException exc) {
				LogHandler.getInstance().handleExceptionLog(exc);
			}
	}
	
	private void executeJumpToTest(IEditorPart editorPart, JavaFileFacade javaFileFacade) {
		IType testcaseToJump = javaFileFacade.getOneCorrespondingTestCase();
		if(testcaseToJump == null)
			testcaseToJump = new NewTestCaseWizard(javaFileFacade.getType()).open();

		if(testcaseToJump != null) {
			try {
				IEditorPart openedEditor = JavaUI.openInEditor(testcaseToJump.getParent());
				jumpToTestMethodIfPossible(editorPart, openedEditor);
			} catch (PartInitException exc) {
				LogHandler.getInstance().handleExceptionLog(exc);
			} catch (JavaModelException exc) {
				LogHandler.getInstance().handleExceptionLog(exc);
			}
		}		
	}
	
	private static void jumpToTestMethodIfPossible(IEditorPart oldEditorPart, IEditorPart openedEditorPart) {
		EditorPartFacade oldEditorPartFacade = new EditorPartFacade(oldEditorPart);
		IMethod method = (oldEditorPartFacade).getMethodUnderCursorPosition();
		if(method == null)
			return;
		
		IMethod testMethod = oldEditorPartFacade.getFirstTestMethodForMethodUnderCursorPosition();
		if(testMethod != null) 
			JavaUI.revealInEditor(openedEditorPart, (IJavaElement)testMethod);
	}
	
	private static void jumpToMethodUnderTestIfPossible(IType classUnderTest, IEditorPart oldEditorPart, IEditorPart openedEditorPart) throws JavaModelException {
		EditorPartFacade oldEditorPartFacade = new EditorPartFacade(oldEditorPart);
		IMethod methode = (oldEditorPartFacade).getMethodUnderCursorPosition();
		if(methode == null)
			return;
		
		String testedMethodName = BaseTools.getTestedMethod(methode.getElementName());
		if(testedMethodName != null) {
			IMethod[] foundTestMethods = classUnderTest.getMethods();
			for(int i=0; i<foundTestMethods.length; i++) {
				IMethod method = foundTestMethods[i];
				if(testedMethodName.startsWith(method.getElementName()) && method.exists()) {
					JavaUI.revealInEditor(openedEditorPart, (IJavaElement)method);
					return;
				}
			}
		}

	}
}

// $Log: not supported by cvs2svn $
// Revision 1.16  2006/05/14 19:08:57  gianasista
// JumpToTest uses TypeChoiceDialog
//
// Revision 1.15  2006/05/12 22:33:42  channingwalton
// added class creation wizards if type to jump to does not exist
//
// Revision 1.14  2006/05/12 17:53:07  gianasista
// added comments
//
// Revision 1.13  2006/04/21 05:57:17  gianasista
// Feature: Jump from testcase back to class under test
//
// Revision 1.12  2006/04/14 17:14:22  gianasista
// Refactoring Support with dialog
//
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
