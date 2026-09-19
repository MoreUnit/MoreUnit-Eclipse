package org.moreunit.git;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * The Git bundles are part of the target platform, so they are installed in
 * the test runtime.
 */
public class GitSupportTest
{
    @Test
    public void should_detect_the_eclipse_git_implementation()
    {
        assertTrue(GitSupport.isAvailable(), "JGit should be installed in the test runtime");
    }

    @Test
    public void should_not_detect_an_unknown_bundle()
    {
        assertFalse(GitSupport.isAvailable("org.moreunit.does.not.exist"));
    }
}
