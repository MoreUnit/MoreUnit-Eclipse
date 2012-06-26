package org.moreunit.util;

import java.util.Map;

import org.eclipse.jdt.core.IMember;
import org.moreunit.core.util.LRUCache;

/**
 * An history to store and restore jumps made between members under tests and
 * test members (and vice versa).
 */
public class MemberJumpHistory
{

    private static final int HISTORY_SIZE = 100;

    private static class ThreadSafeInstanceHolder
    {
        private static final MemberJumpHistory INSTANCE = new MemberJumpHistory();
    }

    private final Map<IMember, IMember> jumps;

    public MemberJumpHistory()
    {
        jumps = new LRUCache<IMember, IMember>(HISTORY_SIZE);
    }

    public static MemberJumpHistory getInstance()
    {
        return ThreadSafeInstanceHolder.INSTANCE;
    }

    public synchronized void registerJump(IMember fromMember, IMember toMember)
    {
        jumps.put(fromMember, toMember);
        jumps.put(toMember, fromMember);
    }

    public synchronized IMember getLastCorrespondingJumpMember(IMember fromMember)
    {
        return jumps.get(fromMember);
    }

}
