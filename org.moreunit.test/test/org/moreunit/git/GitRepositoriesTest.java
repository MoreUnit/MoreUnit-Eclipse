package org.moreunit.git;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

public class GitRepositoriesTest
{
    // the cleanup is done by the test itself: JGit may keep file handles for a
    // short while on Windows (see GitTestFiles)
    @TempDir(cleanup = CleanupMode.NEVER)
    Path tempDir;

    @AfterEach
    public void deleteTemporaryFiles()
    {
        GitTestFiles.deleteRecursively(tempDir);
    }

    @Test
    public void repositoryOf_should_find_the_repository_containing_a_directory() throws Exception
    {
        final Path repositoryDirectory = Files.createDirectories(tempDir.resolve("repository"));
        try (Git git = Git.init().setDirectory(repositoryDirectory.toFile()).call();
                GitRepository repository = GitRepositories.repositoryOf(repositoryDirectory).orElseThrow())
        {
            assertEquals(repositoryDirectory.toAbsolutePath(), repository.getRepository().getWorkTree().toPath().toAbsolutePath());
        }
    }

    @Test
    public void repositoryOf_should_find_the_repository_from_a_nested_directory() throws Exception
    {
        final Path repositoryDirectory = Files.createDirectories(tempDir.resolve("repository"));
        try (Git git = Git.init().setDirectory(repositoryDirectory.toFile()).call())
        {
            final Path nestedDirectory = Files.createDirectories(repositoryDirectory.resolve("src/main/java"));

            try (GitRepository repository = GitRepositories.repositoryOf(nestedDirectory).orElseThrow())
            {
                assertEquals(repositoryDirectory.toAbsolutePath(), repository.getRepository().getWorkTree().toPath().toAbsolutePath());
            }
        }
    }

    @Test
    public void repositoryOf_should_return_empty_when_the_directory_is_not_in_a_repository()
    {
        assertTrue(GitRepositories.repositoryOf(tempDir).isEmpty());
    }

    /**
     * A linked working tree (and a submodule) has a ".git" file pointing to
     * the repository instead of a ".git" directory: JGit resolves it, so
     * MoreUnit can inspect it. The file is written by hand so that the test
     * does not need the "git" executable.
     */
    @Test
    public void repositoryOf_should_support_a_git_file_pointing_to_a_repository() throws Exception
    {
        final Path repositoryDirectory = Files.createDirectories(tempDir.resolve("repository"));
        try (Git git = Git.init().setDirectory(repositoryDirectory.toFile()).call())
        {
            final Path workingTreeDirectory = Files.createDirectories(tempDir.resolve("working-tree"));
            // git accepts forward slashes, also on Windows
            final String gitDirectory = repositoryDirectory.resolve(".git").toString().replace('\\', '/');
            Files.writeString(workingTreeDirectory.resolve(".git"), "gitdir: " + gitDirectory + "\n");

            try (GitRepository repository = GitRepositories.repositoryOf(workingTreeDirectory).orElseThrow())
            {
                // toRealPath: Windows may resolve short names differently
                assertEquals(repositoryDirectory.resolve(".git").toRealPath(), repository.getRepository().getDirectory().toPath().toRealPath());
            }
        }
    }
}
