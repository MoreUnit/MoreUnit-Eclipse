package org.moreunit.util;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
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
import org.moreunit.core.util.StringConstants;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences.ProjectPreferences;

public class PluginTools
{
    private static final Pattern MAVEN_MAIN_FOLDER = Pattern.compile("src/main/([^/]+)");
    private static final Pattern MAVEN_RESOURCE_FOLDER = Pattern.compile("src/[^/]+/resources");
    private static final Pattern MAVEN_TEST_FOLDER = Pattern.compile("src/test/([^/]+)");

    public static IEditorPart getOpenEditorPart()
    {
        IWorkbench wb = PlatformUI.getWorkbench();
        IWorkbenchWindow window = wb.getActiveWorkbenchWindow();

        if(window == null)
            return null;

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
     * If the user configured a test package prefix or suffix it must be added
     * to the test package name.
     *
     * @param cutPackageName
     * @param preferences
     * @param javaProject
     * @return
     */
    public static String getTestPackageName(String cutPackageName, ProjectPreferences preferences)
    {
        String testPackagePrefix = preferences.getPackagePrefix();
        String testPackageSuffix = preferences.getPackageSuffix();
        String testPackageName = cutPackageName;

        if(testPackagePrefix != null)
        {
            testPackageName = String.format("%s.%s", testPackagePrefix, testPackageName);
        }

        if(testPackageSuffix != null)
        {
            testPackageName = String.format("%s.%s", testPackageName, testPackageSuffix);
        }

        return testPackageName;
    }

    public static IPackageFragmentRoot guessSourceFolderCorrespondingToTestFolder(IJavaProject project, IPackageFragmentRoot testFolder)
    {
        List<IPackageFragmentRoot> allSourceFolders = getAllSourceFolderFromProject(project);
        if(allSourceFolders.isEmpty())
            return null;

        if(allSourceFolders.size() == 1)
            return allSourceFolders.get(0);

        if(allSourceFolders.size() == 2)
            return firstSourceFolderNotEqualTo(allSourceFolders, testFolder);

        IPackageFragmentRoot likelySourceFolder = findLikelySourceFolder(allSourceFolders, testFolder);
        if(likelySourceFolder != null)
            return likelySourceFolder;

        // last chance, user better has to properly configure MoreUnit...
        return firstSourceFolderNotEqualTo(allSourceFolders, testFolder);
    }

    private static IPackageFragmentRoot findLikelySourceFolder(List<IPackageFragmentRoot> allSourceFolders, IPackageFragmentRoot testFolder)
    {
        String testFolderPath = getPathStringWithoutProjectName(testFolder);

        IPackageFragmentRoot srcFolder = findMavenLikeSrcFolderFor(allSourceFolders, testFolderPath);
        if(srcFolder != null)
            return srcFolder;

        return findSourceFolderNotContainingTestKeyword(allSourceFolders, testFolderPath);
    }

    private static IPackageFragmentRoot findSourceFolderNotContainingTestKeyword(List<IPackageFragmentRoot> allSourceFolders, String testFolderPath)
    {
        String testKeyword = findTestKeyword(testFolderPath);
        if(testKeyword == null)
            return null;

        for (IPackageFragmentRoot folder : allSourceFolders)
            if(! getPathStringWithoutProjectName(folder).matches(".*\\b" + testKeyword + "\\b.*"))
                return folder;

        return null;
    }

    private static IPackageFragmentRoot firstSourceFolderNotEqualTo(List<IPackageFragmentRoot> allSourceFolders, IPackageFragmentRoot testFolder)
    {
        for (IPackageFragmentRoot folder : allSourceFolders)
            if(! folder.equals(testFolder))
                return folder;

        // if that guess is wrong, then user will have to choose manually
        return allSourceFolders.get(0);
    }

    private static IPackageFragmentRoot findMavenLikeSrcFolderFor(List<IPackageFragmentRoot> allSourceFolders, String testFolderPath)
    {
        Matcher matcher = MAVEN_TEST_FOLDER.matcher(testFolderPath);
        if(! matcher.matches())
            return null;

        String languagePart = matcher.group(1);
        String mainSourceFolderForLanguage = "src/main/" + languagePart;

        for (IPackageFragmentRoot folder : allSourceFolders)
            if(getPathStringWithoutProjectName(folder).equals(mainSourceFolderForLanguage))
                return folder;

        // maybe production code and test code are not written using the same
        // language
        for (IPackageFragmentRoot folder : allSourceFolders)
        {
            String folderName = getPathStringWithoutProjectName(folder);
            if(folderName.startsWith("src/main/") && ! folderName.equals("src/main/resources"))
                return folder;
        }

        return null;
    }

    public static IPackageFragmentRoot guessTestFolderCorrespondingToMainSrcFolder(IJavaProject project, IPackageFragmentRoot mainSrcFolder)
    {
        List<IPackageFragmentRoot> allSourceFolders = getAllSourceFolderFromProject(project);
        if(allSourceFolders.isEmpty())
            return null;

        if(allSourceFolders.size() == 1)
            return allSourceFolders.get(0);

        if(allSourceFolders.size() == 2)
            return firstSourceFolderNotEqualTo(allSourceFolders, mainSrcFolder);

        IPackageFragmentRoot likelyTestFolder = findLikelyTestFolder(allSourceFolders, mainSrcFolder);
        if(likelyTestFolder != null)
            return likelyTestFolder;

        // last chance, user better has to properly configure MoreUnit...
        return firstSourceFolderNotEqualTo(allSourceFolders, mainSrcFolder);
    }

    private static IPackageFragmentRoot findLikelyTestFolder(List<IPackageFragmentRoot> allSourceFolders, IPackageFragmentRoot mainSrcFolder)
    {
        String mainSrcFolderPath = getPathStringWithoutProjectName(mainSrcFolder);

        IPackageFragmentRoot testSrcFolder = findMavenLikeTestFolderFor(allSourceFolders, mainSrcFolderPath);
        if(testSrcFolder != null)
            return testSrcFolder;

        // last attempt, just in case...
        for (IPackageFragmentRoot packageFragmentRoot : allSourceFolders)
            if(getPathStringWithoutProjectName(packageFragmentRoot).equals("test"))
                return packageFragmentRoot;

        return null;
    }

    private static IPackageFragmentRoot findMavenLikeTestFolderFor(List<IPackageFragmentRoot> allSourceFolders, String mainSrcFolderPath)
    {
        Matcher matcher = MAVEN_MAIN_FOLDER.matcher(mainSrcFolderPath);
        if(! matcher.matches())
            return null;

        String languagePart = matcher.group(1);
        String testSourceFolderForLanguage = "src/test/" + languagePart;

        for (IPackageFragmentRoot folder : allSourceFolders)
            if(getPathStringWithoutProjectName(folder).equals(testSourceFolderForLanguage))
                return folder;

        // maybe production code and test code are not written using the same
        // language
        for (IPackageFragmentRoot folder : allSourceFolders)
        {
            String folderName = getPathStringWithoutProjectName(folder);
            if(folderName.startsWith("src/test/") && ! folderName.equals("src/test/resources"))
                return folder;
        }

        return null;
    }

    private static String findTestKeyword(String testFolderPath)
    {
        for (String testKeyword : asList("test", "junit", "testng", "spec", "tst"))
            if(testFolderPath.toLowerCase().matches(".*\\b" + testKeyword + "\\b.*"))
                return testKeyword;

        return null;
    }
}
