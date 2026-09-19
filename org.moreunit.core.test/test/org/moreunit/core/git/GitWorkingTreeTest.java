package org.moreunit.core.git;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class GitWorkingTreeTest
{
    @TempDir
    Path tempDir;

    @Test
    public void locate_should_find_working_tree_from_nested_directory() throws Exception
    {
        final Path repository = Files.createDirectories(tempDir.resolve("repository"));
        Files.createDirectory(repository.resolve(".git"));
        final Path nestedDirectory = Files.createDirectories(repository.resolve("sub/directory"));

        final Optional<GitWorkingTree> tree = GitWorkingTree.locate(nestedDirectory);

        assertTrue(tree.isPresent());
        assertEquals(repository.toAbsolutePath(), tree.get().root());
    }

    @Test
    public void locate_should_accept_a_git_file_as_created_by_linked_worktrees() throws Exception
    {
        final Path repository = Files.createDirectories(tempDir.resolve("worktree"));
        Files.writeString(repository.resolve(".git"), "gitdir: /somewhere/else\n");

        final Optional<GitWorkingTree> tree = GitWorkingTree.locate(repository);

        assertTrue(tree.isPresent());
        assertEquals(repository.toAbsolutePath(), tree.get().root());
    }

    @Test
    public void locate_should_return_empty_when_no_git_entry_exists() throws Exception
    {
        final Path directory = Files.createDirectories(tempDir.resolve("not/a/repository"));

        assertTrue(GitWorkingTree.locate(directory).isEmpty());
    }

    @Test
    public void changedFiles_should_return_paths_relative_to_the_working_tree() throws Exception
    {
        final Path repository = Files.createDirectories(tempDir.resolve("repository"));
        final RecordingGitCommandRunner runner = new RecordingGitCommandRunner(" M src/Foo.java\0?? src/Bar.java\0");
        final GitWorkingTree tree = new GitWorkingTree(repository, runner);

        final List<Path> changedFiles = tree.changedFiles();

        assertEquals(List.of(Path.of("src/Foo.java"), Path.of("src/Bar.java")), changedFiles);
        assertEquals(repository.toAbsolutePath(), runner.workingDirectory);
        assertEquals(Arrays.asList("status", "--porcelain=v1", "-z", "--untracked-files=all"), runner.arguments);
    }

    @Test
    public void changedFiles_should_return_the_new_path_of_renamed_files() throws Exception
    {
        final GitWorkingTree tree = new GitWorkingTree(tempDir, new RecordingGitCommandRunner("R  src/New.java\0src/Old.java\0 M src/Other.java\0"));

        final List<Path> changedFiles = tree.changedFiles();

        assertEquals(List.of(Path.of("src/New.java"), Path.of("src/Other.java")), changedFiles);
    }

    @Test
    public void changedFiles_should_keep_spaces_in_file_names() throws Exception
    {
        final GitWorkingTree tree = new GitWorkingTree(tempDir, new RecordingGitCommandRunner("?? src/Some File.java\0"));

        assertEquals(List.of(Path.of("src/Some File.java")), tree.changedFiles());
    }

    @Test
    public void changedFiles_should_report_git_failures() throws Exception
    {
        final GitWorkingTree tree = new GitWorkingTree(tempDir, (workingDirectory, arguments) -> {
            throw new IOException("git is not installed");
        });

        final IOException failure = assertThrows(IOException.class, tree::changedFiles);
        assertEquals("git is not installed", failure.getMessage());
    }

    @Test
    public void parsePorcelainOutput_should_reject_truncated_output()
    {
        assertThrows(IllegalArgumentException.class, () -> GitWorkingTree.parsePorcelainOutput(" M src/Foo.java".getBytes(UTF_8)));
    }

    @Test
    public void parsePorcelainOutput_should_accept_empty_output()
    {
        assertTrue(GitWorkingTree.parsePorcelainOutput(new byte[0]).isEmpty());
    }

    private static class RecordingGitCommandRunner implements GitCommandRunner
    {
        private final String output;
        private Path workingDirectory;
        private List<String> arguments;

        RecordingGitCommandRunner(String output)
        {
            this.output = output;
        }

        @Override
        public byte[] run(Path directory, String... args)
        {
            workingDirectory = directory;
            arguments = new ArrayList<>(Arrays.asList(args));
            return output.getBytes(UTF_8);
        }
    }
}
