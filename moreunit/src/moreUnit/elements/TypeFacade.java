package moreUnit.elements;

import moreUnit.log.LogHandler;
import moreUnit.preferences.Preferences;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;

/**
 * @author vera
 *
 * 23.05.2006 20:21:57
 */
public abstract class TypeFacade {
	
	ICompilationUnit compilationUnit;
	JavaProjectFacade javaProjectFacade;
	
	public static boolean isTestCase(IType type) {
		if(type == null)
			return false;
		
		IType primaryType = type.getCompilationUnit().findPrimaryType();
		if(primaryType == null)
			return false;
		
		String classname = primaryType.getElementName();
		
		String[] suffixes = Preferences.instance().getSuffixes();
		for(String suffix: suffixes) {
			if(suffix.length() > 0 && classname.endsWith(suffix))
				return true;
		}
		
		String[] prefixes = Preferences.instance().getPrefixes();
		for(String prefix: prefixes) {
			if(prefix.length() > 0 && classname.startsWith(prefix))
				return true;
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
	
	public JavaProjectFacade getJavaProjectFacade() {
		if(javaProjectFacade == null)
			javaProjectFacade = new JavaProjectFacade(compilationUnit.getJavaProject());
		
		return javaProjectFacade;
	}
	
	public IType getType() {
		return compilationUnit.findPrimaryType();
	}
	
	protected boolean doesMethodExist(String testMethodName) {
		try {
			IMethod[] vorhandeneTests = compilationUnit.findPrimaryType().getMethods();
			for (int i = 0; i < vorhandeneTests.length; i++) {
				IMethod method = vorhandeneTests[i];
				if(testMethodName.equals(method.getElementName()))
					return true;
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
		
		return false;
	}

}
