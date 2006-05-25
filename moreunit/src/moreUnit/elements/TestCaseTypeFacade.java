package moreUnit.elements;

import moreUnit.log.LogHandler;
import moreUnit.preferences.Preferences;
import moreUnit.ui.MarkerUpdateRunnable;
import moreUnit.util.BaseTools;
import moreUnit.util.MagicNumbers;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * ClassTypeFacade offers easy access to a simple java file within eclipse.
 * The file represented by this instance is not a testcase.
 * 
 * @author vera
 *
 * 23.05.2006 20:29:57
 */
public class TestCaseTypeFacade extends TypeFacade {

	public TestCaseTypeFacade(ICompilationUnit compilationUnit) {
		super(compilationUnit);
	}

	public TestCaseTypeFacade(IEditorPart editorPart) {
		super(editorPart);
	}

	public TestCaseTypeFacade(IFile file) {
		super(file);
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
	
	public boolean createTestMethodForMethod(IMethod methodToTest) {
		try {
			String methodName = methodToTest.getElementName();
			String erstesZeichen = String.valueOf(methodName.charAt(0));
			methodName = methodName.replaceFirst(erstesZeichen, erstesZeichen.toUpperCase());
			
			String testMethodName = MagicNumbers.TEST_METHOD_PRAEFIX+methodName;
			if(doesMethodExist(testMethodName))
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
}