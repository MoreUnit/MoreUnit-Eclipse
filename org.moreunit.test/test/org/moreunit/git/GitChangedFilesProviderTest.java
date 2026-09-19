package org.moreunit.git;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;

/**
 * Checks that the changed files reported by JGit are mapped to the files of an
 * Eclipse project. The project created by the test framework lives in the
 * workspace, so the repository is created around it.
 */
@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "com:Foo", testCls = "com:FooTest")
public class GitChangedFilesProviderTest extends ContextTestCase
{
    private Git git;
    private Path projectDirectory;

    @BeforeEach
    public void createRepositoryAroundProject() throws Exception
    {
        projectDirectory = context.getProjectHandler().get().getProject().getLocation().toFile().toPath().toAbsolutePath();
        git = Git.init().setDirectory(projectDirectory.toFile()).call();
    }

    @AfterEach
    public void closeRepository()
    {
        if(git != null)
        {
            git.close();
        }
    }

    @Test
    public void changedJavaFiles_should_return_the_modified_java_file() throws Exception
    {
        commitAll();
        final Path fooFile = javaFile("src/com/Foo.java");
        Files.writeString(fooFile, Files.readString(fooFile, UTF_8) + "\n// changed\n", UTF_8);

        final IJavaProject project = context.getProjectHandler().get();

        assertEquals(Set.of(fooFile.toAbsolutePath()), absolutePathsOf(new GitChangedFilesProvider().changedJavaFiles(project)));
    }

    @Test
    public void changedJavaFiles_should_return_the_untracked_java_file() throws Exception
    {
        commitAll();
        final Path newFile = javaFile("src/com/NewClass.java");
        Files.writeString(newFile, "package com;\npublic class NewClass {}\n", UTF_8);

        final IJavaProject project = context.getProjectHandler().get();

        assertEquals(Set.of(newFile.toAbsolutePath()), absolutePathsOf(new GitChangedFilesProvider().changedJavaFiles(project)));
    }

    @Test
    public void changedJavaFiles_should_ignore_non_java_files() throws Exception
    {
        commitAll();
        Files.writeString(projectDirectory.resolve("notes.txt"), "not java", UTF_8);

        final IJavaProject project = context.getProjectHandler().get();

        assertTrue(new GitChangedFilesProvider().changedJavaFiles(project).isEmpty());
    }

    @Test
    public void changedJavaFiles_should_return_nothing_when_the_project_is_not_in_a_repository() throws Exception
    {
        git.close();
        git = null;
        deleteRecursively(projectDirectory.resolve(".git"));

        final IJavaProject project = context.getProjectHandler().get();

        assertTrue(new GitChangedFilesProvider().changedJavaFiles(project).isEmpty());
    }

    private Path javaFile(String relativePath)
    {
        return projectDirectory.resolve(relativePath);
    }

    private void commitAll() throws Exception
    {
        git.add().addFilepattern(".").call();
        git.commit().setMessage("initial").setAuthor("MoreUnit", "moreunit@example.org").call();
    }

    private static Set<Path> absolutePathsOf(Iterable<Path> paths)
    {
        final Set<Path> result = new java.util.LinkedHashSet<>();
        paths.forEach(path -> result.add(path.toAbsolutePath()));
        return result;
    }

    private static void deleteRecursively(Path directory) throws Exception
    {
        if(! Files.exists(directory))
        {
            return;
        }
        try (var paths = Files.walk(directory))
        {
            paths.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                try
                {
                    Files.deleteIfExists(path);
                }
                catch (final Exception e)
                {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
