package moreUnit.handler;

import moreUnit.log.LogHandler;
import moreUnit.util.CodeTools;
import moreUnit.util.MagicNumbers;
import moreUnit.util.PluginTools;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;
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
		
		IFile file = (IFile)editorPart.getEditorInput().getAdapter(IFile.class);
		
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
		IWorkbenchPartSite site = editorPart.getSite();
		ISelectionProvider selectionProvider = site.getSelectionProvider();
		ITextSelection structuredSelection = (ITextSelection) selectionProvider.getSelection();
		
		IMethod method = PluginTools.getMethodUnderCursorPosition(compilationUnit, structuredSelection);
		try {
			if(method != null) {
				IType typeOfTextCaseClassFromJavaFile = PluginTools.getTypeOfTestCaseClassFromJavaFile(file, compilationUnit.getJavaProject());
				if(typeOfTextCaseClassFromJavaFile == null) {
					IJavaProject javaProject = compilationUnit.getJavaProject();
					IPackageDeclaration[] packageDeclarations = compilationUnit.getPackageDeclarations();
					if(packageDeclarations.length > 0) {
						IPackageDeclaration packageDeclaration = packageDeclarations[0];
						String paketName = packageDeclaration.getElementName();
						typeOfTextCaseClassFromJavaFile = PluginTools.createTestCaseClass(file, javaProject, paketName);
					}
				} 
				if(typeOfTextCaseClassFromJavaFile != null && typeOfTextCaseClassFromJavaFile.exists())
					CodeTools.addTestCaseMethod(method, typeOfTextCaseClassFromJavaFile);
				else
					LogHandler.getInstance().handleInfoLog("Es wird keine Testmethode erzeugt");
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
	}
	
	public void executeJumpToTestAction(IEditorPart editorPart) {
		IFile file = (IFile)editorPart.getEditorInput().getAdapter(IFile.class);
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
		
		String klassenName = compilationUnit.findPrimaryType().getFullyQualifiedName();
		IType testKlasse = PluginTools.getTestKlasseVomKlassenNamen(compilationUnit.getJavaProject(), klassenName+MagicNumbers.TEST_CASE_SUFFIX);
		
		if(testKlasse != null) {
			try {
				JavaUI.openInEditor(testKlasse.getParent());
			} catch (PartInitException exc) {
				LogHandler.getInstance().handleExceptionLog(exc);
			} catch (JavaModelException exc) {
				LogHandler.getInstance().handleExceptionLog(exc);
			}
		}
	}
}
