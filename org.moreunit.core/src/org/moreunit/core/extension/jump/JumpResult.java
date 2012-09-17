package org.moreunit.core.extension.jump;

public final class JumpResult
{
    private final boolean done;

    /**
     * The jump action is "done", meaning that there is no more action to take
     * (it either succeeded or was cancelled).
     */
    public static JumpResult done()
    {
        return new JumpResult(true);
    }

    /**
     * The jump action is still to be done.
     */
    public static JumpResult notDone()
    {
        return new JumpResult(false);
    }

    private JumpResult(boolean done)
    {
        this.done = done;
    }

    public boolean isDone()
    {
        return done;
    }
}
