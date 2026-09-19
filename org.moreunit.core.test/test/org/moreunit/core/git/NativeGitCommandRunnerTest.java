package org.moreunit.core.git;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Exercises {@link NativeGitCommandRunner} against a real (temporary) Git
 * repository. Tests are skipped when Git is not installed.
 */
public class NativeGitCommandRunnerTest
{
    @TempDir
    Path tempDir;

    private Path repository;

    @BeforeEach
    public void createRepository() throws Exception
    {
        assumeTrue(gitIsAvailable(), "Git is not installed");
        repository = Files.createDirectories(tempDir.resolve("repository"));
        runGit("init", "-q");
        runGit("config", "user.email", "moreunit@example.org");
        runGit("config", "user.name", "MoreUnit");
    }

    @Test
    public void should_report_modified_and_untracked_files() throws Exception
    {
        commitAFile("Committed.java");
        Files.writeString(repository.resolve("Committed.java"), "changed", UTF_8);
        Files.writeString(repository.resolve("Untracked.java"), "new", UTF_8);

        final GitWorkingTree tree = new GitWorkingTree(repository, NativeGitCommandRunner.INSTANCE);

        assertEquals(List.of(Path.of("Committed.java"), Path.of("Untracked.java")), tree.changedFiles());
    }

    @Test
    public void should_report_the_new_name_of_a_renamed_file() throws Exception
    {
        commitAFile("OldName.java");
        runGit("mv", "OldName.java", "NewName.java");

        final GitWorkingTree tree = new GitWorkingTree(repository, NativeGitCommandRunner.INSTANCE);

        assertEquals(List.of(Path.of("NewName.java")), tree.changedFiles());
    }

    @Test
    public void should_fail_when_the_command_is_invalid()
    {
        final IOException failure = assertThrows(IOException.class, () -> NativeGitCommandRunner.INSTANCE.run(repository, "not-a-git-command"));

        assertTrue(failure.getMessage().contains("not-a-git-command"), failure.getMessage());
    }

    private void commitAFile(String fileName) throws Exception
    {
        Files.writeString(repository.resolve(fileName), "content", UTF_8);
        runGit("add", fileName);
        runGit("commit", "-q", "-m", "add " + fileName);
    }

    private void runGit(String... arguments) throws Exception
    {
        final java.util.List<String> command = new java.util.ArrayList<>();
        command.add("git");
        command.addAll(List.of(arguments));
        final Process process = new ProcessBuilder(command).directory(repository.toFile()).redirectErrorStream(true).start();
        final String output = new String(process.getInputStream().readAllBytes(), UTF_8);
        if(process.waitFor() != 0)
        {
            throw new IllegalStateException("git " + String.join(" ", arguments) + " failed: " + output);
        }
    }

    private static boolean gitIsAvailable()
    {
        try
        {
            return new ProcessBuilder("git", "--version").start().waitFor() == 0;
        }
        catch (final Exception e)
        {
            return false;
        }
    }
}
