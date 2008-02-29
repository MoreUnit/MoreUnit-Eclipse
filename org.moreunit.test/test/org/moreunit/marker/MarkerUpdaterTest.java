package org.moreunit.marker;

import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.ProjectTestCase;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.util.StringConstants;

/**
 * @author vera
 *
 * 20.02.2008 21:48:06
 */
public class MarkerUpdaterTest extends ProjectTestCase {
	
	private ClassTypeFacade cutTypeFacade;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		IPackageFragment createPackage = testProject.createPackage("com");
		IType createType = testProject.createType(createPackage, "Hello.java", getSourceForCut());
		cutTypeFacade = new ClassTypeFacade(createType.getCompilationUnit());
		
		IPackageFragmentRoot junitSourceRoot = testProject.createAdditionalSourceFolder("junit");
		IPackageFragment junitComPaket = testProject.createPackage(junitSourceRoot, "com");
		testProject.createType(junitComPaket, "HelloTest.java", getSourceForFirstTestCase());
		testProject.createType(junitComPaket, "HelloSecondTest.java", getSourceForSecondTestCase());
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

	private String getSourceForCut() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(StringConstants.NEWLINE);
		source.append("public class Hello {").append(StringConstants.NEWLINE);
		source.append("public int getOne() { return 1; }").append(StringConstants.NEWLINE);
		source.append("public int getTwo() { return 2; }").append(StringConstants.NEWLINE);
		source.append("public int getThree() { return 3; }").append(StringConstants.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getSourceForFirstTestCase() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(StringConstants.NEWLINE);
		source.append("import junit.framework.TestCase;").append(StringConstants.NEWLINE);
		source.append("public class HelloTest extends TestCase{").append(StringConstants.NEWLINE);
		source.append("public void testGetOne() {").append(StringConstants.NEWLINE);
		source.append("assertTrue(true);").append(StringConstants.NEWLINE);
		source.append("}").append(StringConstants.NEWLINE);
		source.append("public void testGetThree() {").append(StringConstants.NEWLINE);
		source.append("assertTrue(true);").append(StringConstants.NEWLINE);
		source.append("}").append(StringConstants.NEWLINE);
		source.append("}");
		
		return source.toString();
	}
	
	private String getSourceForSecondTestCase() {
		StringBuffer source = new StringBuffer();
		source.append("package com;").append(StringConstants.NEWLINE);
		source.append("import junit.framework.TestCase;").append(StringConstants.NEWLINE);
		source.append("public class HelloSecondTest extends TestCase{").append(StringConstants.NEWLINE);
		source.append("public void testGetOne() {").append(StringConstants.NEWLINE);
		source.append("assertTrue(true);").append(StringConstants.NEWLINE);
		source.append("}").append(StringConstants.NEWLINE);
		source.append("public void testGetOneAnother() {").append(StringConstants.NEWLINE);
		source.append("assertTrue(true);").append(StringConstants.NEWLINE);
		source.append("}").append(StringConstants.NEWLINE);
		source.append("}");
		
		return source.toString();
	}

}
