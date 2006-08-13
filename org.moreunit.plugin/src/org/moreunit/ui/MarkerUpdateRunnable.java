/**
 * 
 */
package org.moreunit.ui;

import java.util.HashMap;
import java.util.Map;


import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.moreunit.elements.TestMethodVisitor;
import org.moreunit.log.LogHandler;
import org.moreunit.util.BaseTools;
import org.moreunit.util.MagicNumbers;

/**
 * @author giana
 * 14.04.2006 20:47:14
 * This thread handles the updates for the markers.
 * Has to be handled in a WorkspaceRunnable within a Thread.
 * (Found this hint at eclipseZone)
 */
public class MarkerUpdateRunnable implements Runnable {
	
	IType baseClassType;
	IType testCaseType;
	
	public MarkerUpdateRunnable(IType baseClassType, IType testCaseType) {
		this.baseClassType = baseClassType;
		this.testCaseType = testCaseType;
	}

	public void run() {
		IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				if(baseClassType == null || !baseClassType.exists())
					return;
				
				baseClassType.getResource().deleteMarkers(MagicNumbers.TEST_CASE_MARKER, true, IResource.DEPTH_INFINITE);

				TestMethodVisitor testMethodVisitor = new TestMethodVisitor(testCaseType);
				for(MethodDeclaration methodDeclaration : testMethodVisitor.getTestMethods()) {
					createMarkerForTestMethod(baseClassType, methodDeclaration);
				}
			}
		};
		
		IWorkspace workspace= ResourcesPlugin.getWorkspace();
		try {
			workspace.run(runnable, null);
		} catch (CoreException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		} 
	}
	
	private void createMarkerForTestMethod(IType classTypeUnderTest, MethodDeclaration testMethod) throws JavaModelException, CoreException {
		String testedMethodName = BaseTools.getTestedMethod(testMethod.getName().getFullyQualifiedName());
		if(testedMethodName != null) {
			IMethod[] methodsInClassUnderTest = classTypeUnderTest.getMethods();
			for(IMethod method: methodsInClassUnderTest) {
				if(testedMethodName.startsWith(method.getElementName()) && method.exists()) {
					ISourceRange range = method.getNameRange();
					Map<String,Object> map = new HashMap<String, Object>();
					map.put(IMarker.CHAR_START, range.getOffset());
					map.put(IMarker.CHAR_END, range.getOffset());
					map.put(IMarker.MESSAGE,	"This method has a testmethod.");

					MarkerUtilities.createMarker(classTypeUnderTest.getResource(), map, MagicNumbers.TEST_CASE_MARKER);
				}
			}
		}
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.1  2006/06/22 20:22:28  gianasista
// package rename
//
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.4  2006/05/25 19:33:20  gianasista
// JUnit4 support
//
// Revision 1.3  2006/05/20 16:11:36  gianasista
// translated marker message
//
// Revision 1.2  2006/05/14 22:27:10  channingwalton
// made use of generics to remove some warnings
//
// Revision 1.1  2006/04/14 19:41:16  gianasista
// MarkerUpdate moved to Thread because of resource locks
//