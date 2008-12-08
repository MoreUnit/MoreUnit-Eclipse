package org.moreunit.marker;

import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.SimpleProjectTestCase;
import org.moreunit.WorkspaceHelper;
import org.moreunit.elements.ClassTypeFacade;

/**
 * @author vera
 *
 * 20.02.2008 21:48:06
 */
public class MarkerUpdaterTest extends SimpleProjectTestCase {
	
	private ClassTypeFacade cutTypeFacade;
	
	@Override
	protected void setUp() throws Exception {
		
		super.setUp();
		
		IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
		cutTypeFacade = new ClassTypeFacade(cutType.getCompilationUnit());
		WorkspaceHelper.createMethodInJavaType(cutType, "public int getOne()", "return 1;");
		WorkspaceHelper.createMethodInJavaType(cutType, "public int getTwo()", "return 2;");
		WorkspaceHelper.createMethodInJavaType(cutType, "public int getThree()", "return 3;");
		
		IType testType = WorkspaceHelper.createJavaClass(testPackage, "HelloTest");
		WorkspaceHelper.createMethodInJavaType(testType, "public void testGetOne()", "return;");
		WorkspaceHelper.createMethodInJavaType(testType, "public void testGetThree()", "return;");
		
		IType secondTestType = WorkspaceHelper.createJavaClass(testPackage, "HelloSecondTest");
		WorkspaceHelper.createMethodInJavaType(secondTestType, "public void testGetOne()", "return;");
		WorkspaceHelper.createMethodInJavaType(secondTestType, "public void testGetOneAnother()", "return;");
	}
	
	public void testHasTestMethod() throws JavaModelException {
		MarkerUpdater updater = new MarkerUpdater(cutTypeFacade);
		
		// Mutliple testmethods
		IMethod getOneMethod = cutTypeFacade.getType().getMethods()[0];
		assertTrue(updater.hasTestMethod(getOneMethod));
		
		// No tests
		IMethod getTwoMethod = cutTypeFacade.getType().getMethods()[1];
		assertFalse(updater.hasTestMethod(getTwoMethod));
		
		// tests in one testcase
		IMethod getThreeMethod = cutTypeFacade.getType().getMethods()[2];
		assertTrue(updater.hasTestMethod(getThreeMethod));
	}
	
	public void testGetMarkerMap() throws JavaModelException {
		MarkerUpdater updater = new MarkerUpdater(cutTypeFacade);
		
		IMethod getThreeMethod = cutTypeFacade.getType().getMethods()[2];
		Map<String, Object> markerMap = updater.getMarkerMap(getThreeMethod);
		assertEquals(3, markerMap.size());
		assertTrue(markerMap.containsKey(IMarker.CHAR_START));
		assertTrue(markerMap.containsKey(IMarker.CHAR_END));
		assertTrue(markerMap.containsKey(IMarker.MESSAGE));
	}
}
