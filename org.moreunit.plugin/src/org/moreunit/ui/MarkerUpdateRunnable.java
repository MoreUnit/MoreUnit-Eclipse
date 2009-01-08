package org.moreunit.ui;

import java.util.HashMap;
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
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.elements.TestMethodVisitor;
import org.moreunit.log.LogHandler;
import org.moreunit.util.MoreUnitContants;
import org.moreunit.util.TestMethodDiviner;
import org.moreunit.util.TestMethodDivinerFactory;

/**
 * @author giana 14.04.2006 20:47:14 This thread handles the updates for the
 *         markers. Has to be handled in a WorkspaceRunnable within a Thread.
 *         (Found this hint at eclipseZone)
 */
public class MarkerUpdateRunnable extends Job {

	IType baseClassType;
	IType testCaseType;
	TestMethodDivinerFactory testMethodDivinerFactory;
	TestMethodDiviner testMethodDiviner;

	public MarkerUpdateRunnable(IType baseClassType, IType testCaseType) {
		super("Update Moreunit Marker");
		this.baseClassType = baseClassType;
		this.testCaseType = testCaseType;
		testMethodDivinerFactory = new TestMethodDivinerFactory(baseClassType.getCompilationUnit());
		testMethodDiviner = testMethodDivinerFactory.create();
	}

	public IStatus run(IProgressMonitor monitor) {
		if (baseClassType == null || !baseClassType.exists())
			return new Status(Status.OK, MoreUnitPlugin.PLUGIN_ID, "Nothing to do");

		try {
			baseClassType.getResource().deleteMarkers(MoreUnitContants.TEST_CASE_MARKER, true, IResource.DEPTH_INFINITE);

			TestMethodVisitor testMethodVisitor = new TestMethodVisitor(testCaseType);
			for (MethodDeclaration methodDeclaration : testMethodVisitor.getTestMethods()) {
				createMarkerForTestMethod(baseClassType, methodDeclaration);
			}
			return new Status(Status.OK, MoreUnitPlugin.PLUGIN_ID, "Done");
		} catch (Exception e) {
			LogHandler.getInstance().handleExceptionLog(e);
			return new Status(Status.ERROR, MoreUnitPlugin.PLUGIN_ID, e.getLocalizedMessage());
		}
	}

	private void createMarkerForTestMethod(IType classTypeUnderTest, MethodDeclaration testMethod) throws JavaModelException, CoreException {
		String testedMethodName = testMethodDiviner.getMethodNameFromTestMethodName(testMethod.getName().getFullyQualifiedName());
		if (testedMethodName != null) {
			IMethod[] methodsInClassUnderTest = classTypeUnderTest.getMethods();
			for (IMethod method : methodsInClassUnderTest) {
				if (testedMethodName.startsWith(method.getElementName()) && method.exists()) {
					ISourceRange range = method.getNameRange();
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(IMarker.CHAR_START, range.getOffset());
					map.put(IMarker.CHAR_END, range.getOffset());
					map.put(IMarker.MESSAGE, "This method has a testmethod.");

					MarkerUtilities.createMarker(classTypeUnderTest.getResource(), map, MoreUnitContants.TEST_CASE_MARKER);
				}
			}
		}
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.4  2008/02/29 21:32:55  gianasista
// Removed empty comment
//
// Revision 1.3  2008/02/20 19:23:38  gianasista
// Rename of classes for constants
//
// Revision 1.2  2007/11/13 12:41:28  channingwalton
// fix for bug [ 1831049 ] marker update causing an IAE
//
// Revision 1.1.1.1 2006/08/13 14:31:16 gianasista
// initial
//
// Revision 1.1 2006/06/22 20:22:28 gianasista
// package rename
//
// Revision 1.1 2006/06/19 20:08:48 gianasista
// CVS Refactoring
//
// Revision 1.4 2006/05/25 19:33:20 gianasista
// JUnit4 support
//
// Revision 1.3 2006/05/20 16:11:36 gianasista
// translated marker message
//
// Revision 1.2 2006/05/14 22:27:10 channingwalton
// made use of generics to remove some warnings
//
// Revision 1.1 2006/04/14 19:41:16 gianasista
// MarkerUpdate moved to Thread because of resource locks
//