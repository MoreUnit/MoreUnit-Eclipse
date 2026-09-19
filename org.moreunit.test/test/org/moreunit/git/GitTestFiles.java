package org.moreunit.git;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Deletes the temporary directories of the Git tests.
 * <p>
 * On Windows, JGit may keep a file handle for a short while after a repository
 * has been closed, which prevents the deletion of its directory: the deletion
 * is retried a few times, and a directory which still cannot be deleted is
 * left to the operating system instead of failing the test.
 * </p>
 */
final class GitTestFiles
{
    private static final int DELETE_ATTEMPTS = 5;

    private GitTestFiles()
    {
    }

    static void deleteRecursively(Path directory)
    {
        for (int attempt = 1; attempt <= DELETE_ATTEMPTS; attempt++)
        {
            if(deleteOnce(directory))
            {
                return;
            }
            pause(attempt);
        }
    }

    private static boolean deleteOnce(Path directory)
    {
        if(! Files.exists(directory))
        {
            return true;
        }
        try (Stream<Path> paths = Files.walk(directory))
        {
            paths.sorted(Comparator.reverseOrder()).forEach(GitTestFiles::deleteQuietly);
        }
        catch (final IOException e)
        {
            return false;
        }
        return ! Files.exists(directory);
    }

    private static void deleteQuietly(Path path)
    {
        try
        {
            Files.deleteIfExists(path);
        }
        catch (final IOException e)
        {
            // the deletion is retried by the caller
        }
    }

    private static void pause(int attempt)
    {
        try
        {
            Thread.sleep(100L * attempt);
        }
        catch (final InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}
