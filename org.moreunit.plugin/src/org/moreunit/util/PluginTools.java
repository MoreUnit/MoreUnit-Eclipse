package org.moreunit.util;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.moreunit.log.LogHandler;

public class PluginTools {
	
	public static IEditorPart getOpenEditorPart() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		
		if(page != null) 		
			return page.getActiveEditor();
		else
			return null;
	}
	
	public static boolean isJavaFile(IWorkbenchPart part) {
		if (!(part instanceof IEditorPart))
			return false;

		IFile file = (IFile) ((IEditorPart) part).getEditorInput().getAdapter(IFile.class);
		if (file == null)
			return false;

		return "java".equals(file.getFileExtension());
	}

	public static IPackageFragmentRoot createPackageFragmentRoot(String projectName, String folderName) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IJavaProject javaProject = JavaCore.create(project);
		try {
			for(IPackageFragmentRoot aSourceFolder : javaProject.getPackageFragmentRoots()) {
				if(folderName.equals(aSourceFolder.getElementName()))
					return aSourceFolder;
			}
		} catch (JavaModelException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		}
		
		return null;
	}
	
	public static List<IJavaProject> getJavaProjectsFromWorkspace() {
		List<IJavaProject> result = new ArrayList<IJavaProject>();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for(IProject aProject : projects) {
			try {
				if(aProject.hasNature(JavaCore.NATURE_ID)) {
					result.add(JavaCore.create(aProject));
				}
			} catch (CoreException e) {
				LogHandler.getInstance().handleExceptionLog(e);
			}
		}
		
		return result;
	}
	
	public static IPackageFragmentRoot getSourceFolder(ICompilationUnit compilationUnit) {
		IJavaElement element = compilationUnit;
		while(!(element instanceof IPackageFragmentRoot)) {
			element = element.getParent();
		}
		
		return (IPackageFragmentRoot) element;
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.4  2008/03/10 19:49:47  gianasista
// New property page for test source folder configuration
//
// Revision 1.3  2008/02/29 21:33:46  gianasista
// Minor refactorings
//
// Revision 1.2  2007/02/18 13:46:37  gianasista
// Bugfix: Solved exceptions in missing testmethod view
//
// Revision 1.1.1.1  2006/08/13 14:31:16  gianasista
// initial
//
// Revision 1.1  2006/06/22 20:22:28  gianasista
// package rename
//
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.9  2006/01/30 21:12:31  gianasista
// Further Refactorings (moved methods from singleton classes like PluginTools to facade classes)
//
// Revision 1.8  2006/01/28 15:48:24  gianasista
// Moved several methods from PluginTools to EditorPartFacade
//
// Revision 1.7  2006/01/25 21:25:16  gianasista
// getMethodUnderCursorPosition is deprecated, new class EditorPartFacade implements this functionality now
//
// Revision 1.6  2006/01/22 20:53:32  gianasista
// Bugfix: Testcase in wrong java project (sometimes)
//
// Revision 1.5  2006/01/19 21:38:32  gianasista
// Added CVS-commit-logging to all java-files
//