package org.moreunit.properties;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.moreunit.TestProject;

public class ProjectPropertiesTest extends MockObjectTestCase {

	private Mock		javaProject	= mock(IJavaProject.class);
	private Mock		project		= mock(IProject.class);
	private TestProject	testProject2;
	private TestProject	testProject1;
	private TestProject	realJavaProject;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		javaProject.stubs().method("getProject").will(returnValue(project.proxy()));
	}

	@Override
	protected void tearDown() throws Exception {
		dispose(testProject1);
		dispose(testProject2);
		dispose(realJavaProject);
	}

	private void dispose(TestProject testProject) throws CoreException {
		if (testProject != null) {
			testProject.dispose();
		}
	}

	public void testGetJumpTargets() throws Exception {
		testProject1 = new TestProject("ProjectWithTests1");
		testProject2 = new TestProject("ProjectWithTests2");
		setTestProjects("ProjectWithTests1,ProjectWithTests2");
		List<IJavaProject> testProjects = ProjectProperties.instance().getJumpTargets((IJavaProject) javaProject.proxy());
		assertTrue(testProjects.contains(testProject1.getJavaProject()));
		assertTrue(testProjects.contains(testProject2.getJavaProject()));
	}

	public void testGetJumpTargetsWhenNoneSet() throws Exception {
		setTestProjects(null);
		assertEquals(Collections.EMPTY_LIST, ProjectProperties.instance().getJumpTargets((IJavaProject) javaProject.proxy()));
	}

	public void testGetJumpTargetForProjectThatIsTargetForAnotherProject() throws Exception {
		realJavaProject = new TestProject("project");

		testProject1 = new TestProject("realProjectsTests1");
		testProject2 = new TestProject("realProjectsTests2");

		realJavaProject.getJavaProject().getProject().setPersistentProperty(ProjectProperties.JUMP_TARGETS_PROPERTY, testProject1.getProject().getName());

		List<IJavaProject> testProjects = ProjectProperties.instance().getJumpTargets(testProject1.getJavaProject());
		assertTrue(testProjects.contains(realJavaProject.getJavaProject()));
	}

	public void testGetTestProjectsWillReturnAnEmptyListIfThereAreNoTestProjects() throws Exception {
		setTestProjects("");
		assertEquals(Collections.EMPTY_LIST, ProjectProperties.instance().getJumpTargets((IJavaProject) javaProject.proxy()));
	}

	public void testSetTestProjects() throws CoreException {
		realJavaProject = new TestProject("setTestProject");

		testProject1 = new TestProject("setTest1");
		testProject2 = new TestProject("setTest2");
		ProjectProperties.instance().setTestProjects(realJavaProject.getProject(), new IJavaProject[] {testProject1.getJavaProject()});
		
		List<IJavaProject> jumpTargets = ProjectProperties.instance().getJumpTargets(realJavaProject.getJavaProject());
		assertEquals(1, jumpTargets.size());
		assertTrue(jumpTargets.contains(testProject1.getJavaProject()));
		
		jumpTargets = ProjectProperties.instance().getJumpTargets(testProject1.getJavaProject());
		assertEquals(1, jumpTargets.size());
		assertTrue(jumpTargets.contains(realJavaProject.getJavaProject()));

		jumpTargets = ProjectProperties.instance().getJumpTargets(testProject2.getJavaProject());
		assertEquals(0, jumpTargets.size());
	}

	private void setTestProjects(String projects) {
		project.stubs().method("getPersistentProperty").with(same(ProjectProperties.JUMP_TARGETS_PROPERTY)).will(returnValue(projects));
	}
}
