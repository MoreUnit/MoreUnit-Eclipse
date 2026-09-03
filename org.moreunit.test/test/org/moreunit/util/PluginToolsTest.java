package org.moreunit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.moreunit.util.PluginTools.getPathStringWithoutProjectName;
import static org.moreunit.util.PluginTools.guessSourceFolderCorrespondingToTestFolder;
import static org.moreunit.util.PluginTools.guessTestFolderCorrespondingToMainSrcFolder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.moreunit.test.workspace.WorkspaceHelper;

public class PluginToolsTest
{
    private final Set<IProject> projectsToDeleteAfterTest = new HashSet<>();

    @AfterEach
    public void deleteCreatedProjects() throws Exception
    {
        for (final IProject project : projectsToDeleteAfterTest)
            project.delete(true, null);
    }

    @Test
    public void getJavaProjectsFromWorkspace() throws Exception
    {
        // given
        createProject("FirstProject");
        createProject("SecondProject");

        // when
        final List<IJavaProject> javaProjectsFromWorkspace = PluginTools.getJavaProjectsFromWorkspace();

        // then
        assertEquals(2, javaProjectsFromWorkspace.size());
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_source_folder_when_only_one_source_folder() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("src/folder");
        final IPackageFragmentRoot testSrcFolder = project.getSourceFolder("src/folder");

        // when
        final IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertEquals(mainSrcFolder, testSrcFolder);
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_main_folder_when_test_folder_follows_maven_conventions() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("src/test/java", "src/main/java");
        final IPackageFragmentRoot testSrcFolder = project.getSourceFolder("src/test/java");

        // when
        final IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertEquals(mainSrcFolder, project.getSourceFolder("src/main/java"));
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_main_folder_regardless_of_the_language_when_test_folder_follows_maven_conventions_and_a_different_lanague_is_used_for_tests() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("src/main/java", "src/test/groovy");
        final IPackageFragmentRoot testSrcFolder = project.getSourceFolder("src/test/groovy");

        // when
        final IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertEquals(mainSrcFolder, project.getSourceFolder("src/main/java"));
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_main_folder_regardless_of_the_language_when_test_folder_follows_maven_conventions_and_a_different_lanague_is_used_for_tests__2() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("src/main/resources", "src/test/resources", "src/main/java", "src/test/groovy");
        final IPackageFragmentRoot testSrcFolder = project.getSourceFolder("src/test/groovy");

        // when
        final IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertEquals(mainSrcFolder, project.getSourceFolder("src/main/java"));
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_source_folder_not_containing_test_word() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("test", "src");
        final IPackageFragmentRoot testSrcFolder = project.getSourceFolder("test");

        // when
        final IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertEquals(mainSrcFolder, project.getSourceFolder("src"));
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_source_folder_not_containing_test_word_when_several_test_folders_contain_that_word() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("test/one", "source/folder", "test/two");
        final IPackageFragmentRoot testSrcFolder = project.getSourceFolder("test/two");

        // when
        final IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertEquals(mainSrcFolder, project.getSourceFolder("source/folder"));
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_source_folder_not_containing_junit_word() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("src", "junit");
        final IPackageFragmentRoot testSrcFolder = project.getSourceFolder("junit");

        // when
        final IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertEquals(mainSrcFolder, project.getSourceFolder("src"));
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_other_source_folder_when_no_clever_guess_can_be_made() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("one", "two");
        final IPackageFragmentRoot testSrcFolder = project.getSourceFolder("one");

        // when
        final IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertEquals(mainSrcFolder, project.getSourceFolder("two"));
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_another_source_folder_when_no_clever_guess_can_be_made() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("one", "two", "three");
        final IPackageFragmentRoot testSrcFolder = project.getSourceFolder("one");

        // when
        final IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertEquals(mainSrcFolder, project.getSourceFolder("two"));
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_return_source_folder_when_only_one_source_folder() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("src/folder");
        final IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("src/folder");

        // when
        final IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder);

        // then
        assertEquals(testSrcFolder, mainSrcFolder);
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_return_test_folder_when_main_folder_follows_maven_conventions() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("src/test/java", "src/main/java");
        final IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("src/main/java");

        // when
        final IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder);

        // then
        assertEquals(testSrcFolder, project.getSourceFolder("src/test/java"));
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_return_test_folder_regardless_of_the_language_when_main_folder_follows_maven_conventions_and_a_different_lanague_is_used_for_sources() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("src/main/java", "src/test/groovy");
        final IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("src/main/java");

        // when
        final IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder);

        // then
        assertEquals(testSrcFolder, project.getSourceFolder("src/test/groovy"));
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_return_test_folder_regardless_of_the_language_when_main_folder_follows_maven_conventions_and_a_different_lanague_is_used_for_sources__2() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("src/main/resources", "src/test/resources", "src/test/groovy", "src/main/java");
        final IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("src/main/java");

        // when
        final IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder);

        // then
        assertEquals(testSrcFolder, project.getSourceFolder("src/test/groovy"));
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_return_source_folder_named_test() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("test", "src");
        final IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("src");

        // when
        final IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder);

        // then
        assertEquals(testSrcFolder, project.getSourceFolder("test"));
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_return_other_source_folder_when_no_clever_guess_can_be_made() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("one", "two");
        final IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("one");

        // when
        final IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder);

        // then
        assertEquals(testSrcFolder, project.getSourceFolder("two"));
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_return_another_source_folder_when_no_clever_guess_can_be_made() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("one", "two", "three");
        final IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("one");

        // when
        final IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder);

        // then
        assertEquals(testSrcFolder, project.getSourceFolder("two"));
    }

    @Test
    public void getTestPackageName_should_return_unchanged_package_when_no_prefix_nor_suffix_is_configured()
    {
        assertEquals("com.foo", PluginTools.getTestPackageName("com.foo", projectPreferences(null, null)));
    }

    @Test
    public void getTestPackageName_should_add_prefix_when_configured()
    {
        assertEquals("test.com.foo", PluginTools.getTestPackageName("com.foo", projectPreferences("test", null)));
    }

    @Test
    public void getTestPackageName_should_add_suffix_when_configured()
    {
        assertEquals("com.foo.it", PluginTools.getTestPackageName("com.foo", projectPreferences(null, "it")));
    }

    @Test
    public void getTestPackageName_should_add_prefix_and_suffix_when_both_are_configured()
    {
        assertEquals("test.com.foo.it", PluginTools.getTestPackageName("com.foo", projectPreferences("test", "it")));
    }

    private org.moreunit.preferences.Preferences.ProjectPreferences projectPreferences(String prefix, String suffix)
    {
        final org.moreunit.preferences.Preferences.ProjectPreferences prefs = mock(org.moreunit.preferences.Preferences.ProjectPreferences.class);
        when(prefs.getPackagePrefix()).thenReturn(prefix);
        when(prefs.getPackageSuffix()).thenReturn(suffix);
        return prefs;
    }

    @Test
    public void createPackageFragmentRoot_should_return_existing_source_folder() throws Exception
    {
        final IJavaProject project = createProject("CreatePFRProject");
        WorkspaceHelper.createSourceFolderInProject(project, "src/test/java");

        final IPackageFragmentRoot root = PluginTools.createPackageFragmentRoot("CreatePFRProject", "src/test/java");

        assertNotNull(root);
        assertEquals("src/test/java", getPathStringWithoutProjectName(root));
    }

    @Test
    public void createPackageFragmentRoot_should_return_null_when_source_folder_does_not_exist() throws Exception
    {
        createProject("CreatePFRProject2");

        assertNull(PluginTools.createPackageFragmentRoot("CreatePFRProject2", "src/doesNotExist"));
    }

    @Test
    public void getOpenEditorPart_should_not_fail()
    {
        // smoke test for the workbench lookup (a missing page yields null)
        PluginTools.getOpenEditorPart();
    }

    @Test
    public void should_return_empty_string_when_source_folder_is_null()
    {
        assertEquals("", getPathStringWithoutProjectName(null));
    }

    @Test
    public void should_return_null_guesses_when_project_has_no_source_folders() throws Exception
    {
        final IJavaProject project = mock(IJavaProject.class);
        when(project.getPackageFragmentRoots()).thenReturn(new IPackageFragmentRoot[0]);
        final IPackageFragmentRoot folder = mock(IPackageFragmentRoot.class);

        assertNull(guessSourceFolderCorrespondingToTestFolder(project, folder));
        assertNull(guessTestFolderCorrespondingToMainSrcFolder(project, folder));
        assertNull(guessTestFolderCorrespondingToMainSrcFolder(project, folder, "java"));
    }

    @Test
    public void should_filter_out_source_folders_excluding_java_files() throws Exception
    {
        final IPackageFragmentRoot excluded = newSourceRoot("src/main/java", new IPath[] { new Path("**/*.java") });
        final IPackageFragmentRoot plain = newSourceRoot("src/test/java", null);
        final IPackageFragmentRoot otherExclusions = newSourceRoot("src/other/java", new IPath[] { new Path("other/**") });

        final IJavaProject project = mock(IJavaProject.class);
        when(project.getPackageFragmentRoots()).thenReturn(new IPackageFragmentRoot[] { excluded, plain, otherExclusions });

        final List<IPackageFragmentRoot> folders = PluginTools.findJavaSourceFoldersFor(project);

        assertEquals(2, folders.size());
        assertTrue(folders.contains(plain));
        assertTrue(folders.contains(otherExclusions));
        assertFalse(folders.contains(excluded));
    }

    @Test
    public void should_keep_source_folder_when_exclusions_cannot_be_read() throws Exception
    {
        final IPackageFragmentRoot root = mock(IPackageFragmentRoot.class);
        when(root.isArchive()).thenReturn(false);
        final JavaModelException failure = mock(JavaModelException.class);
        when(root.getRawClasspathEntry()).thenThrow(failure);
        when(root.getPath()).thenReturn(new Path("/aProject/src/main/java"));

        final IJavaProject project = mock(IJavaProject.class);
        when(project.getPackageFragmentRoots()).thenReturn(new IPackageFragmentRoot[] { root });

        assertTrue(PluginTools.findJavaSourceFoldersFor(project).contains(root));
    }

    @Test
    public void should_return_no_source_folder_when_package_fragment_roots_cannot_be_read() throws Exception
    {
        final IJavaProject project = mock(IJavaProject.class);
        when(project.getPackageFragmentRoots()).thenThrow(mock(JavaModelException.class));

        assertTrue(PluginTools.getAllSourceFolderFromProject(project).isEmpty());
    }

    @Test
    public void should_return_first_folder_when_all_folders_equal_test_folder() throws Exception
    {
        final IPackageFragmentRoot folder = newSourceRoot("one", null);

        final IJavaProject project = mock(IJavaProject.class);
        when(project.getPackageFragmentRoots()).thenReturn(new IPackageFragmentRoot[] { folder, folder });

        assertSame(folder, guessSourceFolderCorrespondingToTestFolder(project, folder));
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_maven_main_folder_among_several_folders() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("src/test/java", "src/main/java", "other");
        final IPackageFragmentRoot testSrcFolder = project.getSourceFolder("src/test/java");

        // when
        final IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertEquals(project.getSourceFolder("src/main/java"), mainSrcFolder);
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_fall_back_when_all_folders_contain_test_keyword() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("test/one", "test/two", "test/three");
        final IPackageFragmentRoot testSrcFolder = project.getSourceFolder("test/two");

        // when
        final IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertEquals(project.getSourceFolder("test/one"), mainSrcFolder);
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_return_folder_named_test_among_several_folders() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("test", "src", "other");
        final IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("src");

        // when
        final IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder);

        // then
        assertEquals(project.getSourceFolder("test"), testSrcFolder);
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_use_explicit_language_for_maven_test_folder() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("src/main/java", "src/test/java", "src/test/groovy");
        final IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("src/main/java");

        // when
        final IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder, "groovy");

        // then
        assertEquals(project.getSourceFolder("src/test/groovy"), testSrcFolder);
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_return_other_maven_test_folder_when_language_differs() throws Exception
    {
        // given
        final Project project = createAProjectWithSourceFolders("src/main/java", "src/test/scala", "other");
        final IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("src/main/java");

        // when
        final IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder);

        // then
        assertEquals(project.getSourceFolder("src/test/scala"), testSrcFolder);
    }

    @Test
    public void createPackageFragmentRoot_should_return_null_when_java_model_cannot_be_read() throws Exception
    {
        final IProject plainProject = ResourcesPlugin.getWorkspace().getRoot().getProject("PlainPFRProject");
        if(! plainProject.exists())
        {
            plainProject.create(null);
        }
        plainProject.open(null);
        projectsToDeleteAfterTest.add(plainProject);

        assertNull(PluginTools.createPackageFragmentRoot("PlainPFRProject", "src"));
    }

    private IPackageFragmentRoot newSourceRoot(String projectRelativePath, IPath[] exclusions) throws Exception
    {
        final IPackageFragmentRoot root = mock(IPackageFragmentRoot.class);
        when(root.isArchive()).thenReturn(false);
        final IClasspathEntry entry = mock(IClasspathEntry.class);
        when(entry.getEntryKind()).thenReturn(IClasspathEntry.CPE_SOURCE);
        when(entry.getExclusionPatterns()).thenReturn(exclusions);
        when(root.getRawClasspathEntry()).thenReturn(entry);
        when(root.getPath()).thenReturn(new Path("/aProject/" + projectRelativePath));
        return root;
    }

    private IJavaProject createProject(String name) throws Exception
    {
        final IJavaProject project = WorkspaceHelper.createJavaProject(name);
        projectsToDeleteAfterTest.add(project.getProject());
        return project;
    }

    private Project createAProjectWithSourceFolders(String... sourceFolderNames) throws Exception
    {
        final IJavaProject project = createProject("aProject");
        for (final String sourceFolder : sourceFolderNames)
            WorkspaceHelper.createSourceFolderInProject(project, sourceFolder);
        return new Project(project);
    }

    private static class Project
    {
        private final IJavaProject project;

        public Project(IJavaProject project)
        {
            this.project = project;
        }

        public IJavaProject get()
        {
            return project;
        }

        public IPackageFragmentRoot getSourceFolder(String name) throws JavaModelException
        {
            for (final IPackageFragmentRoot srcFolder : project.getPackageFragmentRoots())
                if(getPathStringWithoutProjectName(srcFolder).equals(name))
                    return srcFolder;
            return null;
        }
    }
}
