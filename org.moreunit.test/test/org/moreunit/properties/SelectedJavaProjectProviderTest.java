package org.moreunit.properties;

import org.eclipse.core.runtime.CoreException;
import org.moreunit.AbstractMoreUnitTest;
import org.moreunit.TestProject;

public class SelectedJavaProjectProviderTest extends AbstractMoreUnitTest {

	private TestProject					testProject2;
	private TestProject					testProject1;
	private TestProject					javaProject;
	private SelectedJavaProjectProvider	provider;

	protected void setUp() throws Exception {
		super.setUp();
		javaProject = new TestProject("project");

		provider = new SelectedJavaProjectProvider(javaProject.getJavaProject());
	}

	@Override
	protected void tearDown() throws Exception {
		dispose(testProject1);
		dispose(testProject2);
		dispose(javaProject);
	}

	public void testGetElementsReturnsEmptyArray() throws Exception {
		assertEquals(0, provider.getElements().length);
	}

	public void testElementsWhenProjectHasNoTestProjects() throws Exception {
		testProject1 = new TestProject("testProject1");
		
		SelectedJavaProject[] elements = provider.getElements();
		
		assertEquals(1, elements.length);
		assertEquals(new SelectedJavaProject(testProject1.getJavaProject(), false), elements[0]);
		
		assertEquals(0, provider.getCheckedElements().length);
	}

	public void testGetElementsWhenProjectHasOneTestProject() throws Exception {
		testProject1 = new TestProject("testProject1");
		testProject2 = new TestProject("testProject2");
		
		setJumpTarget(javaProject, testProject1);
		
		SelectedJavaProject[] elements = provider.getElements();
		
		assertEquals(2, elements.length);
		assertEquals(new SelectedJavaProject(testProject1.getJavaProject(), true), elements[0]);
		assertEquals(new SelectedJavaProject(testProject2.getJavaProject(), false), elements[1]);
		
		Object[] checkedElements = provider.getCheckedElements();
		assertEquals(1, checkedElements.length);
		assertEquals(new SelectedJavaProject(testProject1.getJavaProject(), true), checkedElements[0]);
	}

	private void setJumpTarget(TestProject javaProject, TestProject testProject) throws CoreException {
		javaProject.getJavaProject().getProject().setPersistentProperty(ProjectProperties.JUMP_TARGETS_PROPERTY, testProject.getProject().getName());
	}
}
