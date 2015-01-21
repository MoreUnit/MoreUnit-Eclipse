package org.moreunit.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.moreunit.util.PluginTools.getPathStringWithoutProjectName;
import static org.moreunit.util.PluginTools.guessSourceFolderCorrespondingToTestFolder;
import static org.moreunit.util.PluginTools.guessTestFolderCorrespondingToMainSrcFolder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.Test;
import org.moreunit.test.workspace.WorkspaceHelper;

public class PluginToolsTest
{
    private Set<IProject> projectsToDeleteAfterTest = new HashSet<IProject>();

    @After
    public void deleteCreatedProjects() throws Exception
    {
        for (IProject project : projectsToDeleteAfterTest)
            project.delete(true, null);
    }

    @Test
    public void getJavaProjectsFromWorkspace() throws Exception
    {
        // given
        createProject("FirstProject");
        createProject("SecondProject");

        // when
        List<IJavaProject> javaProjectsFromWorkspace = PluginTools.getJavaProjectsFromWorkspace();

        // then
        assertThat(javaProjectsFromWorkspace).hasSize(2);
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_source_folder_when_only_one_source_folder() throws Exception
    {
        // given
        Project project = createAProjectWithSourceFolders("src/folder");
        IPackageFragmentRoot testSrcFolder = project.getSourceFolder("src/folder");

        // when
        IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertThat(mainSrcFolder).isEqualTo(testSrcFolder);
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_main_folder_when_test_folder_follows_maven_conventions() throws Exception
    {
        // given
        Project project = createAProjectWithSourceFolders("src/test/java", "src/main/java");
        IPackageFragmentRoot testSrcFolder = project.getSourceFolder("src/test/java");

        // when
        IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertThat(mainSrcFolder).isEqualTo(project.getSourceFolder("src/main/java"));
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_main_folder_regardless_of_the_language_when_test_folder_follows_maven_conventions_and_a_different_lanague_is_used_for_tests() throws Exception
    {
        // given
        Project project = createAProjectWithSourceFolders("src/main/java", "src/test/groovy");
        IPackageFragmentRoot testSrcFolder = project.getSourceFolder("src/test/groovy");

        // when
        IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertThat(mainSrcFolder).isEqualTo(project.getSourceFolder("src/main/java"));
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_main_folder_regardless_of_the_language_when_test_folder_follows_maven_conventions_and_a_different_lanague_is_used_for_tests__2() throws Exception
    {
        // given
        Project project = createAProjectWithSourceFolders("src/main/resources", "src/test/resources", "src/main/java", "src/test/groovy");
        IPackageFragmentRoot testSrcFolder = project.getSourceFolder("src/test/groovy");

        // when
        IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertThat(mainSrcFolder).isEqualTo(project.getSourceFolder("src/main/java"));
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_source_folder_not_containing_test_word() throws Exception
    {
        // given
        Project project = createAProjectWithSourceFolders("test", "src");
        IPackageFragmentRoot testSrcFolder = project.getSourceFolder("test");

        // when
        IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertThat(mainSrcFolder).isEqualTo(project.getSourceFolder("src"));
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_source_folder_not_containing_test_word_when_several_test_folders_contain_that_word() throws Exception
    {
        // given
        Project project = createAProjectWithSourceFolders("test/one", "source/folder", "test/two");
        IPackageFragmentRoot testSrcFolder = project.getSourceFolder("test/two");

        // when
        IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertThat(mainSrcFolder).isEqualTo(project.getSourceFolder("source/folder"));
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_source_folder_not_containing_junit_word() throws Exception
    {
        // given
        Project project = createAProjectWithSourceFolders("src", "junit");
        IPackageFragmentRoot testSrcFolder = project.getSourceFolder("junit");

        // when
        IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertThat(mainSrcFolder).isEqualTo(project.getSourceFolder("src"));
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_other_source_folder_when_no_clever_guess_can_be_made() throws Exception
    {
        // given
        Project project = createAProjectWithSourceFolders("one", "two");
        IPackageFragmentRoot testSrcFolder = project.getSourceFolder("one");

        // when
        IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertThat(mainSrcFolder).isEqualTo(project.getSourceFolder("two"));
    }

    @Test
    public void guessSourceFolderCorrespondingToTestFolder_should_return_another_source_folder_when_no_clever_guess_can_be_made() throws Exception
    {
        // given
        Project project = createAProjectWithSourceFolders("one", "two", "three");
        IPackageFragmentRoot testSrcFolder = project.getSourceFolder("one");

        // when
        IPackageFragmentRoot mainSrcFolder = guessSourceFolderCorrespondingToTestFolder(project.get(), testSrcFolder);

        // then
        assertThat(mainSrcFolder).isEqualTo(project.getSourceFolder("two"));
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_return_source_folder_when_only_one_source_folder() throws Exception
    {
        // given
        Project project = createAProjectWithSourceFolders("src/folder");
        IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("src/folder");

        // when
        IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder);

        // then
        assertThat(testSrcFolder).isEqualTo(mainSrcFolder);
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_return_test_folder_when_main_folder_follows_maven_conventions() throws Exception
    {
        // given
        Project project = createAProjectWithSourceFolders("src/test/java", "src/main/java");
        IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("src/main/java");

        // when
        IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder);

        // then
        assertThat(testSrcFolder).isEqualTo(project.getSourceFolder("src/test/java"));
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_return_test_folder_regardless_of_the_language_when_main_folder_follows_maven_conventions_and_a_different_lanague_is_used_for_sources() throws Exception
    {
        // given
        Project project = createAProjectWithSourceFolders("src/main/java", "src/test/groovy");
        IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("src/main/java");

        // when
        IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder);

        // then
        assertThat(testSrcFolder).isEqualTo(project.getSourceFolder("src/test/groovy"));
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_return_test_folder_regardless_of_the_language_when_main_folder_follows_maven_conventions_and_a_different_lanague_is_used_for_sources__2() throws Exception
    {
        // given
        Project project = createAProjectWithSourceFolders("src/main/resources", "src/test/resources", "src/test/groovy", "src/main/java");
        IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("src/main/java");

        // when
        IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder);

        // then
        assertThat(testSrcFolder).isEqualTo(project.getSourceFolder("src/test/groovy"));
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_return_source_folder_named_test() throws Exception
    {
        // given
        Project project = createAProjectWithSourceFolders("test", "src");
        IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("src");

        // when
        IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder);

        // then
        assertThat(testSrcFolder).isEqualTo(project.getSourceFolder("test"));
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_return_other_source_folder_when_no_clever_guess_can_be_made() throws Exception
    {
        // given
        Project project = createAProjectWithSourceFolders("one", "two");
        IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("one");

        // when
        IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder);

        // then
        assertThat(testSrcFolder).isEqualTo(project.getSourceFolder("two"));
    }

    @Test
    public void guessTestFolderCorrespondingToMainSrcFolder_should_return_another_source_folder_when_no_clever_guess_can_be_made() throws Exception
    {
        // given
        Project project = createAProjectWithSourceFolders("one", "two", "three");
        IPackageFragmentRoot mainSrcFolder = project.getSourceFolder("one");

        // when
        IPackageFragmentRoot testSrcFolder = guessTestFolderCorrespondingToMainSrcFolder(project.get(), mainSrcFolder);

        // then
        assertThat(testSrcFolder).isEqualTo(project.getSourceFolder("two"));
    }

    private IJavaProject createProject(String name) throws Exception
    {
        IJavaProject project = WorkspaceHelper.createJavaProject(name);
        projectsToDeleteAfterTest.add(project.getProject());
        return project;
    }

    private Project createAProjectWithSourceFolders(String... sourceFolderNames) throws Exception
    {
        IJavaProject project = createProject("aProject");
        for (String sourceFolder : sourceFolderNames)
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
            for (IPackageFragmentRoot srcFolder : project.getPackageFragmentRoots())
                if(getPathStringWithoutProjectName(srcFolder).equals(name))
                    return srcFolder;
            return null;
        }
    }
}
