package org.moreunit.git;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Exercises the Git support against a real (temporary) repository, created
 * with JGit - the Git implementation of Eclipse.
 */
public class GitWorkingTreeChangesTest
{
    @TempDir
    Path tempDir;

    private Path repositoryDirectory;
    private Repository repository;

    @BeforeEach
    public void createRepository() throws Exception
    {
        repositoryDirectory = Files.createDirectories(tempDir.resolve("repository"));
        repository = Git.init().setDirectory(repositoryDirectory.toFile()).setInitialBranch("main").call().getRepository();
    }
    @AfterEach
    public void closeRepository()
    {
        repository.close();
    }

    @Test
    public void changedFiles_should_return_nothing_when_the_working_tree_is_clean() throws Exception
    {
        commitAFile("Committed.java");

        assertTrue(changes().changedFiles().isEmpty());
    }

    @Test
    public void changedFiles_should_report_a_modified_file() throws Exception
    {
        commitAFile("Committed.java");
        writeFile("Committed.java", "changed");

        assertEquals(Set.of(file("Committed.java")), pathsOf(changes().changedFiles()));
    }

    @Test
    public void changedFiles_should_report_an_untracked_file() throws Exception
    {
        commitAFile("Committed.java");
        writeFile("Untracked.java", "new");

        assertEquals(Set.of(file("Untracked.java")), pathsOf(changes().changedFiles()));
    }

    /**
     * JGit reports the old path as removed and the new one as added: the
     * deleted file is filtered out later, when the changed files are matched
     * with the workspace files.
     */
    @Test
    public void changedFiles_should_report_the_new_name_of_a_renamed_file() throws Exception
    {
        commitAFile("OldName.java");
        Files.move(repositoryDirectory.resolve("OldName.java"), repositoryDirectory.resolve("NewName.java"));
        try (Git git = Git.open(repositoryDirectory.toFile()))
        {
            git.rm().addFilepattern("OldName.java").call();
            git.add().addFilepattern("NewName.java").call();
        }

        assertEquals(Set.of(file("NewName.java"), file("OldName.java")), pathsOf(changes().changedFiles()));
    }

    @Test
    public void changedFiles_should_report_a_staged_file() throws Exception
    {
        commitAFile("Committed.java");
        writeFile("Committed.java", "changed");
        Git.open(repositoryDirectory.toFile()).add().addFilepattern("Committed.java").call();

        assertEquals(Set.of(file("Committed.java")), pathsOf(changes().changedFiles()));
    }

    @Test
    public void changedFiles_should_report_a_deleted_file() throws Exception
    {
        commitAFile("Committed.java");
        Files.delete(repositoryDirectory.resolve("Committed.java"));

        assertEquals(Set.of(file("Committed.java")), pathsOf(changes().changedFiles()));
    }

    @Test
    public void workingTree_should_be_the_root_of_the_repository() throws Exception
    {
        assertEquals(repositoryDirectory.toAbsolutePath(), changes().workingTree());
    }

    @Test
    public void workingTree_should_reject_a_bare_repository() throws Exception
    {
        final Path bareDirectory = Files.createDirectories(tempDir.resolve("bare.git"));
        try (Repository bareRepository = Git.init().setDirectory(bareDirectory.toFile()).setBare(true).call().getRepository())
        {
            assertThrows(IOException.class, () -> new GitWorkingTreeChanges(bareRepository).workingTree());
        }
    }

    private GitWorkingTreeChanges changes()
    {
        return new GitWorkingTreeChanges(repository);
    }

    private Path file(String relativePath)
    {
        return repositoryDirectory.resolve(relativePath).toAbsolutePath();
    }

    private static Set<Path> pathsOf(Collection<Path> paths)
    {
        return paths.stream().map(path -> path.toAbsolutePath()).collect(Collectors.toSet());
    }

    private void commitAFile(String fileName) throws Exception
    {
        writeFile(fileName, "content");
        try (Git git = Git.open(repositoryDirectory.toFile()))
        {
            git.add().addFilepattern(fileName).call();
            git.commit().setMessage("add " + fileName).setAuthor("MoreUnit", "moreunit@example.org").call();
        }
    }

    private void writeFile(String fileName, String content) throws IOException
    {
        Files.writeString(repositoryDirectory.resolve(fileName), content, UTF_8);
    }
}
