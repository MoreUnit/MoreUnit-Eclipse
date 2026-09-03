package org.moreunit.core.extension.jump;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class JumpResultTest
{
    @Test
    public void done_should_return_done_result()
    {
        final JumpResult result = JumpResult.done();

        assertTrue(result.isDone());
    }

    @Test
    public void notDone_should_return_not_done_result()
    {
        final JumpResult result = JumpResult.notDone();

        assertFalse(result.isDone());
    }

    @Test
    public void isDone_should_return_true_for_done_result()
    {
        final JumpResult result = JumpResult.done();

        assertTrue(result.isDone());
    }

    @Test
    public void isDone_should_return_false_for_not_done_result()
    {
        final JumpResult result = JumpResult.notDone();

        assertFalse(result.isDone());
    }
}