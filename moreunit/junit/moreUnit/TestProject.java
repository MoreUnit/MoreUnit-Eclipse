package moreUnit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IPluginRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.ITypeNameRequestor;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.ui.callhierarchy.ICallHierarchyViewPart;

/**
 * @author vera
 * 26.12.2005 18:59:03
 */
public class TestProject {
	
	public IProject project;
	public IJavaProject javaProject;
	private IPackageFragmentRoot sourceFolder;
	
	public TestProject() throws CoreException {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		project = workspaceRoot.getProject("AProject");
		project.create(null);
		project.open(null);
		
		javaProject = JavaCore.create(project);
		IFolder classFolder = createClassFolder();
		setJavaNature();
		javaProject.setRawClasspath(new IClasspathEntry[0], null);
		createOutputFolder(classFolder);
		addSystemLibraries();
	}
	
	public IProject getProject() {
		return project;
	}
	
	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public void addJar(String plugin, String jar) throws MalformedURLException, IOException, JavaModelException {
		Path result = findFileInPlugin(plugin, jar);
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = JavaCore.newLibraryEntry(result, null, null);
		javaProject.setRawClasspath(newEntries, null);
	}
	
	public IPackageFragment createPackage(String name) throws CoreException {
		if(sourceFolder == null)
			sourceFolder = createSourceFolder();
		
		return sourceFolder.createPackageFragment(name, false, null);
	}
	
	public IType createType(IPackageFragment pack, String cuName, String source) throws JavaModelException {
		StringBuffer buf = new StringBuffer();
		buf.append("package "+pack.getElementName()+";\n");
		buf.append("\n");
		buf.append(source);
		ICompilationUnit compilationUnit = pack.createCompilationUnit(cuName, buf.toString(), false, null);
		return compilationUnit.getTypes()[0];
	}
	
	public void dispose() throws CoreException {
		waitForIndexer();
		project.delete(true, true, null);
	}
	
	private IFolder createClassFolder() throws CoreException {
		IFolder classFolder = project.getFolder("classes");
		classFolder.create(false, true, null);
		return classFolder;
	}
	
	private void setJavaNature() throws CoreException {
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);
	}
	
	private void createOutputFolder(IFolder classFolder) throws JavaModelException {
		IPath outputLocation = classFolder.getFullPath();
		javaProject.setOutputLocation(outputLocation, null);
	}
	
	private IPackageFragmentRoot createSourceFolder() throws CoreException {
		IFolder folder = project.getFolder("src");
		folder.create(false, true, null);
		IPackageFragmentRoot fragmentRoot = javaProject.getPackageFragmentRoot(folder);
		
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntires = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntires, 0, oldEntries.length);
		newEntires[oldEntries.length] = JavaCore.newSourceEntry(fragmentRoot.getPath());
		javaProject.setRawClasspath(newEntires, null);
		return fragmentRoot;
	}
	
	private void addSystemLibraries() throws JavaModelException {
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		// TODO
		//newEntries[oldEntries.length] = JavaRuntime.getDefaultJREContainerEntry();
		javaProject.setRawClasspath(newEntries, null);
	}
	
	private Path findFileInPlugin(String plugin, String file) throws MalformedURLException, IOException {
		IPluginRegistry registry = Platform.getPluginRegistry();
		IPluginDescriptor descriptor = registry.getPluginDescriptor(plugin);
		URL pluginURL = descriptor.getInstallURL();
		URL jarURL = new URL(pluginURL, file);
		URL localJarURL = Platform.asLocalURL(jarURL);
		return new Path(localJarURL.getPath());
	}
	
	private void waitForIndexer() throws JavaModelException {
		new SearchEngine()
			.searchAllTypeNames(
					ResourcesPlugin.getWorkspace(),
					null,
					null,
					IJavaSearchConstants.EXACT_MATCH,
					IJavaSearchConstants.CASE_SENSITIVE,
					IJavaSearchConstants.CLASS,
					SearchEngine.createJavaSearchScope(new JavaElement[0]), new ITypeNameRequestor() {
						public void acceptClass(char[] packageName, char[] simpleTypeName, char[][] enclosingTypeNames, String path) {}
						public void acceptInterface(char[] packageName, char[] simpleTypeName, char[][] enclosingTypeNames, String path) {}
					}, 
					IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, 
					null);
	}
}
