package moreUnit.elements;

import moreUnit.MoreUnitPlugin;
import moreUnit.log.LogHandler;
import moreUnit.util.MagicNumbers;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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
	
	public void deleteTestCaseMarkers() {
		try {
			javaProject.getProject().deleteMarkers(MagicNumbers.TEST_CASE_MARKER, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public void addTestCaseMarkers() {
		if(!javaProject.isOpen())
			return;
		
		try {
			IType[] testCaseListe = (new JavaProjectFacade(javaProject)).getTestCasesFromJavaProject();

			for(int i=0; i<testCaseListe.length; i++) {
				IType testCase = testCaseListe[i];
				(new JavaFileFacade(testCase.getCompilationUnit())).createMarkerForTestedClass();
			}
		} catch (JavaModelException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		} catch (CoreException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		} catch (NullPointerException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		}
	}	
	
}

// $Log$
// Revision 1.1  2006/01/30 21:12:32  gianasista
// Further Refactorings (moved methods from singleton classes like PluginTools to facade classes)
//