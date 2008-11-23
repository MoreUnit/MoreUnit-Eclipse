package org.moreunit.marker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.log.LogHandler;
import org.moreunit.util.MoreUnitContants;

/**
 * @author vera
 *
 * 20.02.2008 20:49:40
 */
public class MarkerUpdater extends Job {
	
	private ClassTypeFacade classTypeFacade;
	private IMethod[] methods;
	
	public MarkerUpdater(ClassTypeFacade classTypeFacade) {
		super("Update moreUnit marker");
		
		this.classTypeFacade = classTypeFacade;
		
		try {
			IType type = this.classTypeFacade.getType();
			if(type != null)
				this.methods = type.getMethods();
			else
				this.methods = new IMethod[] {};
		} catch (JavaModelException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		}
	}

	public IStatus run(IProgressMonitor monitor) {
		try {
			classTypeFacade.getCompilationUnit().getResource().deleteMarkers(MoreUnitContants.TEST_CASE_MARKER, true, IResource.DEPTH_INFINITE);
			
			for(IMethod method : methods) {
				updateMarkerForMethod(method);
			}
			return new Status(Status.OK, MoreUnitPlugin.PLUGIN_ID, "Done");
		} catch (CoreException e) {
			LogHandler.getInstance().handleExceptionLog(e);
			return new Status(Status.ERROR, MoreUnitPlugin.PLUGIN_ID, e.getLocalizedMessage());
		}
	}
	
	protected void updateMarkerForMethod(IMethod method) throws CoreException {
		if(hasTestMethod(method)) {
			Map<String, Object> markerMap = getMarkerMap(method);
			MarkerUtilities.createMarker(classTypeFacade.getType().getResource(), markerMap, MoreUnitContants.TEST_CASE_MARKER);
		}
	}
	
	public boolean hasTestMethod(IMethod method) {
		List<IMethod> correspondingTestMethods = classTypeFacade.getCorrespondingTestMethods(method);
		return correspondingTestMethods != null && correspondingTestMethods.size() > 0;
	}
	
	public Map<String, Object> getMarkerMap(IMethod method) throws JavaModelException {
		ISourceRange range = method.getNameRange();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(IMarker.CHAR_START, range.getOffset());
		map.put(IMarker.CHAR_END, range.getOffset());
		map.put(IMarker.MESSAGE, "This method has a testmethod.");
		return map;
	}
}