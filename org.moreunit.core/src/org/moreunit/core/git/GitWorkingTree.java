package org.moreunit.core.git;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The Git working tree which contains a given directory. It gives access to
 * the files which changed since the last commit.
 */
public final class GitWorkingTree
{
    private static final String GIT_ENTRY = ".git";

    private final Path root;
    private final GitCommandRunner commandRunner;

    public GitWorkingTree(Path root, GitCommandRunner commandRunner)
    {
        this.root = root.toAbsolutePath().normalize();
        this.commandRunner = commandRunner;
    }

    /**
     * Returns the Git working tree containing the given directory, if there is
     * one. The search goes up through the parent directories, so the given
     * directory does not have to be the root of the working tree.
     *
     * @param directory any directory
     * @return the working tree, or empty if no {@code .git} entry was found
     */
    public static Optional<GitWorkingTree> locate(Path directory)
    {
        return locate(directory, NativeGitCommandRunner.INSTANCE);
    }

    public static Optional<GitWorkingTree> locate(Path directory, GitCommandRunner commandRunner)
    {
        for (Path current = directory.toAbsolutePath().normalize(); current != null; current = current.getParent())
        {
            // ".git" is a directory in regular clones, and a file in worktrees
            if(Files.exists(current.resolve(GIT_ENTRY)))
            {
                return Optional.of(new GitWorkingTree(current, commandRunner));
            }
        }
        return Optional.empty();
    }

    /**
     * @return the root directory of this working tree
     */
    public Path root()
    {
        return root;
    }

    /**
     * Returns the files which differ from the last commit, whether their
     * changes are staged or not, including untracked files. Deleted files are
     * part of the result.
     *
     * @return paths relative to {@link #root()}
     * @throws IOException if Git could not be run or failed
     */
    public List<Path> changedFiles() throws IOException
    {
        return parsePorcelainOutput(commandRunner.run(root, "status", "--porcelain=v1", "-z", "--untracked-files=all"));
    }

    /**
     * Parses the output of {@code git status --porcelain=v1 -z}.
     * <p>
     * With {@code -z}, each entry is a two-character status, a space, the path
     * of the file and a NUL character. Renamed and copied files are followed
     * by a second NUL-terminated path: the path the file had before the
     * operation, which is not returned here.
     *
     * @param output the raw output of Git, using UTF-8 encoded paths
     * @return the paths of the changed files, relative to the working tree
     */
    public static List<Path> parsePorcelainOutput(byte[] output)
    {
        final String text = new String(output, StandardCharsets.UTF_8);
        final List<Path> files = new ArrayList<>();

        int index = 0;
        while(index + 3 <= text.length())
        {
            final char status = text.charAt(index);
            final char subStatus = text.charAt(index + 1);
            final char separator = text.charAt(index + 2);
            if(separator != ' ')
            {
                throw new IllegalArgumentException("Unexpected Git status output: " + text);
            }
            index += 3;

            final int pathEnd = text.indexOf('\0', index);
            if(pathEnd < 0)
            {
                throw new IllegalArgumentException("Truncated Git status output: " + text);
            }
            files.add(Path.of(text.substring(index, pathEnd)));
            index = pathEnd + 1;

            if(isRenameOrCopy(status) || isRenameOrCopy(subStatus))
            {
                // the previous path follows the new one, skip it
                final int previousPathEnd = text.indexOf('\0', index);
                if(previousPathEnd < 0)
                {
                    throw new IllegalArgumentException("Truncated Git status output: " + text);
                }
                index = previousPathEnd + 1;
            }
        }

        if(index != text.length())
        {
            throw new IllegalArgumentException("Truncated Git status output: " + text);
        }
        return Collections.unmodifiableList(files);
    }

    private static boolean isRenameOrCopy(char status)
    {
        return status == 'R' || status == 'C';
    }
}
