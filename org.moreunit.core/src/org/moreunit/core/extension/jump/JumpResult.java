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

    @Override
    public int hashCode()
    {
        return Boolean.hashCode(done);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass())
            return false;
        JumpResult other = (JumpResult) obj;
        return done == other.done;
    }
}
