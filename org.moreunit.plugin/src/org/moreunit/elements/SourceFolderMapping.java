package org.moreunit.elements;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.log.LogHandler;

/**
 * @author vera
 *
 * 15.03.2008 12:06:30
 */
public class SourceFolderMapping {

	private IJavaProject javaProject;
	private IPackageFragmentRoot sourceFolder;
	private IPackageFragmentRoot testFolder;
	
	public SourceFolderMapping(IJavaProject javaProject, IPackageFragmentRoot sourceFolder, IPackageFragmentRoot testFolder) {
		this.javaProject = javaProject;
		this.sourceFolder = sourceFolder;
		this.testFolder = testFolder;
	}
	
	public SourceFolderMapping(IJavaProject javaProject, IPackageFragmentRoot testFolder) {
		this.javaProject = javaProject;
		this.testFolder = testFolder;
		this.sourceFolder = getPreferredSourceFolder();
	}
	
	private IPackageFragmentRoot getPreferredSourceFolder() {
		try {
			IPackageFragmentRoot[] packageFragmentRoots = javaProject.getPackageFragmentRoots();
			if(hasProjectsNoSourceFolders(packageFragmentRoots))
				return null;
			if(hasProjectsOnlyOneSourceFolder(packageFragmentRoots))
				return packageFragmentRoots[0];
			
			// if there are more than one sourcefolder in the project the user has to choose manually
			return packageFragmentRoots[0]; // TODO hack 
			
		} catch (JavaModelException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		}
		
		return null;
	}

	private boolean hasProjectsNoSourceFolders(IPackageFragmentRoot[] packageFragmentRoots) {
		return packageFragmentRoots == null || packageFragmentRoots.length == 0;
	}
	
	private boolean hasProjectsOnlyOneSourceFolder(IPackageFragmentRoot[] packageFragmentRoots) {
		return packageFragmentRoots.length == 1;
	}
	
	public void setSourceFolder(IPackageFragmentRoot sourceFolder) {
		this.sourceFolder = sourceFolder;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public IPackageFragmentRoot getSourceFolder() {
		return sourceFolder;
	}

	public IPackageFragmentRoot getTestFolder() {
		return testFolder;
	}
	
	/*
	private boolean isFirstSourceFolderTestFolder(IPackageFragmentRoot firstSourceFolder) {
		return firstSourceFolder == testFolder;
	}
	*/
}