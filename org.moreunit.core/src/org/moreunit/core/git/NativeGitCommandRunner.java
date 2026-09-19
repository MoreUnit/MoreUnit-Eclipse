package org.moreunit.core.git;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A {@link GitCommandRunner} that launches the {@code git} executable found in
 * the {@code PATH}.
 */
public class NativeGitCommandRunner implements GitCommandRunner
{
    public static final NativeGitCommandRunner INSTANCE = new NativeGitCommandRunner();

    /** Git commands are local and fast: beyond this delay, something is wrong. */
    private static final long TIMEOUT_SECONDS = 30;

    /**
     * Prevents Git from writing to the index while other tools (such as an IDE)
     * may be working with it.
     */
    private static final String NO_OPTIONAL_LOCKS = "--no-optional-locks";

    @Override
    public byte[] run(Path workingDirectory, String... arguments) throws IOException
    {
        final List<String> command = new ArrayList<>();
        command.add("git");
        command.add(NO_OPTIONAL_LOCKS);
        command.addAll(Arrays.asList(arguments));
        final String readableCommand = String.join(" ", command);

        final Process process;
        try
        {
            process = new ProcessBuilder(command).directory(workingDirectory.toFile()).start();
        }
        catch (final IOException e)
        {
            throw new IOException("Could not run '" + readableCommand + "'. Is Git installed and in the PATH?", e);
        }

        try
        {
            final ByteArrayOutputStream errors = new ByteArrayOutputStream();
            final Thread errorDrainer = drainErrorStreamInBackground(process.getErrorStream(), errors);
            final byte[] output = process.getInputStream().readAllBytes();

            if(! process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS))
            {
                process.destroyForcibly();
                throw new IOException("Git command timed out: " + readableCommand);
            }
            errorDrainer.join();

            if(process.exitValue() != 0)
            {
                throw new IOException("Git command failed with exit code " + process.exitValue() + ": " + readableCommand + " - " + errors.toString(StandardCharsets.UTF_8));
            }
            return output;
        }
        catch (final InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while running: " + readableCommand, e);
        }
    }

    /**
     * Reads the error stream of the process while its output is being read, so
     * that the process never blocks on a full pipe.
     */
    private static Thread drainErrorStreamInBackground(InputStream errorStream, OutputStream errors)
    {
        final Thread drainer = new Thread(() -> transferQuietly(errorStream, errors), "MoreUnit Git error drainer");
        drainer.setDaemon(true);
        drainer.start();
        return drainer;
    }

    private static void transferQuietly(InputStream input, OutputStream output)
    {
        try
        {
            input.transferTo(output);
        }
        catch (final IOException e)
        {
            // the process ended: there is nothing left to read
        }
    }
}
