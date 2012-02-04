package org.moreunit.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;

public class PluginTools
{
    private static final Pattern MAVEN_RESOURCE_FOLDER = Pattern.compile("src/[^/]+/resources");

    public static IEditorPart getOpenEditorPart()
    {
        IWorkbench wb = PlatformUI.getWorkbench();
        IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();

        if(page != null)
            return page.getActiveEditor();
        else
            return null;
    }

    public static boolean isJavaFile(IWorkbenchPart part)
    {
        if(! (part instanceof IEditorPart))
            return false;

        IFile file = (IFile) ((IEditorPart) part).getEditorInput().getAdapter(IFile.class);
        if(file == null)
            return false;

        return "java".equals(file.getFileExtension());
    }

    public static IPackageFragmentRoot createPackageFragmentRoot(String projectName, String folderName)
    {
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        IJavaProject javaProject = JavaCore.create(project);
        try
        {
            for (IPackageFragmentRoot aSourceFolder : javaProject.getPackageFragmentRoots())
            {
                if(folderName.equals(PluginTools.getPathStringWithoutProjectName(aSourceFolder)))
                    return aSourceFolder;
            }
        }
        catch (JavaModelException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }

        return null;
    }

    public static List<IJavaProject> getJavaProjectsFromWorkspace()
    {
        List<IJavaProject> result = new ArrayList<IJavaProject>();
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        for (IProject aProject : projects)
        {
            try
            {
                if(aProject.isAccessible() && aProject.hasNature(JavaCore.NATURE_ID))
                {
                    result.add(JavaCore.create(aProject));
                }
            }
            catch (CoreException e)
            {
                LogHandler.getInstance().handleExceptionLog(e);
            }
        }

        return result;
    }

    public static IPackageFragmentRoot getSourceFolder(ICompilationUnit compilationUnit)
    {
        IJavaElement element = compilationUnit;
        while (! (element instanceof IPackageFragmentRoot))
        {
            element = element.getParent();
        }

        return (IPackageFragmentRoot) element;
    }

    public static List<IPackageFragmentRoot> getAllSourceFolderFromProject(IJavaProject javaProject)
    {
        List<IPackageFragmentRoot> resultList = new ArrayList<IPackageFragmentRoot>();
        try
        {
            for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots())
            {
                if(! root.isArchive() && root.getRawClasspathEntry().getEntryKind() == IClasspathEntry.CPE_SOURCE)
                {
                    resultList.add(root);
                }
            }
        }
        catch (JavaModelException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }

        return resultList;
    }
    
    public static List<IPackageFragmentRoot> findJavaSourceFoldersFor(IJavaProject project)
    {
        List<IPackageFragmentRoot> javaSrcFolders = new ArrayList<IPackageFragmentRoot>();

        for (IPackageFragmentRoot sourceFolder : getAllSourceFolderFromProject(project))
        {
            String sourceFolderPath = PluginTools.getPathStringWithoutProjectName(sourceFolder);

            if(! (excludesJavaFiles(sourceFolder) || isMavenLikeResourceFolder(sourceFolderPath)))
            {
                javaSrcFolders.add(sourceFolder);
            }
        }

        return javaSrcFolders;
    }

    private static boolean excludesJavaFiles(IPackageFragmentRoot srcFolder)
    {
        try
        {
            IPath[] exclusionPatterns = srcFolder.getRawClasspathEntry().getExclusionPatterns();
            if(exclusionPatterns != null)
            {
                for (IPath pattern : exclusionPatterns)
                {
                    if(pattern.toString().equals("**/*.java"))
                    {
                        return true;
                    }
                }
            }
        }
        catch (JavaModelException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }

        return false;
    }
    
    private static boolean isMavenLikeResourceFolder(String srcFolderPath)
    {
        return MAVEN_RESOURCE_FOLDER.matcher(srcFolderPath).matches();
    }

    public static String getPathStringWithoutProjectName(IPackageFragmentRoot sourceFolder)
    {
        if(sourceFolder == null)
            return StringConstants.EMPTY_STRING;

        return sourceFolder.getPath().removeFirstSegments(1).toString();
    }
    
    /**
     * Returns the name of the test-package, which depends on the preferences.
     * If the user configured a test package prefix or suffix it must be added to the test package name.
     * 
     * @param cutPackageName
     * @param preferences
     * @param javaProject
     * @return
     */
    public static String getTestPackageName(String cutPackageName, Preferences preferences, IJavaProject javaProject)
    {
        String testPackagePrefix = preferences.getTestPackagePrefix(javaProject);
        String testPackageSuffix = preferences.getTestPackageSuffix(javaProject);
        String testPackageName = cutPackageName;
        
        if(!BaseTools.isStringTrimmedEmpty(testPackagePrefix))
        {
            testPackageName = String.format("%s.%s", testPackagePrefix, testPackageName);
        }
        
        if(!BaseTools.isStringTrimmedEmpty(testPackageSuffix))
        {
            testPackageName = String.format("%s.%s", testPackageName, testPackageSuffix);
        }
        
        return testPackageName;
    }
}
