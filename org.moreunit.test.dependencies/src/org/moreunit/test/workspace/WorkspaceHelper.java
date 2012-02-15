package org.moreunit.test.workspace;

import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.service.datalocation.Location;
import org.moreunit.test.context.StringUtils;
import org.eclipse.jdt.junit.JUnitCore;

/**
 * @author vera 29.11.2008 13:41:52
 */
public class WorkspaceHelper
{
    private static final String NEW_LINE = "\n";
    private static final String CLASSES_FOLDER = "classes";

    public static IJavaProject createJavaProject(String projectName) throws Exception
    {
        IProject project = createNewProject(projectName);
        return createJavaProjectFromProject(project);
    }

    private static IProject createNewProject(String projectName) throws CoreException
    {
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = workspaceRoot.getProject(projectName);

        // If project existed, delete if first, because create would throw an
        // exception
        // otherwise
        if(project.exists())
        {
            project.delete(true, true, null);
        }

        // Create and open project
        project.create(null);
        project.open(null);

        return project;
    }

    public static IJavaProject getJavaProject(String projectName)
    {
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = workspaceRoot.getProject(projectName);
        return (IJavaProject) project.getAdapter(IJavaProject.class);
    }

    private static IJavaProject createJavaProjectFromProject(IProject project) throws Exception
    {
        IJavaProject javaProject = JavaCore.create(project);
        IProjectDescription description = project.getDescription();
        description.setNatureIds(new String[] { JavaCore.NATURE_ID });
        project.setDescription(description, null);

        createNewClassFolder(javaProject, CLASSES_FOLDER);
        addDefaultJreToClassPath(javaProject);

        return javaProject;
    }
    
    public static void addContainerToProject(IJavaProject javaProject, IClasspathContainer container) throws IOException, JavaModelException
    {
        IClasspathEntry[] entriesToAdd = container.getClasspathEntries();
        IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
        IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + entriesToAdd.length];
        System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
        System.arraycopy(entriesToAdd, 0, newEntries, oldEntries.length, entriesToAdd.length);
        javaProject.setRawClasspath(newEntries, null);
    }

    private static IPackageFragmentRoot createNewClassFolder(IJavaProject javaProject, String classFolderName) throws CoreException
    {
        IFolder classFolder = javaProject.getProject().getFolder(classFolderName);
        classFolder.create(false, true, null);

        IPath outputLocation = classFolder.getFullPath();
        javaProject.setOutputLocation(outputLocation, null);

        IPackageFragmentRoot fragmentRoot = javaProject.getPackageFragmentRoot(classFolder);
        return fragmentRoot;
    }

    private static void addDefaultJreToClassPath(IJavaProject javaProject) throws JavaModelException
    {
        javaProject.setRawClasspath(new IClasspathEntry[0], null);

        IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
        IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];

        System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
        newEntries[oldEntries.length] = JavaRuntime.getDefaultJREContainerEntry();

        javaProject.setRawClasspath(newEntries, null);
    }

    public static void addJarToProject(IJavaProject javaProject, String jarName) throws IOException, JavaModelException
    {
        Path result = createPathForFilename(jarName);
        IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
        IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
        System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
        newEntries[oldEntries.length] = JavaCore.newLibraryEntry(result, null, null);
        javaProject.setRawClasspath(newEntries, null);
    }

    private static Path createPathForFilename(String filename) throws IOException
    {
        Location location = Platform.getInstallLocation();
        URL pluginURL = location.getURL();
        URL jarURL = new URL(pluginURL, filename);

        URL localJarURL = FileLocator.toFileURL(jarURL);

        return new Path(localJarURL.getPath());
    }

    public static IPackageFragment createNewPackageInSourceFolder(IPackageFragmentRoot sourceFolder, String packageName) throws JavaModelException
    {
        return sourceFolder.createPackageFragment(packageName, false, null);
    }

    public static void deleteProject(IJavaProject javaProject) throws CoreException
    {
        javaProject.getProject().delete(true, true, null);
    }

    public static IPackageFragmentRoot createSourceFolderInProject(IJavaProject javaProject, String sourceFolderName) throws CoreException
    {
        IFolder folder = javaProject.getProject().getFolder(sourceFolderName);
        if(folder.exists())
        {
            return javaProject.getPackageFragmentRoot(folder);
        }

        createFolder(javaProject, sourceFolderName);

        IPackageFragmentRoot fragmentRoot = javaProject.getPackageFragmentRoot(folder);

        IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
        IClasspathEntry[] newEntires = new IClasspathEntry[oldEntries.length + 1];
        System.arraycopy(oldEntries, 0, newEntires, 0, oldEntries.length);
        newEntires[oldEntries.length] = JavaCore.newSourceEntry(fragmentRoot.getPath());
        javaProject.setRawClasspath(newEntires, null);
        return fragmentRoot;
    }

    public static IType createJavaClass(IPackageFragment packageFragment, String javaClassName) throws JavaModelException
    {
        return createJavaType(packageFragment, javaClassName, JavaTypeKind.CLASS);
    }

    public static IType createJavaClassExtending(IPackageFragment packageFragment, String javaClassName, String parentClassName) throws JavaModelException
    {
        String declaration = String.format("public %1$s %2$s extends %3$s { %4$s%4$s } %4$s", JavaTypeKind.CLASS.toJavaCode(), javaClassName, parentClassName, NEW_LINE);
        String sourceCode = String.format("%s%s%s", getPackageDeclarationString(packageFragment), NEW_LINE, declaration);
        return createJavaType(packageFragment, javaClassName, sourceCode);
    }

    public static IType createJavaEnum(IPackageFragment packageFragment, String javaClassName) throws JavaModelException
    {
        return createJavaType(packageFragment, javaClassName, JavaTypeKind.ENUM);
    }

    private static IType createJavaType(IPackageFragment packageFragment, String javaClassName, JavaTypeKind type) throws JavaModelException
    {
        String sourceCode = String.format("%s%s%s", getPackageDeclarationString(packageFragment), NEW_LINE, getTypeDeclarationString(type, javaClassName));
        return createJavaType(packageFragment, javaClassName, sourceCode);
    }

    private static IType createJavaType(IPackageFragment packageFragment, String javaClassName, String sourceCode) throws JavaModelException
    {
        ICompilationUnit compilationUnit = packageFragment.createCompilationUnit(String.format("%s.java", javaClassName), sourceCode, false, null);
        return compilationUnit.getTypes()[0];
    }

    private static String getPackageDeclarationString(IPackageFragment packageFragment)
    {
        return String.format("package %s;%s", packageFragment.getElementName(), NEW_LINE);
    }

    private static String getTypeDeclarationString(JavaTypeKind type, String javaClassName)
    {
        return String.format("public %1$s %2$s { %3$s%3$s } %3$s", type.toJavaCode(), javaClassName, NEW_LINE);
    }

    public static IMethod createMethodInJavaType(IType javaType, String methodDeclaration, String methodSourceCode) throws JavaModelException
    {
        String completeMethodCodeString = String.format("%s{%s%s}", methodDeclaration, NEW_LINE, methodSourceCode);
        return javaType.createMethod(completeMethodCodeString, null, true, null);
    }

    public static void assertSameMethodName(IMethod method, MethodDeclaration methodDeclaration)
    {
        TestCase.assertEquals(method.getElementName(), methodDeclaration.getName().getFullyQualifiedName());
    }

    public static IFolder createFolder(IJavaProject project, String folderName) throws CoreException
    {
        IFolder srcFolder = project.getProject().getFolder(folderName);
        if(srcFolder.exists())
        {
            return srcFolder;
        }

        IFolder folder = null;

        for (String part : StringUtils.split(folderName, "/"))
        {
            if(folder == null)
            {
                folder = project.getProject().getFolder(part);
            }
            else
            {
                folder = folder.getFolder(part);
            }

            if(! folder.exists())
            {
                folder.create(false, true, null);
            }
        }

        return folder;
    }

    public static void deleteCompilationUnitsForTypes(IType[] types) throws JavaModelException
    {
        for (IType type : types)
        {
            type.getCompilationUnit().delete(true, null);
        }
    }
}
