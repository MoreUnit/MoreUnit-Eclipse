/**
 * 
 */
package moreUnit.ui;

import java.util.HashMap;
import java.util.Map;

import moreUnit.log.LogHandler;
import moreUnit.util.BaseTools;
import moreUnit.util.MagicNumbers;

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
import org.eclipse.ui.texteditor.MarkerUtilities;

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

				IMethod[] testMethoden = testCaseType.getMethods();
				for(int j=0; j<testMethoden.length; j++) {
					IMethod methode = testMethoden[j];
					createMarkerForTestMethod(baseClassType, methode);
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
	
	private void createMarkerForTestMethod(IType testedClass, IMethod methode) throws JavaModelException, CoreException {
		String testedMethodName = BaseTools.getTestedMethod(methode.getElementName());
		if(testedMethodName != null) {
			IMethod[] foundTestMethods = testedClass.getMethods();
			for(int i=0; i<foundTestMethods.length; i++) {
				IMethod method = foundTestMethods[i];
				if(testedMethodName.startsWith(method.getElementName()) && method.exists()) {
					ISourceRange range = method.getNameRange();
					Map map = new HashMap();
					map.put(IMarker.CHAR_START, new Integer(range.getOffset()));
					map.put(IMarker.CHAR_END, new Integer(range.getOffset()));
					map.put(IMarker.MESSAGE,	"Diese Methode befindet sich im Test");

					MarkerUtilities.createMarker(testedClass.getResource(), map, MagicNumbers.TEST_CASE_MARKER);
				}
			}
		}
	}
}

// $Log: not supported by cvs2svn $