package org.moreunit.elements;


import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;

/**
 * @author vera
 *
 * 23.05.2006 20:21:57
 */
public abstract class TypeFacade {

	ICompilationUnit compilationUnit;

	public static boolean isTestCase(IType type) {
		if(type == null) {
			return false;
		}

		IType primaryType = type.getCompilationUnit().findPrimaryType();
		if(primaryType == null) {
			return false;
		}

		String classname = primaryType.getElementName();
		 Preferences preferences = Preferences.getInstance();
		String[] suffixes = preferences.getSuffixes(type.getJavaProject());
		for(String suffix: suffixes) {
			if((suffix.length() > 0) && classname.endsWith(suffix)) {
				return true;
			}
		}

		String[] prefixes = preferences.getPrefixes(type.getJavaProject());
		for(String prefix: prefixes) {
			if((prefix.length() > 0) && classname.startsWith(prefix)) {
				return true;
			}
		}

		return false;
	}

	public TypeFacade(ICompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public TypeFacade(IFile file) {
		this.compilationUnit = JavaCore.createCompilationUnitFrom(file);
	}

	public TypeFacade(IEditorPart editorPart) {
		IFile file = (IFile)editorPart.getEditorInput().getAdapter(IFile.class);
		this.compilationUnit = JavaCore.createCompilationUnitFrom(file);
	}

	public IType getType() {
		return this.compilationUnit.findPrimaryType();
	}
	
	public ICompilationUnit getCompilationUnit() {
		return this.compilationUnit;
	}

	protected boolean doesMethodExist(String testMethodName) {
		try {
			IMethod[] vorhandeneTests = this.compilationUnit.findPrimaryType().getMethods();
			for (IMethod method : vorhandeneTests) {
				if(testMethodName.equals(method.getElementName())) {
					return true;
				}
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}

		return false;
	}
	
	public IJavaProject getJavaProject() {
		return compilationUnit.getJavaProject();
	}

}
