package moreUnit.elements;

import moreUnit.MoreUnitPlugin;
import moreUnit.log.LogHandler;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author vera
 *
 * 30.01.2006 20:30:54
 */
public class JavaProjectFacade {
	
	private IJavaProject javaProject;
	
	public JavaProjectFacade(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public IPackageFragmentRoot getJUnitSourceFolder() {
		try {
			IPackageFragmentRoot[] packageFragmentRoots = javaProject.getPackageFragmentRoots();
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
	
	public IType[] getTestCasesFromJavaProject() throws JavaModelException {
		IType testCaseType = javaProject.findType("junit.framework.TestCase");
		if(testCaseType == null)
			return null;

		ITypeHierarchy hierarchy= testCaseType.newTypeHierarchy(javaProject, new NullProgressMonitor());
		IType[] testCaseListe = hierarchy.getAllSubtypes(testCaseType);
		return testCaseListe;
	}
}

// $Log$