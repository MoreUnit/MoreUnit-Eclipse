package moreUnit.util;

import java.util.HashMap;
import java.util.Map;

import moreUnit.log.LogHandler;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.texteditor.MarkerUtilities;

/**
 * @author vera
 * 26.12.2005 12:56:28
 */
public class MarkerTools {

	public static void deleteTestCaseMarkers(IJavaProject javaProject) {
		try {
			javaProject.getProject().deleteMarkers(MagicNumbers.TEST_CASE_MARKER, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public static void addTestCaseMarkers(IJavaProject javaProject) {
		if(!javaProject.isOpen())
			return;
		
		try {
			IType[] testCaseListe = PluginTools.getTestCasesFromJavaProject(javaProject);

			for(int i=0; i<testCaseListe.length; i++) {
				IType testCase = testCaseListe[i];
				createMarkerForTestedClass(javaProject, testCase);
			}
		} catch (JavaModelException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		} catch (CoreException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		} catch (NullPointerException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		}
	}
	
	public static void createMarkerForTestedClass(IJavaProject javaProject, IType testCase) throws CoreException {
		IResource resource= testCase.getUnderlyingResource();
		if (resource == null)
			return;
		if (!resource.getProject().equals(javaProject.getProject()))
			return;
		
		
		if(!Flags.isAbstract(testCase.getFlags())) {
			String testedClassString = BaseTools.getTestedClass(testCase.getFullyQualifiedName());
			if(testedClassString == null)
				return;
			
			IType testedClass = javaProject.findType(testedClassString);
			
			if(testedClass == null || !testedClass.exists())
				return;
			
			testedClass.getResource().deleteMarkers(MagicNumbers.TEST_CASE_MARKER, true, IResource.DEPTH_INFINITE);

			IMethod[] testMethoden = testCase.getMethods();
			for(int j=0; j<testMethoden.length; j++) {
				IMethod methode = testMethoden[j];
				String testedMethodName = BaseTools.getTestedMethod(methode.getElementName());
				if(testedMethodName != null) {
					IMethod[] foundTestMethods = testedClass.getMethods();
					for(int i=0; i<foundTestMethods.length; i++) {
						IMethod method = foundTestMethods[i];
						if(method.getElementName().equals(testedMethodName) && method.exists()) {
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
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//
