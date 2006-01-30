package moreUnit.elements;

import moreUnit.log.LogHandler;
import moreUnit.util.BaseTools;
import moreUnit.util.MagicNumbers;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;

public class JavaFileFacade {
	
	private ICompilationUnit compilationUnit;
	private JavaProjectFacade javaProjectFacade;
	
	public JavaFileFacade(ICompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}
	
	public JavaFileFacade(IFile file) {
		this.compilationUnit = JavaCore.createCompilationUnitFrom(file);
	}
	
	public JavaFileFacade(IEditorPart editorPart) {
		IFile file = (IFile)editorPart.getEditorInput().getAdapter(IFile.class);
		this.compilationUnit = JavaCore.createCompilationUnitFrom(file);
	}

	public IType getCorrespondingTestCase() {
		try {
			String klassenName = compilationUnit.findPrimaryType().getFullyQualifiedName()+MagicNumbers.TEST_CASE_SUFFIX;
			return compilationUnit.getJavaProject().findType(klassenName);
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
	
		return null;
	}
	
	public boolean isTestCase() {
		String classname = compilationUnit.findPrimaryType().getElementName();
		return classname.endsWith(MagicNumbers.TEST_CASE_SUFFIX);
	}
	
	public IType createTestCase() {
		try {
			String paketName = MagicNumbers.EMPTY_STRING;
			IPackageDeclaration[] packageDeclarations = compilationUnit.getPackageDeclarations();
			if(packageDeclarations.length > 0) {
				IPackageDeclaration packageDeclaration = packageDeclarations[0];
				paketName = packageDeclaration.getElementName();
			}
			
			IPackageFragmentRoot junitSourceFolder = getJavaProjectFacade().getJUnitSourceFolder();
			if (junitSourceFolder.exists()) {
				IPackageFragment packageFragment = junitSourceFolder.getPackageFragment(paketName);
				if (!packageFragment.exists()) 
					packageFragment = junitSourceFolder.createPackageFragment(paketName, true, null);
				
				IFile file = (IFile) compilationUnit.getUnderlyingResource().getAdapter(IFile.class);
				String testCaseClassName = BaseTools.getNameOfTestCaseClass(file);
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
	
	public JavaProjectFacade getJavaProjectFacade() {
		if(javaProjectFacade == null)
			javaProjectFacade = new JavaProjectFacade(compilationUnit.getJavaProject());
		
		return javaProjectFacade;
	}
	
	public IMethod getCorrespondingTestMethod(IMethod method) {
		String nameOfCorrespondingTestMethod = BaseTools.getTestmethodNameFromMethodName(method.getElementName());
		
		try {
			IMethod[] methodsOfType = getCorrespondingTestCase().getCompilationUnit().findPrimaryType().getMethods();
			for(int i=0; i<methodsOfType.length; i++) {
				IMethod testmethod = methodsOfType[i];
				if(nameOfCorrespondingTestMethod.equals(testmethod.getElementName()))
					return testmethod;
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
		
		return null;
	}
	
	public boolean createTestMethodForMethod(IMethod methodToTest) {
		try {
			String methodName = methodToTest.getElementName();
			String erstesZeichen = String.valueOf(methodName.charAt(0));
			methodName = methodName.replaceFirst(erstesZeichen, erstesZeichen.toUpperCase());
			
			String testMethodName = MagicNumbers.TEST_METHOD_PRAEFIX+methodName;
			if(doesTestMethodExist(testMethodName))
				return false;
			
			String methodHead = "public void "+testMethodName+"() {"+MagicNumbers.NEWLINE+"}";
			compilationUnit.findPrimaryType().createMethod(methodHead, null, true, null);
			return true;
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
		
		return false;
	}
	
	private boolean doesTestMethodExist(String testMethodName) {
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

// $Log$
// Revision 1.1  2006/01/28 15:48:25  gianasista
// Moved several methods from PluginTools to EditorPartFacade
//