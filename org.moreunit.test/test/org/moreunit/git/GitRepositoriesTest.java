package org.moreunit.git;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class GitRepositoriesTest
{
    @TempDir
    Path tempDir;

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
     * A linked working tree has a ".git" file pointing to the repository of
     * the main working tree: JGit resolves it, so MoreUnit can inspect it.
     * The working tree is created with the "git" executable, because JGit has
     * no API for this.
     */
    @Test
    public void repositoryOf_should_support_linked_working_trees() throws Exception
    {
        final Path mainDirectory = Files.createDirectories(tempDir.resolve("main"));
        final Path linkedDirectory = tempDir.resolve("linked");
        try (Git git = Git.init().setDirectory(mainDirectory.toFile()).call())
        {
            git.commit().setMessage("initial").setAuthor("MoreUnit", "moreunit@example.org").setAllowEmpty(true).call();
        }
        assumeTrue(createLinkedWorkingTree(mainDirectory, linkedDirectory), "the git executable is not available");

        try (GitRepository repository = GitRepositories.repositoryOf(linkedDirectory).orElseThrow())
        {
            assertEquals(linkedDirectory.toAbsolutePath(), repository.getRepository().getWorkTree().toPath().toAbsolutePath());
        }
    }

    private static boolean createLinkedWorkingTree(Path mainDirectory, Path linkedDirectory) throws Exception
    {
        final Process process = new ProcessBuilder("git", "-C", mainDirectory.toString(), "worktree", "add", linkedDirectory.toString(), "-b", "feature") //
                .redirectErrorStream(true).start();
        final String output = new String(process.getInputStream().readAllBytes(), UTF_8);
        return process.waitFor() == 0 && output.isEmpty();
    }
}
