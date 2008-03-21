package org.moreunit.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.moreunit.ProjectTestCase;
import org.moreunit.TestProject;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.preferences.PreferencesConverter;

public class PluginToolsTest extends ProjectTestCase {
	
	public void testGetJavaProjectsFromWorkspace() throws CoreException {
		new TestProject("anotherProject");
		List<IJavaProject> javaProjectsFromWorkspace = PluginTools.getJavaProjectsFromWorkspace();
		assertEquals(2, javaProjectsFromWorkspace.size());
	}


}