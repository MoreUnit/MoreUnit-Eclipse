package org.moreunit.core.extension.jump;

public final class JumpResult
{
    private final boolean done;

    public static JumpResult done()
    {
        return new JumpResult(true);
    }

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
