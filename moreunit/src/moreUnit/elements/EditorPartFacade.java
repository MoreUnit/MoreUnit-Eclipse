/**
 * 
 */
package moreUnit.elements;

import moreUnit.MoreUnitPlugin;
import moreUnit.log.LogHandler;
import moreUnit.util.BaseTools;
import moreUnit.util.MagicNumbers;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
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
		String classname = getCompilationUnit().findPrimaryType().getElementName();
		return classname.endsWith(MagicNumbers.TEST_CASE_SUFFIX);
	}
	
	public IType createTestCase() {
		try {
			String paketName = MagicNumbers.EMPTY_STRING;
			IPackageDeclaration[] packageDeclarations = getCompilationUnit().getPackageDeclarations();
			if(packageDeclarations.length > 0) {
				IPackageDeclaration packageDeclaration = packageDeclarations[0];
				paketName = packageDeclaration.getElementName();
			}
			
			IPackageFragmentRoot junitSourceFolder = getJUnitSourceFolder();
			if (junitSourceFolder.exists()) {
				IPackageFragment packageFragment = junitSourceFolder.getPackageFragment(paketName);
				if (!packageFragment.exists()) 
					packageFragment = junitSourceFolder.createPackageFragment(paketName, true, null);
				
				String testCaseClassName = BaseTools.getNameOfTestCaseClass(getFile());
				StringBuffer contents = new StringBuffer();
				if(paketName != null && paketName.length() > 0) {
					contents.append("package " + paketName	+ ";"+MagicNumbers.NEWLINE);
					contents.append(MagicNumbers.NEWLINE);
				}
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
	
	private IPackageFragmentRoot getJUnitSourceFolder() {
		try {
			IPackageFragmentRoot[] packageFragmentRoots = getJavaProject().getPackageFragmentRoots();
			for(int i=0; i<packageFragmentRoots.length; i++) {
				IPackageFragmentRoot packageFragmentRoot = packageFragmentRoots[i];
				String junitFolder = MoreUnitPlugin.getDefault().getJunitDirectoryFromPreferences();
				if(packageFragmentRoot instanceof IPackageFragmentRoot && packageFragmentRoot.getElementName().equals(junitFolder))
					return packageFragmentRoot;
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
		
		return null;
	}

	public boolean createTestMethod() {
		// FIX to be implemented
		return false;
	}
	
	public IType getCorrespondingTestCase() {
		try {
			String klassenName = getCompilationUnit().findPrimaryType().getFullyQualifiedName()+MagicNumbers.TEST_CASE_SUFFIX;
			return getJavaProject().findType(klassenName);
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
	
		return null;
	}
	
	public IMethod getFirstTestMethodForMethodUnderCursorPosition() {
		// FIX to be implemented
		return null;
	}
}


// $Log$
// Revision 1.1  2006/01/25 21:26:30  gianasista
// Started implementing a smarter code
//