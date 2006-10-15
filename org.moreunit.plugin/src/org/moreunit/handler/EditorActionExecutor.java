package org.moreunit.handler;


import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.moreunit.actions.CreateTestMethodEditorAction;
import org.moreunit.actions.CreateTestMethodHierarchyAction;
import org.moreunit.actions.JumpAction;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.elements.TestCaseTypeFacade;
import org.moreunit.elements.TypeFacade;
import org.moreunit.log.LogHandler;
import org.moreunit.util.BaseTools;
import org.moreunit.wizards.NewClassWizard;
import org.moreunit.wizards.NewTestCaseWizard;


/**
 * Handles the actions which are delegated from the handlers:<br>
 * <ul>
 * <li> key actions like {@link CreateTestMethodActionHandler} and {@link JumpActionHandler} </li>
 * <li> menu action provided by the popup menu in the editor like {@link CreateTestMethodEditorAction} and
 * {@link JumpAction} </li>
 * <li> menu action provided by the popup menu in the package explorer like {@link CreateTestMethodHierarchyAction}
 * </li>
 * </ul>
 * The handler is a singelton.
 * 
 * @author vera 25.10.2005
 */
public class EditorActionExecutor {

	private static EditorActionExecutor	instance;

	private EditorActionExecutor() {
	}

	public static EditorActionExecutor getInstance() {
		if (instance == null)
			instance = new EditorActionExecutor();

		return instance;
	}

	public void executeCreateTestMethodAction(IEditorPart editorPart) {
		LogHandler.getInstance().handleInfoLog("MoreUnitActionHandler.executeCreateTestMethodAction()");

		EditorPartFacade editorPartFacade = new EditorPartFacade(editorPart);
		if(TypeFacade.isTestCase(editorPartFacade.getCompilationUnit().findPrimaryType()))
			createAnotherTestMethod(editorPartFacade);
		else 
			createFirstTestMethod(editorPartFacade);
	}

	private void createFirstTestMethod(EditorPartFacade editorPartFacade) {
		IMethod method = editorPartFacade.getMethodUnderCursorPosition();
		if (method == null) {
			LogHandler.getInstance().handleInfoLog("No method found under cursor position");
			return;
		}
		ClassTypeFacade classTypeFacade = new ClassTypeFacade(method.getCompilationUnit());

		IType typeOfTestCaseClassFromJavaFile = classTypeFacade.getOneCorrespondingTestCase();

		if (typeOfTestCaseClassFromJavaFile == null)
			typeOfTestCaseClassFromJavaFile = new NewTestCaseWizard(classTypeFacade.getType()).open();

		if (typeOfTestCaseClassFromJavaFile != null && typeOfTestCaseClassFromJavaFile.exists())
			(new TestCaseTypeFacade(typeOfTestCaseClassFromJavaFile.getCompilationUnit())).createTestMethodForMethod(method);
		else
			LogHandler.getInstance().handleInfoLog("No testmethod is created.");
	}
	
	private void createAnotherTestMethod(EditorPartFacade testCaseTypeFacade) {
		IMethod method = testCaseTypeFacade.getMethodUnderCursorPosition();
		if(method == null) {
			LogHandler.getInstance().handleInfoLog("No method found under cursor position");
			return;
		}
		
		TestCaseTypeFacade testFacade = new TestCaseTypeFacade(method.getCompilationUnit());
		
	}
	
	public void executeJumpAction(IEditorPart editorPart) {
		IFile file = (IFile) editorPart.getEditorInput().getAdapter(IFile.class);
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
		executeJumpAction(editorPart, compilationUnit);
	}

	public void executeJumpAction(ICompilationUnit compilationUnit) {
		executeJumpAction(null, compilationUnit);
	}

	private void executeJumpAction(IEditorPart editorPart, ICompilationUnit compilationUnit) {
		if (TypeFacade.isTestCase(compilationUnit.findPrimaryType()))
			executeJumpFromTest(editorPart, new TestCaseTypeFacade(compilationUnit));
		else
			executeJumpToTest(editorPart, new ClassTypeFacade(compilationUnit));
	}

	private void executeJumpFromTest(IEditorPart editorPart, TestCaseTypeFacade javaFileFacade) {
		IType classUnderTest = javaFileFacade.getCorrespondingClassUnderTest();
		if (classUnderTest == null) {
			classUnderTest = new NewClassWizard(javaFileFacade.getType()).open();
		}
		if (classUnderTest != null)
			try {
				IEditorPart openedEditorPart = JavaUI.openInEditor(classUnderTest.getParent());
				if (editorPart != null) {
					jumpToMethodUnderTestIfPossible(classUnderTest, editorPart, openedEditorPart);
				}
			} catch (PartInitException exc) {
				LogHandler.getInstance().handleExceptionLog(exc);
			} catch (JavaModelException exc) {
				LogHandler.getInstance().handleExceptionLog(exc);
			}
	}

	private void executeJumpToTest(IEditorPart editorPart, ClassTypeFacade javaFileFacade) {
		IType testcaseToJump = javaFileFacade.getOneCorrespondingTestCase();
		if (testcaseToJump == null)
			testcaseToJump = new NewTestCaseWizard(javaFileFacade.getType()).open();

		if (testcaseToJump != null) {
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

	private void jumpToTestMethodIfPossible(IEditorPart oldEditorPart, IEditorPart openedEditorPart) {
		EditorPartFacade oldEditorPartFacade = new EditorPartFacade(oldEditorPart);
		IMethod method = (oldEditorPartFacade).getMethodUnderCursorPosition();
		if (method == null)
			return;

		IMethod testMethod = oldEditorPartFacade.getFirstTestMethodForMethodUnderCursorPosition();
		if (testMethod != null)
			JavaUI.revealInEditor(openedEditorPart, (IJavaElement) testMethod);
	}

	private void jumpToMethodUnderTestIfPossible(IType classUnderTest, IEditorPart oldEditorPart, IEditorPart openedEditorPart) throws JavaModelException {
		EditorPartFacade oldEditorPartFacade = new EditorPartFacade(oldEditorPart);
		IMethod methode = (oldEditorPartFacade).getMethodUnderCursorPosition();
		if (methode == null)
			return;

		String testedMethodName = BaseTools.getTestedMethod(methode.getElementName());
		if (testedMethodName != null) {
			IMethod[] foundTestMethods = classUnderTest.getMethods();
			for (int i = 0; i < foundTestMethods.length; i++) {
				IMethod method = foundTestMethods[i];
				if (testedMethodName.startsWith(method.getElementName()) && method.exists()) {
					JavaUI.revealInEditor(openedEditorPart, (IJavaElement) method);
					return;
				}
			}
		}

	}
}

// $Log: not supported by cvs2svn $
// Revision 1.2  2006/10/02 18:22:23  channingwalton
// added actions for jumping from views. added some tests for project properties. improved some of the text
//
// Revision 1.1.1.1  2006/08/13 14:31:16  gianasista
// initial
//
// Revision 1.1  2006/06/22 20:22:29  gianasista
// package rename
//
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.2  2006/05/23 19:39:15  gianasista
// Splitted JavaFileFacade into two classes
//
// Revision 1.1  2006/05/20 16:08:21  gianasista
// Rename of MoreUnitActionHandler, new name EditorActionExecutor
//
// Revision 1.18  2006/05/18 06:57:48  channingwalton
// fixed some warnings and deprecated APIs
//
// Revision 1.17  2006/05/15 19:50:42  gianasista
// removed deprecated method call
//
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
