package moreUnit;

import org.eclipse.jdt.core.IPackageFragmentRoot;

import junit.framework.TestCase;

/**
 * @author vera
 *
 * 23.05.2006 21:46:30
 */
public class ProjectTestCase extends TestCase {
	
	protected TestProject testProject;

	IPackageFragmentRoot junitSourceRoot;
	
	protected void setUp() throws Exception {
		super.setUp();
		testProject = new TestProject("ATestProject");
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		testProject.dispose();
	}
	
	public void testNothing() {
		assertTrue(true);
	}

}
