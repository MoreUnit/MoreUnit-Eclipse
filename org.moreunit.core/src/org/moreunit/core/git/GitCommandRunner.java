package org.moreunit.core.git;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Runs a Git command in a given directory and returns what it wrote on its
 * standard output stream.
 */
public interface GitCommandRunner
{
    /**
     * @param workingDirectory the directory in which the command is run
     * @param arguments the Git arguments, e.g. {@code "status"},
     *            {@code "--porcelain=v1"}
     * @return the raw standard output of the command
     * @throws IOException if Git could not be run, failed, or took too long
     */
    byte[] run(Path workingDirectory, String... arguments) throws IOException;
}
