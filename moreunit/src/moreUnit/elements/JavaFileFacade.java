package moreUnit.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import moreUnit.log.LogHandler;
import moreUnit.preferences.Preferences;
import moreUnit.ui.MarkerUpdateRunnable;
import moreUnit.ui.TypeChoiceDialog;
import moreUnit.util.BaseTools;
import moreUnit.util.MagicNumbers;
import moreUnit.util.TestCaseDiviner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * JavaFileFacade offers easy access to a simple java file within eclipse.
 * It is identified by a {@link ICompilationUnit}.
 * 
 * @author vera
 */
public class JavaFileFacade {
	
	private ICompilationUnit compilationUnit;
	private JavaProjectFacade javaProjectFacade;
	
	private TestCaseDiviner testCaseDiviner;
	
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

	/**
	 * Returns the corresponding testcase of the javaFileFacade. If there are more
	 * than one testcases the uses has to make a choice via a dialog. If no test is
	 * found <code>null</code> is returned.
	 * 
	 * @return one of the corresponding testcases
	 */
	public IType getOneCorrespondingTestCase() {
		Set<IType> testcases = getCorrespondingTestCaseList();
		IType testcaseToJump = null;
		if(testcases.size() == 1)
			testcaseToJump = (IType) testcases.toArray()[0];
		else
			testcaseToJump = new TypeChoiceDialog((IType[]) testcases.toArray(new IType[testcases.size()])).getChoice();
		
		return testcaseToJump;
	}
	
	public Set<IType> getCorrespondingTestCaseList() {
		return getTestCaseDiviner().getMatches();
	}
	
	public IType getCorrespondingClassUnderTest() {
		String testedClassString = BaseTools.getTestedClass(getType().getFullyQualifiedName(), Preferences.instance().getPrefixes(), Preferences.instance().getSuffixes());
		if(testedClassString == null)
			return null;

		try {
			return compilationUnit.getJavaProject().findType(testedClassString);
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
		
		return null;
	}
	
	/**
	 * This method checks if the name of the class starts with one of
	 * the prefixes or ends with one of the suffixes defined in the preferences.
	 *  
	 * @return <code>true</code> if a prefix is found this classname starts with
	 *         suffix is found this classname ends, otherwise <code>false</code>
	 *         is returned
	 */
	public boolean isTestCase() {
		IType primaryType = compilationUnit.findPrimaryType();
		if(primaryType == null)
			return false;
		
		String classname = primaryType.getElementName();
		
		String[] suffixes = Preferences.instance().getSuffixes();
		for(String suffix: suffixes) {
			if(classname.endsWith(suffix))
				return true;
		}
		
		String[] prefixes = Preferences.instance().getPrefixes();
		for(String prefix: prefixes) {
			if(classname.startsWith(prefix))
				return true;
		}
		
		return false;
	}
	
	/*
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
				contents.append("import junit.framework.TestCase;"+MagicNumbers.NEWLINE);
				contents.append(MagicNumbers.NEWLINE);
				contents.append("public class " + testCaseClassName + " extends TestCase {"+ MagicNumbers.NEWLINE);
				contents.append(MagicNumbers.NEWLINE);
				contents.append("}");
				ICompilationUnit compilationUnitCopy = packageFragment.createCompilationUnit(testCaseClassName+".java",contents.toString(), true, null);
				try {
					String fileComment = CodeGeneration.getFileComment(compilationUnitCopy, "\n");
					String typeComment = CodeGeneration.getTypeComment(compilationUnitCopy, compilationUnitCopy.findPrimaryType().getTypeQualifiedName(), "\n");
					
					compilationUnitCopy.getBuffer().setContents(CodeGeneration.getCompilationUnitContent(compilationUnitCopy, fileComment, typeComment, contents.toString(), "\n"));
				} catch (CoreException exc) {
					LogHandler.getInstance().handleExceptionLog(exc);
				}
				return compilationUnitCopy.findPrimaryType();
			} else {
				LogHandler.getInstance().handleInfoLog("junit-Source-Folder konnte nicht gefunden werden.");
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
			
		return null;
	}
	*/
	
	public JavaProjectFacade getJavaProjectFacade() {
		if(javaProjectFacade == null)
			javaProjectFacade = new JavaProjectFacade(compilationUnit.getJavaProject());
		
		return javaProjectFacade;
	}
	
	public IMethod getCorrespondingTestMethod(IMethod method, IType testCaseType) {
		String nameOfCorrespondingTestMethod = BaseTools.getTestmethodNameFromMethodName(method.getElementName());
		
		if(testCaseType == null)
			return null;
		
		try {
			IMethod[] methodsOfType = testCaseType.getCompilationUnit().findPrimaryType().getMethods();
			for(int i=0; i<methodsOfType.length; i++) {
				IMethod testmethod = methodsOfType[i];
				if(testmethod.getElementName().startsWith(nameOfCorrespondingTestMethod))
					return testmethod;
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
		
		return null;
	}
	
	public List<IMethod> getCorrespondingTestMethods(IMethod method) {
		List<IMethod> result = new ArrayList<IMethod>();
		
		Set<IType> allTestCases = getCorrespondingTestCaseList();
		
		for(IType testCaseType: allTestCases) {
			result.addAll(getTestMethodsForTestCase(method, testCaseType));
		}
		
		return result;
	}

	private List<IMethod> getTestMethodsForTestCase(IMethod method, IType testCaseType) {
		List<IMethod> result = new ArrayList<IMethod>();
		
		if(testCaseType == null)
			return result;
		
		String nameOfCorrespondingTestMethod = BaseTools.getTestmethodNameFromMethodName(method.getElementName());
		
		try {
			IMethod[] methodsOfType = testCaseType.getCompilationUnit().findPrimaryType().getMethods();
			for(int i=0; i<methodsOfType.length; i++) {
				IMethod testmethod = methodsOfType[i];
				if(testmethod.getElementName().startsWith(nameOfCorrespondingTestMethod))
					result.add(testmethod);
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
		
		return result;
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
	
	public void createMarkerForTestedClass() throws CoreException {
		IResource resource= compilationUnit.getUnderlyingResource();
		if (resource == null)
			return;
		
		if(!Flags.isAbstract(getType().getFlags())) {
			
			IType testedClass = getCorrespondingClassUnderTest();
			if(testedClass == null)
				return;
			
			PlatformUI.getWorkbench().getDisplay().asyncExec(new MarkerUpdateRunnable(testedClass, getType()));
		}
	}

	public IType getType() {
		return compilationUnit.findPrimaryType();
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
	
	
	/**
	 * Getter uses lazy caching. 
	 */
	private TestCaseDiviner getTestCaseDiviner() {
		if(testCaseDiviner == null)
			testCaseDiviner = new TestCaseDiviner(compilationUnit, Preferences.instance());
		
		return testCaseDiviner;
	}
	
}

// $Log$
// Revision 1.16  2006/05/18 06:57:48  channingwalton
// fixed some warnings and deprecated APIs
//
// Revision 1.15  2006/05/15 19:49:58  gianasista
// proceeded integration of switchunit code
//
// Revision 1.14  2006/05/14 22:27:10  channingwalton
// made use of generics to remove some warnings
//
// Revision 1.13  2006/05/14 19:07:55  gianasista
// JumpToTest uses TypeChoiceDialog
//
// Revision 1.12  2006/05/12 22:33:42  channingwalton
// added class creation wizards if type to jump to does not exist
//
// Revision 1.11  2006/05/12 17:52:38  gianasista
// added comments
//
// Revision 1.10  2006/04/24 20:15:05  gianasista
// Creation of marker via asyncExec
//
// Revision 1.9  2006/04/21 05:56:39  gianasista
// Feature: Jump from testcase back to class under test
//
// Revision 1.8  2006/04/16 16:57:35  gianasista
// Bugfix: isTestCase wasn't null-safe yet
//
// Revision 1.7  2006/04/14 19:42:38  gianasista
// MarkerUpdate moved to Thread because of resource locks
//
// Revision 1.6  2006/04/14 17:14:22  gianasista
// Refactoring Support with dialog
//
// Revision 1.5  2006/02/26 16:53:17  gianasista
// Test methods can have a suffix and are recognized as a testmethod
//
// Revision 1.4  2006/02/22 21:30:52  gianasista
// Removed unneccessary if-statement.
//
// Revision 1.3  2006/01/31 19:05:54  gianasista
// Refactored MarkerTools and added methods to corresponding facade classes.
//
// Revision 1.2  2006/01/30 21:12:31  gianasista
// Further Refactorings (moved methods from singleton classes like PluginTools to facade classes)
//
// Revision 1.1  2006/01/28 15:48:25  gianasista
// Moved several methods from PluginTools to EditorPartFacade
//
