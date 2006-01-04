package moreUnit.util;


import moreUnit.log.LogHandler;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class PluginTools {
	
	public static IType getTestKlasseVomKlassenNamen(IJavaProject javaProject, String klassenName) {
		try {
			return javaProject.findType(klassenName);
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
	
		return null;
	}
	
	public static IType getTestKlasseVomKlassenNamen(ICompilationUnit compilationUnit) {
		try {
			String klassenName = compilationUnit.findPrimaryType().getFullyQualifiedName()+MagicNumbers.TEST_CASE_SUFFIX;
			return compilationUnit.getJavaProject().findType(klassenName);
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
	
		return null;
	}

	public static IType[] getTestCasesFromJavaProject(IJavaProject javaProject) throws JavaModelException {
		IType testCaseType = javaProject.findType("junit.framework.TestCase");
		if(testCaseType == null)
			return null;

		ITypeHierarchy hierarchy= testCaseType.newTypeHierarchy(javaProject, new NullProgressMonitor());
		IType[] testCaseListe = hierarchy.getAllSubtypes(testCaseType);
		return testCaseListe;
	}

	public static IMethod getMethodUnderCursorPosition(ICompilationUnit compilationUnit, ITextSelection textSelection) {
		IMethod method = null;
		try {
			IJavaElement javaElement = compilationUnit.getElementAt(textSelection.getOffset());
			if(javaElement instanceof IMethod) {
				method = (IMethod) javaElement;
			} else 
				LogHandler.getInstance().handleInfoLog("Keine Methode gefunden.");
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		return method;
	}
	
	public static IType getTypeOfTestCaseClassFromJavaFile(IFile javaFile, IJavaProject javaProject) {
//		try {
			//return javaProject.findType(BaseTools.getNameOfTestCaseClass(javaFile));
			return getTestKlasseVomKlassenNamen(JavaCore.createCompilationUnitFrom(javaFile));
//		} catch (JavaModelException e) {
//			e.printStackTrace();
//		}
		
//		return null;
	}
	
	public static IType createTestCaseClass(IFile classToTest, IJavaProject javaProject, String paketName) {
		try {
			IPackageFragmentRoot junitSourceFolder = getJUnitSourceFolder(javaProject);
			if (junitSourceFolder.exists()) {
				IPackageFragment packageFragment = junitSourceFolder.getPackageFragment(paketName);
				if (!packageFragment.exists()) 
					packageFragment = junitSourceFolder.createPackageFragment(paketName, true, null);
				
				String testCaseClassName = BaseTools.getNameOfTestCaseClass(classToTest);
				StringBuffer contents = new StringBuffer();
				contents.append("package " + paketName	+ ";"+MagicNumbers.NEWLINE);
				contents.append(MagicNumbers.NEWLINE);
				contents.append("import junit.framework.TestCase;"+MagicNumbers.NEWLINE);
				contents.append(MagicNumbers.NEWLINE);
				contents.append("public class " + testCaseClassName + " extends TestCase {"+ MagicNumbers.NEWLINE);
				contents.append(MagicNumbers.NEWLINE);
				contents.append("}");
				ICompilationUnit compilationUnit = packageFragment.createCompilationUnit(testCaseClassName+".java",contents.toString(), true, null);
				return compilationUnit.findPrimaryType();
			} else {
				LogHandler.getInstance().handleInfoLog("junit-Source-Folder konnte nicht gefunden werden.");
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
			
		return null;
	}
	
	public static IEditorPart getOpenEditorPart() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		
		return page.getActiveEditor();
	}
	
	private static IPackageFragmentRoot getJUnitSourceFolder(IJavaProject javaProject) {
		try {
			IPackageFragmentRoot[] packageFragmentRoots = javaProject.getAllPackageFragmentRoots();
			for(int i=0; i<packageFragmentRoots.length; i++) {
				IPackageFragmentRoot packageFragmentRoot = packageFragmentRoots[i];
				if(packageFragmentRoot instanceof IPackageFragmentRoot && packageFragmentRoot.getElementName().equals("junit"))
					return packageFragmentRoot;
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
		
		return null;
	}
	
	public static boolean isTestCase(IType type) {
		String classname = type.getElementName();
		return classname.endsWith(MagicNumbers.TEST_CASE_SUFFIX);
	}
}